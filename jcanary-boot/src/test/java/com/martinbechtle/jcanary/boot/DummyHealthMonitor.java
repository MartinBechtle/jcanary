package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.tweet.HealthMonitor;
import com.martinbechtle.jcanary.tweet.HealthResult;
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
