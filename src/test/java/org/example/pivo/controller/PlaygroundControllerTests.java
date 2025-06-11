package org.example.pivo.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.config.PostgresInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.math.BigInteger;

@EnableWireMock(
        {
                @ConfigureWireMock(
                        name = "wm-server",
                        port = 10102
                )
        }
)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class PlaygroundControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void feignTest() throws Exception {
        var storeId = "S7TKIwtHDfoLOESVj16e_v3ie";
        var create1 = CreateEmployeeDto.builder()
                .name("Alice")
                .surname("Johnson")
                .phone("79684551668")
                .email("alice.johnson@example.com")
                .position("Cashier")
                .salary(BigInteger.valueOf(30000))
                .store(storeId)
                .build();
        var create2 = CreateEmployeeDto.builder()
                .name("Bob")
                .surname("Smith")
                .phone("79684551888")
                .email("bob.smith@example.com")
                .position("Manager")
                .salary(BigInteger.valueOf(50000))
                .store(storeId)
                .build();

        // --- POST #1 ---
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(create1)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                      "id": "id_1",
                                      "name": "Alice",
                                      "surname": "Johnson",
                                      "phone": "79684551668",
                                      "email": "alice.johnson@example.com",
                                      "position": "Cashier",
                                      "salary": 30000,
                                      "store": "%s"
                                    }
                                """.formatted(storeId))));

        // --- POST #2 ---
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(create2)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                   {
                                     "id": "id_2",
                                     "name": "Bob",
                                     "surname": "Smith",
                                     "phone": "79684551888",
                                     "email": "bob.smith@example.com",
                                     "position": "Manager",
                                     "salary": 50000,
                                     "store": "%s"
                                   }
                                """.formatted(storeId))));

        // --- GET before DELETE ---
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/employee/store/" + storeId))
                .inScenario("get-store")
                .whenScenarioStateIs("Started")
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                      "id": "store_123",
                                      "employees": [
                                        {
                                          "id": "id_1",
                                          "name": "Alice",
                                          "surname": "Johnson",
                                          "phone": "79684551668",
                                          "email": "alice.johnson@example.com",
                                          "position": "Cashier",
                                          "salary": 30000,
                                          "store": "store_123"
                                        },
                                        {
                                          "id": "id_2",
                                          "name": "Bob",
                                          "surname": "Smith",
                                          "phone": "79684551888",
                                          "email": "bob.smith@example.com",
                                          "position": "Manager",
                                          "salary": 50000,
                                          "store": "store_123"
                                        }
                                      ]
                                    }
                                """))
                .willSetStateTo("DELETED"));

        // --- DELETE ---
        WireMock.stubFor(WireMock.delete(WireMock.urlPathEqualTo("/employee/store/" + storeId))
                .willReturn(WireMock.aResponse()
                        .withStatus(204)));

        // --- GET after DELETE ---
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/employee/store/" + storeId))
                .inScenario("get-store")
                .whenScenarioStateIs("DELETED")
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/playground/feign")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getStatus())
                .isEqualTo(500);

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson("""
                            {
                              "name": "Alice",
                              "surname": "Johnson",
                              "phone": "79684551668",
                              "email": "alice.johnson@example.com",
                              "position": "Cashier",
                              "salary": 30000,
                              "store": "%s"
                            }
                        """.formatted(storeId))));

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson("""
                            {
                              "name": "Bob",
                              "surname": "Smith",
                              "phone": "79684551888",
                              "email": "bob.smith@example.com",
                              "position": "Manager",
                              "salary": 50000,
                              "store": "%s"
                            }
                        """.formatted(storeId))));

        WireMock.verify(2, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/employee/store/%s".formatted(storeId))));
        WireMock.verify(1,
                WireMock.deleteRequestedFor(WireMock.urlPathEqualTo("/employee/store/%s".formatted(storeId))));

    }
}
