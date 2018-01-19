package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.Dependency;
import com.martinbechtle.jcanary.api.HealthMonitor;
import com.martinbechtle.jcanary.api.HealthResult;
import com.martinbechtle.jcanary.api.HealthTweet;

import java.time.Clock;
import java.util.Optional;

import static com.martinbechtle.jrequire.Require.notNull;
import static java.util.Arrays.stream;

/**
 * Component responsible for producing {@link HealthTweet}s.
 * 
 * Essentially wraps a {@link HealthMonitor} adding extra functionality such as catching exceptions and measuring
 * execution time.
 *
 * @author Martin Bechtle
 */
public class HealthTweeter {

    private final HealthMonitor monitor;
    private final Dependency dependency;
    private final Clock clock;
    private final long timeToLiveInMillis;

    private HealthTweet lastTweet;
    private Long nextComputeTimeMillis;


    public HealthTweeter(HealthMonitor monitor, Clock clock) {

        this.monitor = notNull(monitor, "monitor");
        this.clock = notNull(clock, "clock");

        Class klass = monitor.getClass();

        HealthTweetDescriptor descriptor = stream(klass.getAnnotationsByType(HealthTweetDescriptor.class))
                .findFirst()
                .map(annotation -> (HealthTweetDescriptor) annotation)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("%s of type %s requires a %s annotation",
                                HealthTweet.class.getSimpleName(),
                                klass.getSimpleName(),
                                HealthTweetDescriptor.class.getSimpleName()))
                );

        this.dependency = new Dependency(descriptor.importance(), descriptor.type(), descriptor.name());
        this.timeToLiveInMillis = descriptor.secondsToLive() * 1000L;
    }

    public HealthTweet tweet() {

        return getLastHealthTweetIfNotExpired()
                .orElseGet(() -> {
                    long start = clock.millis();
                    HealthResult result = monitor.check();
                    long executionTimeMs = clock.millis() - start;
                    HealthTweet healthTweet = new HealthTweet(dependency, result, executionTimeMs);
                    return setLastHealthTweet(healthTweet);
                });
    }

    private Optional<HealthTweet> getLastHealthTweetIfNotExpired() {

        long now = clock.millis();
        if (lastTweet != null && nextComputeTimeMillis != null && nextComputeTimeMillis > now) {
            return Optional.of(lastTweet);
        }
        return Optional.empty();
    }

    private HealthTweet setLastHealthTweet(HealthTweet healthTweet) {

        lastTweet = healthTweet;
        nextComputeTimeMillis = clock.millis() + timeToLiveInMillis;
        return lastTweet;
    }

    public String getName() {

        return dependency.getName();
    }

    public Dependency getDependency() {

        return dependency;
    }
}
