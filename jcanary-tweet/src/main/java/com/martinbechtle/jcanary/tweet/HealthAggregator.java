package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.DependencyStatus;
import com.martinbechtle.jcanary.api.HealthMonitor;
import com.martinbechtle.jcanary.api.HealthResult;
import com.martinbechtle.jcanary.api.HealthTweet;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static com.martinbechtle.jrequire.Require.notNull;
import static java.util.stream.Collectors.toList;

/**
 * Aggregates many different {@link HealthMonitor}s.
 * <p>
 * You would typically use this as a singleton, registering all monitors on it and invoking it regularly from your
 * canary endpoint as a health check.
 *
 * @author Martin Bechtle
 */
public class HealthAggregator {

    private final LinkedHashMap<String, HealthTweeter> healthTweets;

    private final Clock clock;

    private final ForkJoinPool forkJoinPool;

    static final String UNCAUGTHT_EXCEPTION_ERRMSG = "Error while trying to compute status";

    /**
     * @param clock use {@link Clock#systemDefaultZone()} unless you have specific needs
     * @param forkJoinPool a thread pool to be used for parallel processing of health monitors, please set it up
     *                   with a maximum number of threads proportional to the number of health checks and their
     *                   expected duration
     */
    public HealthAggregator(Clock clock, ForkJoinPool forkJoinPool) {

        this.clock = notNull(clock);
        this.forkJoinPool = notNull(forkJoinPool);
        this.healthTweets = new LinkedHashMap<>();
    }

    /**
     * Register a {@link HealthMonitor}
     *
     * @param monitor mandatory
     * @return an instance of this object for method chaining
     * @throws IllegalArgumentException if a {@link HealthMonitor} with such name was already registered
     */
    public HealthAggregator register(HealthMonitor monitor) {

        notNull(monitor);

        HealthTweeter healthTweeter = new HealthTweeter(monitor, clock);
        String healthTweeterName = healthTweeter.getName();

        if (healthTweets.containsKey(healthTweeterName)) {
            throw new IllegalArgumentException(
                    String.format("%s with name %s already registered",
                            HealthTweet.class,
                            healthTweeterName));
        }
        this.healthTweets.put(healthTweeterName, healthTweeter);
        return this;
    }

    /**
     * Invoke all {@link HealthMonitor}s and aggregate the data into a list.
     * The list is returned in the same order in which the {@link HealthMonitor}s were registered.
     *
     * @return a list of all {@link HealthTweet}s produced, potentially empty if no {@link HealthMonitor}s registered
     */
    public List<HealthTweet> collect() {

        return forkJoinPool.submit(() -> healthTweets.values()
                .parallelStream() // will use this fork-join pool instead of the common pool
                .map(this::tweet)
                .collect(toList()))
                .join();
    }

    private HealthTweet tweet(HealthTweeter healthTweeter) {

        long start = clock.millis();
        try {
            return healthTweeter.tweet();
        }
        catch (RuntimeException e) {
            long executionTimeMs = clock.millis() - start;
            return new HealthTweet(
                    healthTweeter.getDependency(),
                    HealthResult.of(DependencyStatus.UNKNOWN, UNCAUGTHT_EXCEPTION_ERRMSG),
                    executionTimeMs);
        }
    }

}
