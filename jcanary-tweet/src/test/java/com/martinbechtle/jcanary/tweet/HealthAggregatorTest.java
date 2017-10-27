package com.martinbechtle.jcanary.tweet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for {@link HealthAggregator}
 *
 * @author Martin Bechtle
 */
@DisplayName("Health Aggregator")
class HealthAggregatorTest {

    private HealthAggregator healthAggregator = new HealthAggregator();

    @Test
    @DisplayName("should throw exception when registering a monitor without descriptor")
    void registerWithoutDescriptor() {

        assertThrows(IllegalArgumentException.class,
                () -> healthAggregator.register(new TestHealthMonitorWithoutDescriptor()));
    }

    @Test
    @DisplayName("should return self instance when registering a monitor with descriptor")
    void registerWithDescriptor() {

        HealthAggregator result = healthAggregator.register(new TestHealthMonitorWithDescriptor());
        assertEquals(result, healthAggregator);
    }

    @Test
    @DisplayName("should throw exception when registering two monitors with the same name")
    void registerDuplicate() {

        assertThrows(IllegalArgumentException.class, () -> healthAggregator
                .register(new TestHealthMonitorWithoutDescriptor())
                .register(new TestHealthMonitorWithDescriptor()));
    }

    @Test
    void collect() {

        // TODO
    }

    private static class TestHealthMonitorWithoutDescriptor implements HealthMonitor {

        @Override
        public HealthResult check() {

            return HealthResult.ok();
        }
    }

    @HealthTweetDescriptor(name = "monitor")
    private static class TestHealthMonitorWithDescriptor implements HealthMonitor {

        @Override
        public HealthResult check() {

            return HealthResult.ok();
        }
    }

}