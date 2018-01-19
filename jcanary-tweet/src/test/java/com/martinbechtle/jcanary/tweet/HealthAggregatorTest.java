package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

import static com.martinbechtle.jcanary.tweet.HealthAggregator.UNCAUGTHT_EXCEPTION_ERRMSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for {@link HealthAggregator}
 *
 * @author Martin Bechtle
 */
@DisplayName("Health Aggregator")
@SuppressWarnings("squid:S2187") // sonar does not recognise this as a unit test as all tests are nested
class HealthAggregatorTest {

    private HealthAggregator healthAggregator = new HealthAggregator(Clock.systemDefaultZone());

    @Nested
    @DisplayName("register(HealthMonitor)")
    class RegisterTest {

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
    }

    @Nested
    @DisplayName("collect(HealthMonitor)")
    class CollectTest {

        @Test
        @DisplayName("should aggregate results from all registered monitors")
        void collectOk() {

            healthAggregator
                    .register(new TestHealthMonitorWithDescriptor())
                    .register(new TestHealthMonitorWithCriticalStatus());

            List<HealthTweet> expectedTweets = Arrays.asList(
                    new HealthTweet(defaultDependency("monitor"), HealthResult.ok(), 0),
                    new HealthTweet(defaultDependency("failingMonitor"), criticalResult(), 0)
            );

            List<HealthTweet> actualTweets = healthAggregator.collect();

            assertEquals(expectedTweets, actualTweets);
        }

        @Test
        @DisplayName("should catch any exception thrown by monitors and show as a unknown health, and still process any other monitor")
        void collectException() {

            healthAggregator
                    .register(new TestHealthMonitorWithDescriptor())
                    .register(new TestHealthMonitorThrowing());

            List<HealthTweet> expectedTweets = Arrays.asList(
                    new HealthTweet(defaultDependency("monitor"), HealthResult.ok(), 0),
                    new HealthTweet(defaultDependency("exceptionThrowingMonitor"), unknownResult(), 0)
            );

            List<HealthTweet> actualTweets = healthAggregator.collect();

            assertEquals(expectedTweets, actualTweets);
        }
    }

    /**
     * returns a {@link Dependency} with the default values of {@link HealthTweetDescriptor}
     */
    private static Dependency defaultDependency(String name) {

        return new Dependency(DependencyImportance.PRIMARY, DependencyType.RESOURCE, name);
    }

    private static HealthResult criticalResult() {

        return HealthResult.of(DependencyStatus.CRITICAL, "Status is critical");
    }

    private static HealthResult unknownResult() {

        return HealthResult.of(DependencyStatus.UNKNOWN, UNCAUGTHT_EXCEPTION_ERRMSG);
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

    @HealthTweetDescriptor(name = "failingMonitor")
    private static class TestHealthMonitorWithCriticalStatus implements HealthMonitor {

        @Override
        public HealthResult check() {

            return criticalResult();
        }
    }

    @HealthTweetDescriptor(name = "exceptionThrowingMonitor")
    private static class TestHealthMonitorThrowing implements HealthMonitor {

        @Override
        public HealthResult check() {

            throw new RuntimeException();
        }
    }

}