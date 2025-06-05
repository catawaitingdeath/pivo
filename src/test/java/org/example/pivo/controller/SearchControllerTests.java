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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
        beerRepository.save(BeerData.beerEntityLager());
        beerRepository.save(BeerData.beerEntityAle());
        beerRepository.save(BeerData.beerEntityPorter());
        beerRepository.save(BeerData.beerEntityStout());
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/by-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "игули")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Жигули Барное светлое фильтрованное"));
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
                        .param("minAlcohol", "6")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonAssertions.assertThatJson(result.equals(beerListString));
    }

    @Test
    void searchInStockTest() throws Exception {
        var beerEntityLager = BeerData.beerEntityLager();
        var beerId = beerRepository.save(beerEntityLager).getId();
        var storeId1 = storeRepository.save(StoreData.storeEntity1()).getId();
        var storeId2 = storeRepository.save(StoreData.storeEntity2()).getId();
        var storeId3 = storeRepository.save(StoreData.storeEntity3()).getId();
        var storeId4 = storeRepository.save(StoreData.storeEntity4()).getId();
        var storageEntity1 = StorageEntity.builder()
                .beer(beerId)
                .store(storeId1)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity2 = StorageEntity.builder()
                .beer(beerId)
                .store(storeId2)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity3 = StorageEntity.builder()
                .beer(beerId)
                .store(storeId3)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity4 = StorageEntity.builder()
                .beer(beerId)
                .store(storeId4)
                .count(BigInteger.valueOf(10))
                .build();
        storageRepository.save(storageEntity1);
        storageRepository.save(storageEntity2);
        storageRepository.save(storageEntity3);
        storageRepository.save(storageEntity4);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/in-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("beerId", beerId)
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(storeId3))
                .andExpect(jsonPath("$.content[1].id").value(storeId4));
    }

    @Test
    void searchForStoresTest() throws Exception {
        var beerEntity1 = BeerData.beerEntityLager();
        var beerEntity2 = BeerData.beerEntityAle();
        var beerEntity3 = BeerData.beerEntityPorter();
        var beerId1 = beerRepository.save(beerEntity1).getId();
        var beerId2 = beerRepository.save(beerEntity2).getId();
        var beerId3 = beerRepository.save(beerEntity3).getId();
        var beerIds = List.of(beerId1, beerId2, beerId3);
        var storeEntity1 = StoreData.storeEntity1();
        var storeEntity2 = StoreData.storeEntity2();
        var storeEntity3 = StoreData.storeEntity3();
        String storeId1 = storeRepository.save(storeEntity1).getId();
        String storeId2 = storeRepository.save(storeEntity2).getId();
        String storeId3 = storeRepository.save(storeEntity3).getId();
        var storageEntity1 = StorageEntity.builder()
                .beer(beerId1)
                .store(storeId1)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity2 = StorageEntity.builder()
                .beer(beerId1)
                .store(storeId2)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity3 = StorageEntity.builder()
                .beer(beerId1)
                .store(storeId3)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity4 = StorageEntity.builder()
                .beer(beerId2)
                .store(storeId1)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity5 = StorageEntity.builder()
                .beer(beerId2)
                .store(storeId2)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity6 = StorageEntity.builder()
                .beer(beerId3)
                .store(storeId2)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity7 = StorageEntity.builder()
                .beer(beerId3)
                .store(storeId3)
                .count(BigInteger.valueOf(10))
                .build();
        storageRepository.save(storageEntity1);
        storageRepository.save(storageEntity2);
        storageRepository.save(storageEntity3);
        storageRepository.save(storageEntity4);
        storageRepository.save(storageEntity5);
        storageRepository.save(storageEntity6);
        storageRepository.save(storageEntity7);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("beers", beerIds.toArray(new String[0]))
                        .param("pageNumber", "1")
                        .param("pageSize", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(storeId2));
    }


    @Test
    void searchForBeersTest() throws Exception {
        var beerEntity1 = BeerData.beerEntityLager();
        var beerEntity2 = BeerData.beerEntityAle();
        var beerEntity3 = BeerData.beerEntityPorter();
        beerRepository.save(beerEntity1);
        beerRepository.save(beerEntity2);
        beerRepository.save(beerEntity3);
        var storeEntity = StoreData.storeEntity1();
        String storeId = storeRepository.save(storeEntity).getId();
        var storageEntity1 = StorageEntity.builder()
                .beer(beerEntity1.getId())
                .store(storeId)
                .count(BigInteger.valueOf(10))
                .build();
        var storageEntity2 = StorageEntity.builder()
                .beer(beerEntity2.getId())
                .store(storeId)
                .count(BigInteger.valueOf(100))
                .build();
        var storageEntity3 = StorageEntity.builder()
                .beer(beerEntity3.getId())
                .store(storeId)
                .count(BigInteger.valueOf(10))
                .build();
        storageRepository.save(storageEntity1);
        storageRepository.save(storageEntity2);
        storageRepository.save(storageEntity3);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/certain-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("storeId", storeId)
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Напиток пивной Айс Крим Портер Хазелнат"));
    }
}
