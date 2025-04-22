package org.example.pivo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.assertj.core.api.Assertions;
import org.example.pivo.model.entity.BeerEntity;
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
    protected MockMvc mockMvc;
    @Autowired
    protected BeerRepository beerRepository;
    @Autowired private BeerService beerService;
    @Autowired private ObjectMapper jacksonObjectMapper;

    @BeforeEach
    public void setUp() {
        beerRepository.deleteAll();
    }

    @Test
    void myAwesomeTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/beer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Охота крепкое",
                                  "producer": "Хейнекен",
                                  "price": 90,
                                  "alcohol": 8.5,
                                  "typeName": "лагер"
                                }"""))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var pivo = beerRepository.findAllByProducer("Хейнекен");
        Assertions.assertThat(pivo)
                .hasSize(1);
    }

    @Test
    void myAwesomeTest1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/beer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Охота крепкое",
                                  "producer": "Хейнекен",
                                  "price": 90,
                                  "alcohol": 8.5,
                                  "typeName": "лагер"
                                }"""))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var pivo = beerRepository.findAllByProducer("Хейнекен");
        Assertions.assertThat(pivo)
                .hasSize(1);
    }

    @Test
    void getAllTest() throws Exception {
        var CreateBeerDto1 = BeerData.createBeerDto("лагер");
        var CreateBeerDto2 = BeerData.createBeerDto("эль");
        beerService.create(CreateBeerDto1);
        beerService.create(CreateBeerDto2);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void gerBeerTest() throws Exception {
        var createBeerDto = BeerData.createBeerDto("лагер");
        var id = beerService.create(createBeerDto).getId();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonBeer = ow.writeValueAsString(createBeerDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonBeer));
    }

    @Test
    void customTest() throws Exception {
        var createBeerDtoLager = BeerData.createBeerDto("лагер");
        var createBeerDtoAle = BeerData.createBeerDto("эль");
        var idLager = beerService.create(createBeerDtoLager).getId();
        var idAle = beerService.create(createBeerDtoAle).getId();
        var expectedList = List.of(beerRepository.findById(idLager).get(), beerRepository.findById(idAle).get());
        var jsonBeer = mockMvc.perform(MockMvcRequestBuilders.get("/beer/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("price", "10")
                        .param("alcohol", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<BeerEntity> list = jacksonObjectMapper.readValue(jsonBeer, new TypeReference<>(){});
        Assertions.assertThat(list)
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(expectedList);
    }

}