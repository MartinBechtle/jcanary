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

    private Long executionTimeMs;

    public HealthTweet() {
        // for serialization
    }

    public HealthTweet(Dependency dependency, HealthResult result, long executionTimeMs) {

        this.dependency = notNull(dependency, "dependency");
        this.result = notNull(result, "result");
        this.executionTimeMs = executionTimeMs;
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

    public Long getExecutionTimeMs() {

        return executionTimeMs;
    }

    public HealthTweet setExecutionTimeMs(Long executionTimeMs) {

        this.executionTimeMs = executionTimeMs;
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
                .append(executionTimeMs, that.executionTimeMs)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .append(dependency)
                .append(result)
                .append(executionTimeMs)
                .toHashCode();
    }

    @Override
    public String toString() {

        return "HealthTweet{" +
                "dependency=" + dependency +
                ", result=" + result +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}
