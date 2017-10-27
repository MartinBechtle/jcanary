package com.martinbechtle.jcanary.api;

/**
 * @author Martin Bechtle
 */
public enum DependencyStatus {

    /**
     * Dependency is perfectly healthy.
     */
    HEALTHY,

    /**
     * Dependency status is unknown. Use this to indicate that an error occurred while trying to compute the status
     * of this dependency, which might not necessarily mean that the dependency is not working.
     */
    UNKNOWN,

    /**
     * Dependency is potentially unhealthy, but not completely degraded. Example: a database might have a high latency.
     */
    DEGRADED,

    /**
     * Dependency is broken. Example: remote api unreachable.
     */
    CRITICAL
}
