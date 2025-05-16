package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StoreEntity;
import org.example.pivo.model.exceptions.NotFoundException;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.utils.data.StoreData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class StoreServiceTests {
    private StoreService storeService;
    private StoreMapper storeMapper = Mappers.getMapper(StoreMapper.class);
    private StoreRepository mockStoreRepository;
    private String id1 = "W_cPwW5eqk9kxe2OxgivJzVgu";
    private String id2 = "9kxe2OW_cPwW5exgivJ";


    @BeforeEach
    void setup() {
        mockStoreRepository = mock(StoreRepository.class);
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
        Assertions.assertThat(actual)
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
        var pageNumber = 0;
        var pageSize = 10;

        Mockito.doReturn(new PageImpl<>(storeEntityList)).when(mockStoreRepository).findAll(PageRequest.of(pageNumber, pageSize));
        var actual = storeService.getAll(pageNumber, pageSize);
        Assertions.assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(storeDtoList);
    }

    @Test
    @DisplayName("Return an empty list")
    void getAll_EmptyStoreRepository() {
        List<BeerEntity> storeEntityList = List.of();

        Mockito.doReturn(storeEntityList).when(mockStoreRepository).findAll();

        var actual = storeService.getAll(0, 10);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return a store")
    void getStore_ReturnStore() {
        var storeDto = StoreData.storeDto1(id1);

        Mockito.doReturn(Optional.of(StoreData.storeEntity1(id1))).when(mockStoreRepository).findById(id1);

        var actual = storeService.get(id1);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(storeDto);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getStore_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockStoreRepository).findById("0");

        var exception = assertThrows(NotFoundException.class, () -> storeService.get("0"));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }

}
