package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.DependencyStatus;

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
}
