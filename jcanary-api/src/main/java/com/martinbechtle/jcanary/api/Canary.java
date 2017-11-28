package com.martinbechtle.jcanary.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Aggregates all {@link HealthTweet}s of a given service
 *
 * @author Martin Bechtle
 */
public class Canary {

    private static final String UNKNOWN_SERVICE = "unknown-service";

    /**
     * The service for which this canary was produced
     */
    private String serviceName;

    /**
     * The result of the invocation to the canary endpoint on the service. See {@link CanaryResult}
     */
    private CanaryResult result;

    /**
     * List of all collected {@link HealthTweet}s.
     * Might be empty if no {@link HealthMonitor}s defined on the service or result is not {@link CanaryResult#OK}
     */
    private List<HealthTweet> tweets;

    Canary() {
        // for serialization
    }

    public Canary(String serviceName, CanaryResult result, List<HealthTweet> tweets) {

        this.serviceName = Optional.ofNullable(serviceName).orElse(UNKNOWN_SERVICE);
        this.result = notNull(result);
        this.tweets = Optional.ofNullable(tweets).orElse(emptyList());
    }

    public static Canary forbidden(String serviceName) {

        return new Canary(serviceName, CanaryResult.FORBIDDEN, emptyList());
    }

    public static Canary error(String serviceName) {

        return new Canary(serviceName, CanaryResult.ERROR, emptyList());
    }

    public static Canary ok(String serviceName, List<HealthTweet> healthTweets) {

        return new Canary(serviceName, CanaryResult.OK, healthTweets);
    }

    public String getServiceName() {

        return serviceName;
    }

    public Canary setServiceName(String serviceName) {

        this.serviceName = serviceName;
        return this;
    }

    public CanaryResult getResult() {

        return result;
    }

    public Canary setResult(CanaryResult result) {

        this.result = result;
        return this;
    }

    public List<HealthTweet> getTweets() {

        return tweets;
    }

    public Canary setTweets(List<HealthTweet> tweets) {

        this.tweets = tweets;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Canary)) {
            return false;
        }

        Canary canary = (Canary) o;

        return new EqualsBuilder()
                .append(serviceName, canary.serviceName)
                .append(result, canary.result)
                .append(tweets, canary.tweets)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .append(serviceName)
                .append(result)
                .append(tweets)
                .toHashCode();
    }
}
