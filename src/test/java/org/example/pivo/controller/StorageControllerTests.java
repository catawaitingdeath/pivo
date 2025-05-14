package org.example.pivo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.example.pivo.config.PostgresInitializer;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.service.BeerService;
import org.example.pivo.service.StorageService;
import org.example.pivo.service.StoreService;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.StorageData;
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
public class StorageControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private StorageService storageService;
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

    @BeforeEach
    public void setUp() {
        storageRepository.deleteAll();
        storeRepository.deleteAll();
        beerRepository.deleteAll();
    }

    @Test
    void createTest() throws Exception {
        var content = StorageData.createStorageDto1();
        content.setBeer(beerId1);
        content.setStore(storeId1);
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
        var storageEntity1 = StorageData.storageEntity1();
        storageEntity1.setBeer(beerId1);
        storageEntity1.setStore(storeId1);
        var storageEntity2 = StorageData.storageEntity2();
        storageEntity2.setBeer(beerId2);
        storageEntity2.setStore(storeId2);
        storageRepository.save(storageEntity1);
        storageRepository.save(storageEntity2);
        mockMvc.perform(MockMvcRequestBuilders.get("/storage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].count").value("100"))
                .andExpect(jsonPath("$[1].count").value("10"));
    }

    @Test
    void getStorageTest() throws Exception {
        var createStorageDto = StorageData.createStorageDto1();
        createStorageDto.setBeer(beerId1);
        createStorageDto.setStore(storeId1);
        var id = storageService.create(createStorageDto).getId();
        String jsonStorage = objectMapper.writeValueAsString(createStorageDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/storage/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonStorage));
    }
}
