package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.example.pivo.config.PostgresInitializer;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.service.StoreService;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.StoreData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class StoreControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private String id = "W_cPwW5eqk9kxe2OxgivJzVgu";

    @BeforeEach
    public void setUp() {
        WireMock.stubFor(WireMock.delete(WireMock.urlPathEqualTo("/employee/store/" + id))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                      "id": "%s",
                                      "employees": [
                                        {
                                          "id": "id_1",
                                          "name": "Alice",
                                          "surname": "Johnson",
                                          "phone": "79684551668",
                                          "email": "alice.johnson@example.com",
                                          "position": "Cashier",
                                          "salary": 30000,
                                          "store": "%s"
                                        },
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
                                      ]
                                    }
                                """.formatted(id))));
        storeRepository.deleteAll();
    }

    @Test
    void createTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "Ленинградское ш., 58с53, Москва",
                                    "phone": "123456789"
                                }"""))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var store = storeRepository.findAll();
        Assertions.assertThat(store)
                .hasSize(1);
    }

    @Test
    void getAllTest() throws Exception {
        var storeEntity1 = StoreData.storeEntity1();
        var storeEntity2 = StoreData.storeEntity2();
        storeRepository.save(storeEntity1);
        storeRepository.save(storeEntity2);
        mockMvc.perform(MockMvcRequestBuilders.get("/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0].address").value("Ленинградское ш., 58с53, Москва"))
                .andExpect(jsonPath("$.content[1].address").value("Просторная ул., 6, Москва"));
    }

    @Test
    void getStoreTest() throws Exception {
        var storeEntity = StoreData.storeEntity1(id);
        storeRepository.save(storeEntity);
        var storeDto = StoreData.storeDto1(id);
        String jsonStore = objectMapper.writeValueAsString(storeDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/store/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonStore));
    }

    @Test
    void deleteStoreTest() throws Exception {
        var storeEntity = StoreData.storeEntity1(id);
        storeRepository.save(storeEntity);
        var storesBefore = storeRepository.findAll();
        Assertions.assertThat(storesBefore)
                .hasSize(1);
        mockMvc.perform(MockMvcRequestBuilders.delete("store/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var storesAfter = storeRepository.findAll();
        Assertions.assertThat(storesAfter)
                .isEmpty();
    }
}
