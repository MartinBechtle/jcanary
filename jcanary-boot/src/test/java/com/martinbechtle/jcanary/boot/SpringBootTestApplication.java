package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.tweet.HealthAggregator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * @author Martin Bechtle
 */
@SpringBootApplication
@Configuration
public class SpringBootTestApplication {

    @Bean(name = "canaryHealthAggregator")
    public HealthAggregator healthAggregator() {

        return new HealthAggregator(Clock.systemDefaultZone())
                .register(new DummyHealthMonitor());
    }
}
