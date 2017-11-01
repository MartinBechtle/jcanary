package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.tweet.HealthAggregator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuration for integration tests
 *
 * @author Martin Bechtle
 */
@SpringBootApplication
public class IntegrationTestConfig {

    @Bean(name = "canaryHealthAggregator")
    public HealthAggregator healthAggregator() {

        return new HealthAggregator(Clock.systemDefaultZone())
                .register(new DummyHealthMonitor());
    }
}
