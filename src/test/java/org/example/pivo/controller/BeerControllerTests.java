package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.service.BeerService;
import org.example.pivo.utils.data.BeerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
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

}