package com.martinbechtle.jcanary.tweet;

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
        @DisplayName("should invoke monitor twice on two calls when secondsToLive is positive and such amount of seconds as already passed by the time of the second invocation")
        void tweetCacheExpired() {

            Clock clock = mock(Clock.class);
            when(clock.millis()).thenReturn(0L);

            HealthMonitor nonCachingMonitor = Mockito.spy(new CachingHealthMonitor());
            HealthTweeter healthTweeter = new HealthTweeter(nonCachingMonitor, clock);
            healthTweeter.tweet();

            reset(clock);
            when(clock.millis()).thenReturn(200000L);

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

}