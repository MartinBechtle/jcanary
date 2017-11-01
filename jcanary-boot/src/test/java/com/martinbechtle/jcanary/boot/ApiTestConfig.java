package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.tweet.HealthAggregator;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

/**
 * Test configuration for testing the API (controllers) in isolation, mocking dependencies
 *
 * @author Martin Bechtle
 */
@SpringBootApplication
public class ApiTestConfig {

    @Bean(name = "canaryHealthAggregator")
    public HealthAggregator healthAggregator() {

        return Mockito.mock(HealthAggregator.class);
    }
}
