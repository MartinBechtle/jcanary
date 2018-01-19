# JCanary
Java library for canary checks on web services.

Canary checks are similar to a health check, with the main difference being that it tests the system much deeper.
Health checks usually collect a number of indicators and for each of those the result is either UP or DOWN.
If the service is unhealthy, a health endpoint usually returns a non-200 status code.
This has important impacts on load balancers and orchestration systems like Kubernetes.

A **canary** endpoint reveals much more sophisticate information and can be used for general monitoring of service,
as opposed to just detecting if it's functioning or not functioning.

For example, your service might depend on a third party for some API calls, but not all of them.
In such case, the canary endpoint might return information about that third party being down.
Your service might also depend on a scheduled task running every X hours. 
The canary endpoint could raise an alarm if no task was run in the last X+1 hours.

## Requirements
* Java version 8 or greater

## Versioning
Version 1 will have to be extremely flexible, so expect breaking changes between minor versions.
We will use semantic versioning starting with version 2, as soon as the API is stable and future-proof.

## How does JCanary work
JCanary allows you to define custom Dependencies and Health Monitors for your service.

Each dependency has a unique name, a type (eg: Database, FTP, MessageQueue, Worker)
and an importance (eg: primary, secondary). For each of those you can define a Health Monitor that
determines in which status such dependency is (eg: healthy, degraded, critical).

Each HealthMonitor is "tweeted" by JCanary and cached for a specified amount of time.
A HealthAggregator component can be used to aggregate results from different monitors 
and expose such information over a REST endpoint.

## Including JCanary in your project
This project cannot be found in the Maven central repository. 
It's not famous enough yet! It is available on jitpack, so you will have to add the jitpack repository in your build tool.

The project is made of three libraries:

* jcanary-api: the core API with POJOs (contracts) specifying the Dependency format
* jcanary-tweet: the engine that aggregates health monitors and caches results
* jcanary-boot: a wrapper of jcanary-tweet that allows super-easy setup in Spring Boot

### Maven
To use it in your Maven build add:
```xml
  <repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
  </repositories>
```

and the dependency:

```xml
	<dependency>
		<groupId>com.github.MartinBechtle.jcanary</groupId>
		<artifactId>jcanary-tweet</artifactId>
		<version>1.2.0-RC1</version>
	</dependency>
```

### Gradle
To use it in your Gradle build add:
```groovy
    repositories {
        maven {
            url 'https://jitpack.io'
        }
    }
```

and the dependency:
```groovy
compile "com.github.MartinBechtle.jcanary:jcanary-tweet:1.2.0-RC1"
```

## Using with Spring Boot

### Requirements
Spring Boot version 1.3.0 at least. Untested with Spring Boot 2 but might work when using Spring MVC.

### Dependencies
Include the jcanary-boot library in your project instead of jcanary-tweet (see above gradle/maven examples).
You can now configure via your application.properties (or yaml):

```properties
jcanary.boot.enabled=true
jcanary.boot.path=/canary
jcanary.boot.secret=changeMePlease
```

### Configuration
Summary of available options:
* enabled: false by default, set this to true to enable Spring Boot autoconfiguration for jcanary
* path: by default the canary endpoint is exposed on /canary (as a GET) request, but you can override
* secret: by default empty, if you don't want to expose your canary data to the world you can require a secret that has to be passed as query parameter or authorization header with the GET request


### Setting up health monitors
First of all define one or more Health Monitors
```java
@HealthTweetDescriptor(
        name = "my-database",
        secondsToLive = 200,
        importance = DependencyImportance.PRIMARY,
        type = DependencyType.DATABASE)
public class DatabaseHealthMonitor implements HealthMonitor {

    private final UserRepository repository;

    @Autowired
    public DummyHealthMonitor(UserRepository repository) {

        this.repository = repository;
    }

    @Override
    public HealthResult check() {

        try {
            repository.findOne(1);
            return HealthResult.ok();
        }
        catch (Exception e) {
            return HealthResult.of(DependencyStatus.CRITICAL, "Could not query database!");
        }
    }
}
```

