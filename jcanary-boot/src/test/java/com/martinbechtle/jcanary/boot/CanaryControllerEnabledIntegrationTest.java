package com.martinbechtle.jcanary.boot;

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

import static com.martinbechtle.jcanary.boot.TestUtils.responseBodyEqualsJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * * Integration test for the case in which jcanary is disabled (see application-enabled.properties)
 *
 * @author Martin Bechtle
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IntegrationTestConfig.class})
@WebAppConfiguration
@ActiveProfiles({"enabled"})
public class CanaryControllerEnabledIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();
    }

    @Test
    public void canaryEndpoint_ShouldReturnOk_WhenEnabledInConfig() throws Exception {

        mockMvc.perform(get("/canary"))
                .andExpect(status().isOk())
                .andExpect(responseBodyEqualsJson("Canary_HealthyResponse.json", getClass()))
                .andExpect(jsonPath("$.serviceName").value("test-service"))
                .andExpect(jsonPath("$.result").value("OK"))
                .andExpect(jsonPath("$.tweets[0].dependency.importance").value("PRIMARY"))
                .andExpect(jsonPath("$.tweets[0].dependency.type").value("RESOURCE"))
                .andExpect(jsonPath("$.tweets[0].dependency.name").value("dummyMonitor"))
                .andExpect(jsonPath("$.tweets[0].result.status").value("HEALTHY"))
                .andExpect(jsonPath("$.tweets[0].result.statusText").value(""));
    }


}
