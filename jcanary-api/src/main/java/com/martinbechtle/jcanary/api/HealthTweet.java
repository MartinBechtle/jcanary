package com.martinbechtle.jcanary.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.martinbechtle.jrequire.Require.notNull;

/**
 * Final result of a health check. Pairs a {@link HealthResult} with the {@link Dependency} under test.
 *
 * @author Martin Bechtle
 */
public class HealthTweet {

    private Dependency dependency;

    private HealthResult result;

    public HealthTweet(Dependency dependency, HealthResult result) {

        this.dependency = notNull(dependency, "dependency");
        this.result = notNull(result, "result");
    }

    public Dependency getDependency() {

        return dependency;
    }

    public HealthTweet setDependency(Dependency dependency) {

        this.dependency = dependency;
        return this;
    }

    public HealthResult getResult() {

        return result;
    }

    public HealthTweet setResult(HealthResult result) {

        this.result = result;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof HealthTweet)) {
            return false;
        }

        HealthTweet that = (HealthTweet) o;

        return new EqualsBuilder()
                .append(dependency, that.dependency)
                .append(result, that.result)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .append(dependency)
                .append(result)
                .toHashCode();
    }

    @Override
    public String toString() {

        return "HealthTweet{" +
                "dependency=" + dependency +
                ", result=" + result +
                '}';
    }
}
