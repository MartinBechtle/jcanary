package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.tweet.HealthAggregator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.util.concurrent.ForkJoinPool;

/**
 * Configuration for integration tests
 *
 * @author Martin Bechtle
 */
@SpringBootApplication
public class IntegrationTestConfig {

    @Bean(name = "canaryHealthAggregator")
    public HealthAggregator healthAggregator() {

        return new HealthAggregator(Clock.systemDefaultZone(), new ForkJoinPool(1))
                .register(new DummyHealthMonitor());
    }
}
