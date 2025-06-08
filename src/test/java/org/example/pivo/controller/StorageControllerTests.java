package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.example.pivo.config.PostgresInitializer;
import org.example.pivo.model.dto.BeerShipmentDto;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.StorageData;
import org.example.pivo.utils.data.StoreData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class StorageControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private BeerRepository beerRepository;
    private String beerId1 = "2IVPACIrIT-Tr2Gw-JoApXZKT";
    private String beerId2 = "Ota-_XO_6Dc2nCKEU7LEmsi1K";
    private String storeId1 = "S7TKIwtHDfoLOESVj16e_v3ie";
    private String storeId2 = "inSV3fZx2Ai1bn0CjaDvFkIxw";
    private String storageId1 = "phpIoHFCT8fc5GEZqCZQimYxD";

    @BeforeEach
    public void setUp() {
        storageRepository.deleteAll();
        storeRepository.deleteAll();
        beerRepository.deleteAll();
        beerRepository.save(BeerData.beerEntityAle(beerId1));
        beerRepository.save(BeerData.beerEntityLager(beerId2));
        storeRepository.save(StoreData.storeEntityLenigradskoe(storeId1));
        storeRepository.save(StoreData.storeEntityProstornaya(storeId2));
    }

    @Test
    void createTest() throws Exception {
        var content = StorageData.createStorageDto100(beerId1, storeId1);
        var contentString = objectMapper.writeValueAsString(content);
        mockMvc.perform(MockMvcRequestBuilders.post("/storage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentString))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var storage = storageRepository.findAll();
        Assertions.assertThat(storage)
                .hasSize(1);
    }

    @Test
    void getAllTest() throws Exception {
        var storageEntity1 = StorageData.storageEntity100();
        storageEntity1.setBeer(beerId1);
        storageEntity1.setStore(storeId1);
        var storageEntity2 = StorageData.storageEntity10();
        storageEntity2.setBeer(beerId2);
        storageEntity2.setStore(storeId2);
        storageRepository.save(storageEntity1);
        storageRepository.save(storageEntity2);
        mockMvc.perform(MockMvcRequestBuilders.get("/storage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0].count").value("100"))
                .andExpect(jsonPath("$.content[1].count").value("10"));
    }

    @Test
    void getStorageTest() throws Exception {
        storageRepository.save(StorageData.storageEntity100(storageId1));
        String jsonStorage = objectMapper.writeValueAsString(StorageData.storageDto100(storageId1));
        mockMvc.perform(MockMvcRequestBuilders.get("/storage/{id}", storageId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonStorage));
    }

    @Test
    void shipmentTest() throws Exception {
        var position1 = BeerShipmentDto.Position.builder()
                .beerId(beerId1)
                .count(BigInteger.TEN)
                .build();
        var position2 = BeerShipmentDto.Position.builder()
                .beerId(beerId2)
                .count(BigInteger.TEN)
                .build();
        var store = StoreData.storeEntityLenigradskoe(storeId1);
        var storeShipment = BeerShipmentDto.StoreShipment.builder()
                .storeId(store.getId())
                .position(List.of(position1, position2))
                .build();
        var beerShipmentDto = BeerShipmentDto.builder()
                .storeShipments(List.of(storeShipment))
                .build();
        var contentString = objectMapper.writeValueAsString(beerShipmentDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/storage/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentString))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var storage = storageRepository.findAll();
        Assertions.assertThat(storage)
                .hasSize(2);
    }

    @Test
    @DisplayName("Return error: Позиции пива не уникальны")
    void shipmentNoUniqueIdsTest_ReturnError() throws Exception {
        var position1 = BeerShipmentDto.Position.builder()
                .beerId(beerId1)
                .count(BigInteger.TEN)
                .build();
        var position2 = BeerShipmentDto.Position.builder()
                .beerId(beerId1)
                .count(BigInteger.TEN)
                .build();
        var position3 = BeerShipmentDto.Position.builder()
                .beerId(beerId2)
                .count(BigInteger.TEN)
                .build();
        var store = StoreData.storeEntityLenigradskoe(storeId1);
        var storeShipment = BeerShipmentDto.StoreShipment.builder()
                .storeId(store.getId())
                .position(List.of(position1, position2, position3))
                .build();
        var beerShipmentDto = BeerShipmentDto.builder()
                .storeShipments(List.of(storeShipment))
                .build();
        var contentString = objectMapper.writeValueAsString(beerShipmentDto);
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/storage/shipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn()
                .getResolvedException()
                .getMessage();
        Assertions.assertThat(result)
                .isEqualTo("Позиции пива не уникальны");
    }
}
