package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.DependencyImportance;
import com.martinbechtle.jcanary.api.DependencyType;
import com.martinbechtle.jcanary.api.HealthMonitor;

import java.lang.annotation.*;

/**
 * Mandatory configuration for classes extending {@link HealthMonitor}
 *
 * @author Martin Bechtle
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface HealthTweetDescriptor {

    String name();

    DependencyType type() default DependencyType.RESOURCE;

    DependencyImportance importance() default DependencyImportance.PRIMARY;

    /**
     * Time to live (to cache the result without re-computing) in seconds for healthy results.
     * Use a negative value to never cache. Default is one minute.
     */
    int secondsToLive() default 60;

    /**
     * Time to live in seconds for unhealthy results.
     * Defaults to one minute, useful to customise when secondsToLive is big, and you don't want unhealthy results
     * to be cached too long, else you might experience a lot of latency before seing a healthy result after fixing
     * the underlying issue.
     */
    int secondsToLiveIfUnhealthy() default 60;
}
