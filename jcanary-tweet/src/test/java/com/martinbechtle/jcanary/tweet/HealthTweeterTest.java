package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.DependencyStatus;
import com.martinbechtle.jcanary.api.HealthMonitor;
import com.martinbechtle.jcanary.api.HealthResult;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.Clock;

import static org.mockito.Mockito.*;

/**
 * Unit test for {@link HealthTweeter}
 *
 * @author Martin Bechtle
 */
@SuppressWarnings("squid:S2187") // sonar does not recognise this as a unit test as all tests are nested
class HealthTweeterTest {

    @Nested
    @DisplayName("tweet()")
    class Tweet {

        @Test
        @DisplayName("should invoke monitor on every call when secondsToLive is negative")
        void tweetUncached() {

            HealthMonitor nonCachingMonitor = Mockito.spy(new NonCachingHealthMonitor());
            HealthTweeter healthTweeter = new HealthTweeter(nonCachingMonitor, Clock.systemDefaultZone());
            healthTweeter.tweet();
            healthTweeter.tweet();
            verify(nonCachingMonitor, times(2)).check();
        }

        @Test
        @DisplayName("should invoke monitor only once on subsequent calls when secondsToLive is positive")
        void tweetCached() {

            HealthMonitor nonCachingMonitor = Mockito.spy(new CachingHealthMonitor());
            HealthTweeter healthTweeter = new HealthTweeter(nonCachingMonitor, Clock.systemDefaultZone());
            healthTweeter.tweet();
            healthTweeter.tweet();
            verify(nonCachingMonitor, times(1)).check();
        }

        @Test
        @DisplayName("should invoke monitor twice on two calls when secondsToLive is positive and such amount of seconds has already passed by the time of the second invocation")
        void tweetCacheExpired() {

            Clock clock = mock(Clock.class);
            when(clock.millis()).thenReturn(0L);

            HealthMonitor nonCachingMonitor = Mockito.spy(new CachingHealthMonitor());
            HealthTweeter healthTweeter = new HealthTweeter(nonCachingMonitor, clock);
            healthTweeter.tweet();

            reset(clock);
            when(clock.millis()).thenReturn(200_000L);

            healthTweeter.tweet();
            verify(nonCachingMonitor, times(2)).check();
        }

        @Test
        @DisplayName("should invoke monitor again if {secondsToLive} seconds have not yet passed, but {secondsToLiveIfUnhealth} seconds have passed and previous result was unhealthy")
        void tweetUnhealthyCacheExpired() {

            Clock clock = mock(Clock.class);
            when(clock.millis()).thenReturn(0L);

            HealthMonitor nonCachingMonitor = Mockito.spy(new UnhealthyCachingHealthMonitor());
            HealthTweeter healthTweeter = new HealthTweeter(nonCachingMonitor, clock);
            healthTweeter.tweet();

            reset(clock);
            when(clock.millis()).thenReturn(20_000L);

            healthTweeter.tweet();
            verify(nonCachingMonitor, times(2)).check();
        }
    }

    @HealthTweetDescriptor(name = "monitor", secondsToLive = -1)
    private static class NonCachingHealthMonitor implements HealthMonitor {

        @Override
        public HealthResult check() {

            return HealthResult.ok();
        }
    }

    @HealthTweetDescriptor(name = "monitor", secondsToLive = 100)
    private static class CachingHealthMonitor implements HealthMonitor {

        @Override
        public HealthResult check() {

            return HealthResult.ok();
        }
    }

    @HealthTweetDescriptor(name = "monitor", secondsToLive = 100, secondsToLiveIfUnhealthy = 10)
    private static class UnhealthyCachingHealthMonitor implements HealthMonitor {

        @Override
        public HealthResult check() {

            return HealthResult.of(DependencyStatus.CRITICAL, "I eat unhealthy food");
        }
    }

}