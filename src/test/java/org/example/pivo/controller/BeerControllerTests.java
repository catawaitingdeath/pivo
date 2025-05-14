package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private BeerService beerService;
    @Autowired
    private ObjectMapper jacksonObjectMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BeerMapper beerMapper;

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
        assertThat(pivo)
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
        var createBeerDto = BeerData.createBeerDtoLager();
        var id = beerService.create(createBeerDto).getId();
        String jsonBeer = objectMapper.writeValueAsString(createBeerDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonBeer));
    }

    @Test
    void customTest() throws Exception {
        var createBeerDtoLager = BeerData.createBeerDtoLager();
        var createBeerDtoAle = BeerData.createBeerDtoAle();
        var idLager = beerService.create(createBeerDtoLager).getId();
        var idAle = beerService.create(createBeerDtoAle).getId();
        var expectedList = List.of(beerRepository.findById(idLager).get(), beerRepository.findById(idAle).get());
        var expectedListString = jacksonObjectMapper.writeValueAsString(expectedList);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("price", "10")
                        .param("alcohol", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(expectedListString));
    }

    @Test
    void searchByNameTest() throws Exception {
        var createBeerDtoLager = BeerData.createBeerDtoLager();
        var createBeerDtoAle = BeerData.createBeerDtoAle();
        var idLager = beerService.create(createBeerDtoLager).getId();
        beerService.create(createBeerDtoAle);

        var expectedList = List.of(BeerData.beerDtoLager(idLager));
        var expectedListString = jacksonObjectMapper.writeValueAsString(expectedList);
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/beer/searchByName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "игули"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonAssertions.assertThatJson(result.getResponse().getContentAsString()).isEqualTo(expectedListString);
    }

    @Test
    void searchByCriteriaTest() throws Exception {
        var createBeerDtoLager = BeerData.createBeerDtoLager();
        var createBeerDtoAle = BeerData.createBeerDtoAle();
        var id = beerService.create(createBeerDtoLager).getId();
        beerService.create(createBeerDtoAle);
        var beerList = List.of(BeerData.beerDtoLager(id));
        var beerListString = jacksonObjectMapper.writeValueAsString(beerList);
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/beer/searchByCriteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minPrice", "10")
                        .param("maxPrice", "80")
                        .param("minAlcohol", "5")
                        .param("maxAlcohol", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonAssertions.assertThatJson(result.getResponse().getContentAsString()).isEqualTo(beerListString);

    }

}