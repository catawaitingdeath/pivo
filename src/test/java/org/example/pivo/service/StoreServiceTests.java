package org.example.pivo.service;

import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StoreEntity;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.StoreData;
import org.example.pivo.utils.data.TypeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class StoreServiceTests {
    private StoreService storeService;
    private StoreMapper storeMapper = Mappers.getMapper(StoreMapper.class);
    private StoreRepository mockStoreRepository = mock(StoreRepository.class);
    private String id1 = "W_cPwW5eqk9kxe2OxgivJzVgu";
    private String id2 = "9kxe2OW_cPwW5exgivJ";


    @BeforeEach
    void setup() {
        storeService = new StoreService(mockStoreRepository, storeMapper);
    }

    @Test
    @DisplayName("Create a store")
    void createStore() {
        var storeEntity = StoreData.storeEntity1(id1);
        var storeEntityNullId = StoreData.storeEntity1();
        var createStoreDto = StoreData.createStoreDto1();
        var storeDto = StoreData.storeDto1(id1);

        Mockito.doReturn(storeEntity).when(mockStoreRepository).save(storeEntityNullId);

        var actual = storeService.create(createStoreDto);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(storeDto);
        Mockito.verify(mockStoreRepository, Mockito.times(1)).save(storeEntityNullId);
    }

    @Test
    @DisplayName("Return a list of stores")
    void getAll_FullRepository() {
        var storeDto1 = StoreData.storeDto1(id1);
        var storeDto2 = StoreData.storeDto2(id2);
        List<StoreDto> storeDtoList = new ArrayList<>();
        storeDtoList.add(storeDto1);
        storeDtoList.add(storeDto2);
        var storeEntity1 = StoreData.storeEntity1(id1);
        var storeEntity2 = StoreData.storeEntity2(id2);
        List<StoreEntity> storeEntityList = new ArrayList<>();
        storeEntityList.add(storeEntity2);
        storeEntityList.add(storeEntity1);

        Mockito.doReturn(storeEntityList).when(mockStoreRepository).findAll();
        var actual = storeService.getAll();
        assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(storeDtoList);
    }

    @Test
    @DisplayName("Return an empty list")
    void getAll_EmptyStoreRepository() {
        List<StoreEntity> storeEntityList = List.of();

        Mockito.doReturn(storeEntityList).when(mockStoreRepository).findAll();

        var actual = storeService.getAll();
        assertThat(actual)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return a store")
    void getStore_ReturnStore() {
        var storeDto = StoreData.storeDto1(id1);

        Mockito.doReturn(Optional.of(StoreData.storeEntity1(id1))).when(mockStoreRepository).findById(id1);

        var actual = storeService.get(id1);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(storeDto);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getStore_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockStoreRepository).findById("0");

        var exception = assertThrows(RuntimeException.class, () -> storeService.get("0"));
        assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }

}
