package com.martinbechtle.jcanary.api;

/**
 * @author Martin Bechtle
 */
public enum CanaryResult {

    /**
     * When the service negated access to the canary because of authentication failure
     */
    FORBIDDEN,

    /**
     * When the service encountered an error trying to produce a canary
     */
    ERROR,

    /**
     * When the canary information was successfully produced, independently from the status of the service,
     * that is whether the service has any failing health checks
     */
    OK
}
