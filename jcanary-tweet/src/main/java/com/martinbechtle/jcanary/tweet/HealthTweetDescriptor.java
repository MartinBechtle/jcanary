package com.martinbechtle.jcanary.tweet;

import com.martinbechtle.jcanary.api.DependencyImportance;
import com.martinbechtle.jcanary.api.DependencyType;

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
     * Time to live (to cache the result without re-computing) in seconds.
     * Use a negative value to never cache. Default is one minute.
     */
    int secondsToLive() default 60;
}
