package com.martinbechtle.jcanary.api;

/**
 * @author Martin Bechtle
 */
public enum DependencyImportance {

    /**
     * The dependency is of primary importance, as the service cannot function without it. Example: the primary
     * data store.
     */
    PRIMARY,

    /**
     * The dependency is important for the service, as the service will not function properly, but is not strictly
     * necessary for every business function. Example: a remote API, or mis-configuration of the service.
     */
    SECONDARY,

    /**
     * The service can function properly for some time without this dependency, or it can degrade gracefully.
     * Example: some batch job did not run in the last X hours, or some analytics stream is unresponsive.
     */
    MANAGEABLE
}
