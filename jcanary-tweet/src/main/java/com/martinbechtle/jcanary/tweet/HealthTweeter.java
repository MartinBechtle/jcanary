package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.Dependency;

import static com.martinbechtle.jrequire.Require.notNull;
import static java.util.Arrays.stream;

/**
 * Component responsible for producing {@link HealthTweet}s
 *
 * @author Martin Bechtle
 */
public class HealthTweeter {

    private final HealthMonitor monitor;
    private final Dependency dependency;

    public HealthTweeter(HealthMonitor monitor) {

        this.monitor = notNull(monitor, "monitor");

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
    }

    public HealthTweet tweet() {

        // TODO implement time to live
        HealthResult result = monitor.check();
        return new HealthTweet(dependency, result);
    }

    public String getName() {

        return dependency.getName();
    }

    public Dependency getDependency() {

        return dependency;
    }
}