Please refer to the javadoc or sources, which are published on the Jitpack Maven repo, 
to find out any default values for the HealthTweetDescriptor 
and all possible values for DependencyImportance, DependencyType and DependencyStatus.

Also note that if the check() method throws an Exception, it will still be caught, rather than resulting in a horrible error, 
but you have no control over the status and status text.

Once you have one or more monitors, you should define a bean named "canaryHealthAggregator" and register any desired
HealthMonitors with it. Note that without the HealthTweetDescriptor annotation a HealthMonitor is invalid and will
result in an exception when trying to register. Also each monitor needs a unique name (duplication also leads to exception).

```java
@Configuration
public class CanaryConfig {

    // assuming a bean of type DatabaseHealthMonitor is defined
    @Bean(name = "canaryHealthAggregator")
    public HealthAggregator healthAggregator(DatabaseHealthMonitor databaseHealthMonitor) {

        return new HealthAggregator(Clock.systemDefaultZone(), new ForkJoinPool(3))
                .register(databaseHealthMonitor);
    }
}
```

It is technically possible to define what implementation of Clock to use (for determining cache timeout),
but the systemDefaultZone one is recommended.

Note that the ForkJoinPool is meant to be used for parallel processing of health monitors.
It's up to you to choose a size that is proportional to your number of health checks and their duration. 

Now set enable jcanary-boot in your application.properties (or yaml):
```properties
jcanary.boot.enabled=true
```

jcanary-boot will set up a Spring RestController listening on GET /canary

You can optionally specify a secret for authentication:
```properties
jcanary.boot.secret=changeMePlease
```

Example of successful request:
```bash
curl -X GET http://localhost:9090/tide-backend/rest/api/v3/banking/canary?secret=changeMePlease
```

```json
{
   "serviceName":"test-service",
   "result":"OK",
   "tweets":[
      {
         "dependency":{
            "importance":"PRIMARY",
            "type":"RESOURCE",
            "name":"dummyMonitor"
         },
         "result":{
            "status":"HEALTHY",
            "statusText":""
         }
      }
   ]
}
```

Example of response in case of wrong secret:

```bash
curl -X GET http://localhost:9090/tide-backend/rest/api/v3/banking/canary?secret=wrongSecret
< HTTP/1.1 401  
```

```json
{
   "serviceName":"test-service",
   "result":"FORBIDDEN",
   "tweets":[]
}
```

Yes, we know that HTTP status 403 is for access forbidden. But actually 401 is more suitable for an authentication failure. 
The result "FORBIDDEN" has to do with the Canary API and not really anything to do with the HTTP protocol.

Example of response in case of uncaught exception while processing the request:

```bash
curl -X GET http://localhost:9090/tide-backend/rest/api/v3/banking/canary?secret=wrongSecret
< HTTP/1.1 500   
```

```json
{
   "serviceName":"test-service",
   "result":"ERROR",
   "tweets":[]
}
```


## Using with other frameworks

Just include jcanary-tweet instead of jcanary-boot.

As long as you register a HealthAggregator component, 
just write your own REST controller that invokes the HealthAggregator.collect() method
and returns the result in the response body.

You will have to implement your own security around such controller.

## Monitoring your canary endpoints

Configure each health monitor in a way that, if the canary endpoint is called too often, it caches the results for a sensible
amount of time in order to avoid putting to much pressure on the system.

At this point you have to build your own monitoring infrastructure: you can either just inspect the canary endpoints manually
and have a look at the JSON, or build a tool that polls at regular intervals all your services and shoots alarms when required.
The canary endpoint will never give a qualitative measure of your service's health: it is your job to decide which and
how many dependencies can be in a non healthy status before launching any alarms.

And that is exactly what jcanary gives you power to do, as it allows you to decide, for each dependency, what type it is
and what importance it has, and your health monitor implementations can decide what kind of degradation level is being faced.

## Future development

A web based portal will be built allowing to visually monitor your services, supporting different and customised alarm mechanisms.
It will also allow to have a high level visual overview of your architecture if you assign unique names to all your 
dependencies across multiple services, as it will figure out all inter-dependencies.
