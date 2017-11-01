package com.martinbechtle.jcanary.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Optional;

import static com.martinbechtle.jrequire.Require.notNull;

/**
 * Result returned by {@link HealthMonitor}
 *
 * @author Martin Bechtle
 */
public class HealthResult {

    private final DependencyStatus status;

    private final String statusText;

    private HealthResult(DependencyStatus status, String statusText) {

        this.status = status;
        this.statusText = statusText;
    }

    public static HealthResult of(DependencyStatus status, String statusText) {

        return new HealthResult(
                notNull(status, "status"),
                Optional.ofNullable(statusText).orElse(""));
    }

    public static HealthResult ok() {

        return HealthResult.of(DependencyStatus.HEALTHY, null);
    }

    public DependencyStatus getStatus() {

        return status;
    }

    public String getStatusText() {

        return statusText;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof HealthResult)) {
            return false;
        }

        HealthResult that = (HealthResult) o;

        return new EqualsBuilder()
                .append(status, that.status)
                .append(statusText, that.statusText)
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .append(status)
                .append(statusText)
                .toHashCode();
    }

    @Override
    public String toString() {

        return "HealthResult{" +
                "status=" + status +
                ", statusText='" + statusText + '\'' +
                '}';
    }
}
