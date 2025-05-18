package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.example.pivo.config.PostgresInitializer;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.repository.StoreRepository;
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

import java.math.BigInteger;
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
    private String genericId = "W_cPwW5eqk9kxe2OxgivJzVgu";
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StorageRepository storageRepository;

    @BeforeEach
    public void setUp() {
        storeRepository.deleteAll();
        beerRepository.deleteAll();
        storageRepository.deleteAll();
    }

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
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonAssertions.assertThatJson(result.equals(expectedListString));
    }

    @Test
    void searchByCriteriaTest() throws Exception {
        var beerEntityLager = BeerData.beerEntityLager();
        var beerEntityAle = BeerData.beerEntityAle();
        var beerEntityStout = BeerData.beerEntityStout();
        var beerEntityPorter = BeerData.beerEntityPorter();
        beerRepository.save(beerEntityLager);
        var id = beerRepository.save(beerEntityAle).getId();
        beerRepository.save(beerEntityStout);
        beerRepository.save(beerEntityPorter);
        var beerList = List.of(BeerData.beerDtoAle(id));
        var beerListString = jacksonObjectMapper.writeValueAsString(beerList);

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/by-criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("minPrice", "65")
                        .param("maxPrice", "80")
                        .param("minAlcohol", "6"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonAssertions.assertThatJson(result.equals(beerListString));
    }

    @Test
    void searchInStockTest() throws Exception {
        var beerEntityLager = BeerData.beerEntityLager();
        var beerEntityAle = BeerData.beerEntityAle();
        beerRepository.save(beerEntityLager);
        beerRepository.save(beerEntityAle);
        storeRepository.save(StoreData.storeEntity1());
        var storeList = List.of(StoreData.storeDto1(genericId));
        var storeListString = jacksonObjectMapper.writeValueAsString(storeList);
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/in-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("beerName", beerEntityLager.getName()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonAssertions.assertThatJson(result.equals(storeListString));
    }



    @Test
    void searchForBeersTest() throws Exception {
        var beerEntity1 = BeerData.beerEntityLager();
        var beerEntity2 = BeerData.beerEntityAle();
        beerRepository.save(beerEntity1);
        beerRepository.save(beerEntity2);
        var storeEntity = StoreData.storeEntity1();
        String storeId = storeRepository.save(storeEntity).getId();
        var storageEntity1 = StorageEntity.builder()
                .beer(beerEntity1.getId())
                .store(storeEntity.getId())
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity2 = StorageEntity.builder()
                .beer(beerEntity2.getId())
                .store(storeEntity.getId())
                .count(BigInteger.valueOf(100))
                .build();
        storageRepository.save(storageEntity1);
        storageRepository.save(storageEntity2);
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/certain-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("storeId", storeId)
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonAssertions.assertThatJson(result)
                .inPath("$.content[0].name").isEqualTo("Жигули Барное светлое фильтрованное");
        JsonAssertions.assertThatJson(result)
                .inPath("$.content[1].name").isEqualTo("Troll Brew IPA светлое нефильтрованное");
    }
}
