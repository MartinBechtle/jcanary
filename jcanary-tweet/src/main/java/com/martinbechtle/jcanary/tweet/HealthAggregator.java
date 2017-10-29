package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.DependencyStatus;
import com.martinbechtle.jrequire.Require;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;

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

    static final String UNCAUGTHT_EXCEPTION_ERRMSG = "Error while trying to compute status";

    public HealthAggregator(Clock clock) {

        this.clock = clock;
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

        Require.notNull(monitor);

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

        return healthTweets.values()
                .stream()
                .map(healthTweeter -> {
                    try {
                        return healthTweeter.tweet();
                    }
                    catch (RuntimeException e) {
                        return new HealthTweet(
                                healthTweeter.getDependency(),
                                HealthResult.of(DependencyStatus.UNKNOWN, UNCAUGTHT_EXCEPTION_ERRMSG));
                    }
                })
                .collect(toList());
    }

}
