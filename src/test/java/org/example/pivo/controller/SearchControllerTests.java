package org.example.pivo.controller;

import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.StoreData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigInteger;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class SearchControllerTests extends BasicControllerTests {

    public void saveBeerEntities() {
        beerRepository.save(BeerData.beerEntityLager());
        beerRepository.save(BeerData.beerEntityAle());
        beerRepository.save(BeerData.beerEntityPorter());
        beerRepository.save(BeerData.beerEntityStout());
    }

    public StorageEntity storageEntityBuilder(String beer, String store) {
        return StorageEntity.builder()
                .beer(beer)
                .store(store)
                .count(BigInteger.valueOf(10))
                .build();
    }

    @Test
    void searchByNameTest() throws Exception {
        saveBeerEntities();
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
        var beerEntityAle = BeerData.beerEntityAle();
        var id = beerRepository.save(beerEntityAle).getId();
        var beerList = List.of(BeerData.beerDtoAle(id));
        var beerListString = objectMapper.writeValueAsString(beerList);

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
        var storeId1 = storeRepository.save(StoreData.storeEntityLenigradskoe()).getId();
        var storeId2 = storeRepository.save(StoreData.storeEntityProstornaya()).getId();
        var storeId3 = storeRepository.save(StoreData.storeEntityLetnaya()).getId();
        var storeId4 = storeRepository.save(StoreData.storeEntityDzerzhinskogo()).getId();
        storageRepository.save(storageEntityBuilder(beerId, storeId1));
        storageRepository.save(storageEntityBuilder(beerId, storeId2));
        storageRepository.save(storageEntityBuilder(beerId, storeId3));
        storageRepository.save(storageEntityBuilder(beerId, storeId4));
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
        var storeEntity1 = StoreData.storeEntityLenigradskoe();
        var storeEntity2 = StoreData.storeEntityProstornaya();
        var storeEntity3 = StoreData.storeEntityLetnaya();
        String storeId1 = storeRepository.save(storeEntity1).getId();
        String storeId2 = storeRepository.save(storeEntity2).getId();
        String storeId3 = storeRepository.save(storeEntity3).getId();
        var storageEntity1 = storageEntityBuilder(beerId1, storeId1);
        var storageEntity2 = storageEntityBuilder(beerId1, storeId2);
        var storageEntity3 = storageEntityBuilder(beerId1, storeId3);
        var storageEntity4 = storageEntityBuilder(beerId2, storeId2);
        var storageEntity5 = storageEntityBuilder(beerId2, storeId2);
        var storageEntity6 = storageEntityBuilder(beerId3, storeId2);
        var storageEntity7 = storageEntityBuilder(beerId3, storeId3);
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
        var storeEntity = StoreData.storeEntityLenigradskoe();
        String storeId = storeRepository.save(storeEntity).getId();
        var storageEntity1 = storageEntityBuilder(beerEntity1.getId(), storeId);
        var storageEntity2 = storageEntityBuilder(beerEntity2.getId(), storeId);
        var storageEntity3 = storageEntityBuilder(beerEntity3.getId(), storeId);
        storageRepository.save(storageEntity1);
        storageRepository.save(storageEntity2);
        storageRepository.save(storageEntity3);
        mockMvc.perform(MockMvcRequestBuilders.get("/beer/search/certain-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("storeId", storeId)
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0].beerDto.name").value("Напиток пивной Айс Крим Портер Хазелнат"));
    }
}
