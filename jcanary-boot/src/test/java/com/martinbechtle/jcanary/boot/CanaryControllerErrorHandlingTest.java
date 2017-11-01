package com.martinbechtle.jcanary.boot;

import com.martinbechtle.jcanary.tweet.HealthAggregator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * * Integration test for the case in which jcanary is disabled (see application-enabled.properties)
 *
 * @author Martin Bechtle
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApiTestConfig.class})
@WebAppConfiguration
@ActiveProfiles({"enabled"})
public class CanaryControllerErrorHandlingTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private HealthAggregator healthAggregator;

    private MockMvc mockMvc;

    @Before
    public void setUp() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();
    }

    @Test
    public void canaryEndpoint_ShouldReturnFailedCanary_WhenExceptionThrown() throws Exception {

        when(healthAggregator.collect())
                .thenThrow(new RuntimeException());

        mockMvc.perform(get("/canary"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.serviceName").value("test-service"))
                .andExpect(jsonPath("$.result").value("ERROR"))
                .andExpect(jsonPath("$.tweets").isEmpty());
    }
}
