package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.example.pivo.config.PostgresInitializer;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.utils.data.BeerData;
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

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class SearchControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private ObjectMapper jacksonObjectMapper;
    private String idLager = "W_cPwW5eqk9kxe2OxgivJzVgu";

    @Test
    void searchByNameTest() throws Exception {
        var beerEntityLager = beerRepository.save(BeerData.beerEntityLager());
        beerRepository.save(BeerData.beerEntityAle());
        var expectedList = List.of(BeerData.beerDtoLager(beerEntityLager.getId()));
        var expectedListString = jacksonObjectMapper.writeValueAsString(expectedList);
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/by-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "игули"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonAssertions.assertThatJson(result.getResponse().getContentAsString()).isEqualTo(expectedListString);
    }

    @Test
    void searchByCriteriaTest() throws Exception {
        var beerEntityLager = BeerData.beerEntityLager();
        var beerEntityAle = BeerData.beerEntityAle();
        beerRepository.save(beerEntityLager);
        beerRepository.save(beerEntityAle);
        var beerList = List.of(BeerData.beerDtoLager(idLager));
        var beerListString = jacksonObjectMapper.writeValueAsString(beerList);
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/by-criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minPrice", "10")
                        .param("maxPrice", "80")
                        .param("minAlcohol", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonAssertions.assertThatJson(result.equals(beerListString));

    }
}
