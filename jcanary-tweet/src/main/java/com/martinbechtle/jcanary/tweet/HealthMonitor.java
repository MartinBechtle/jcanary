package com.martinbechtle.jcanary.tweet;

/**
 * To be implemented by classes that perform health checks.
 *
 * @author Martin Bechtle
 */
public interface HealthMonitor {

    HealthResult check();
}
