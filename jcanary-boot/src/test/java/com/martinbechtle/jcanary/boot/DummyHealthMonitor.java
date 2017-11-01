package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.api.HealthMonitor;
import com.martinbechtle.jcanary.api.HealthResult;
import com.martinbechtle.jcanary.tweet.HealthTweetDescriptor;

/**
 * @author Martin Bechtle
 */
@HealthTweetDescriptor(name = "dummyMonitor", secondsToLive = -1)
public class DummyHealthMonitor implements HealthMonitor {

    @Override
    public HealthResult check() {

        return HealthResult.ok();
    }
}
