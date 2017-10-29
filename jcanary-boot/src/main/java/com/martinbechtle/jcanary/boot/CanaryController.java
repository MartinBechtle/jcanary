package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.tweet.HealthAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Exposes the result of {@link HealthAggregator} as a {@link RestController} GET request.
 * <p>
 * Requires Spring Boot Starter Web in the classpath, and requires a property jcanary.boot.enabled to be set to true.
 * <p>
 * The path, by default, is /canary but can be overridden with the jcanary.boot.path property.
 * <p>
 * In case security is wanted, you can specify a secret in the jcanary.boot.secret property.
 * Such secret can be provided by the caller either as a request param of name secret and the specified value
 * (eg: GET /canary?secret=12345), or within the Authorization header.
 *
 * @author Martin Bechtle
 */
@RestController
@ConditionalOnProperty(name = "jcanary.boot.enabled", havingValue = "true")
@RequestMapping("${jcanary.boot.path:/canary}")
public class CanaryController {

    private final HealthAggregator healthAggregator;

    private final String secret;

    @Autowired
    public CanaryController(@Qualifier("canaryHealthAggregator") HealthAggregator healthAggregator,
                            @Value("${jcanary.boot.secret:}") String secret) {

        this.healthAggregator = healthAggregator;
        this.secret = secret;
    }

    @RequestMapping
    public ResponseEntity tweet(HttpServletRequest httpServletRequest) {

        if (secret != null && !secret.isEmpty()) {

            String providedSecret = Optional.ofNullable(httpServletRequest.getParameter("secret"))
                    .orElseGet(() -> httpServletRequest.getHeader("Authorization"));

            if (!secret.equals(providedSecret)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }
        }
        return ResponseEntity.ok(
                healthAggregator.collect());
    }
}
