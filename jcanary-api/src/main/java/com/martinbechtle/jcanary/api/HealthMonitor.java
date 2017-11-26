package com.martinbechtle.jcanary.api;

/**
 * To be implemented by classes that perform health checks.
 *
 * @author Martin Bechtle
 */
public interface HealthMonitor {

    HealthResult check();
}
