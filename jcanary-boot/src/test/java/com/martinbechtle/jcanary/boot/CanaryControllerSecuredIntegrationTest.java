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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * * Integration test for the case in which jcanary is secured with a secret (see application-secured.properties)
 *
 * @author Martin Bechtle
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpringBootTestApplication.class})
@WebAppConfiguration
@ActiveProfiles({"secured"})
public class CanaryControllerSecuredIntegrationTest {

    private static final String SECRET_CORRECT = "this123IsA$secret!";
    private static final String SECRET_WRONG = "wrong";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();
    }

    @Test
    public void canaryEndpoint_ShouldReturnUnauthorized_WhenEnabledAndSecuredInConfig_AndMissingSecretInRequest()
            throws Exception {

        mockMvc.perform(get("/canary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void canaryEndpoint_ShouldReturnUnauthorized_WhenEnabledAndSecuredInConfig_AndWrongSecretInQuery()
            throws Exception {

        mockMvc.perform(get("/canary").param("secret", SECRET_WRONG))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void canaryEndpoint_ShouldReturnOk_WhenEnabledAndSecuredInConfig_AndCorrectSecretInQuery()
            throws Exception {

        mockMvc.perform(
                get("/canary")
                        .param("secret", SECRET_CORRECT))
                .andExpect(status().isOk());
    }

    @Test
    public void canaryEndpoint_ShouldReturnOk_WhenEnabledAndSecuredInConfig_AndCorrectSecretInAuthHeader()
            throws Exception {

        mockMvc.perform(
                get("/canary").
                        header("Authorization", SECRET_CORRECT))
                .andExpect(status().isOk());
    }

    @Test
    public void canaryEndpoint_ShouldReturnOk_WhenEnabledAndSecuredInConfig_AndCorrectSecretInQuery_AndExistingAuthHeader()
            throws Exception {

        mockMvc.perform(
                get("/canary")
                        .header("Authorization", "someOtherHeader")
                        .param("secret", SECRET_CORRECT))
                .andExpect(status().isOk());
    }
}
