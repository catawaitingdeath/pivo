package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.assertj.core.api.Assertions;
import org.example.pivo.config.PostgresInitializer;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.service.BeerService;
import org.example.pivo.utils.data.BeerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class BeerControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private ObjectMapper jacksonObjectMapper;
    @Autowired
    private ObjectMapper objectMapper;
    private String idLager = "W_cPwW5eqk9kxe2OxgivJzVgu";

    @BeforeEach
    public void setUp() {
        beerRepository.deleteAll();
    }

    @Test
    void createTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/beer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Жигули Барное светлое фильтрованное",
                                  "producer": "Московская пивоваренная компания",
                                  "price": 70,
                                  "alcohol": 5,
                                  "typeName": "лагер"
                                }"""))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var pivo = beerRepository.findAll();
        Assertions.assertThat(pivo)
                .hasSize(1);
    }

    @Test
    void getAllTest() throws Exception {
        var beerEntity1 = BeerData.beerEntityLager();
        var beerEntity2 = BeerData.beerEntityAle();
        beerRepository.save(beerEntity1);
        beerRepository.save(beerEntity2);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].typeName").value("лагер"))
                .andExpect(jsonPath("$[1].typeName").value("эль"));
    }

    @Test
    void getBeerTest() throws Exception {
        var beerEntity = BeerData.beerEntityLager(idLager);
        beerRepository.save(beerEntity);
        var beerDto = BeerData.beerDtoLager(idLager);
        String jsonBeer = objectMapper.writeValueAsString(beerDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/{id}", idLager)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonBeer));
    }

    @Test
    void customTest() throws Exception {
        var beerEntityLager = BeerData.beerEntityLager();
        var beerEntityAle = BeerData.beerEntityAle();
        beerRepository.save(beerEntityLager);
        beerRepository.save(beerEntityAle);
        var expectedList = List.of(beerEntityLager);
        var expectedListString = jacksonObjectMapper.writeValueAsString(expectedList);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("price", "10")
                        .param("alcohol", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(expectedListString));
    }

}