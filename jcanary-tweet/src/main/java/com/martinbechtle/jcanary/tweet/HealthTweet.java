package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.Dependency;

import static com.martinbechtle.jrequire.Require.notNull;

/**
 * Final result of a health check. Pairs a {@link HealthResult} with the {@link Dependency} under test.
 *
 * @author Martin Bechtle
 */
public class HealthTweet {

    private final Dependency dependency;

    private final HealthResult result;

    public HealthTweet(Dependency dependency, HealthResult result) {

        this.dependency = notNull(dependency, "dependency");
        this.result = notNull(result, "result");
    }

    public Dependency getDependency() {

        return dependency;
    }

    public HealthResult getResult() {

        return result;
    }

}
