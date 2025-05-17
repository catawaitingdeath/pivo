package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.mapper.StorageMapper;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.model.exceptions.NotFoundException;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.utils.data.StorageData;
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

public class StorageServiceTests {
    private StorageService storageService;
    private StorageMapper storageMapper = Mappers.getMapper(StorageMapper.class);
    private StorageRepository mockStorageRepository;
    private String id1 = "sO4QJESdtHrFP6YVr_vvgNywK";
    private String id2 = "qHzP0ozaJvqMI5ctVTlMC7_b8";


    @BeforeEach
    void setup() {
        mockStorageRepository = mock(StorageRepository.class);
        storageService = new StorageService(mockStorageRepository, storageMapper);
    }

    @Test
    @DisplayName("Create a storage")
    void createStorage() {
        var storageEntity = StorageData.storageEntity1(id1);
        var storageEntityNullId = StorageData.storageEntity1();
        var createStorageDto = StorageData.createStorageDto1();
        var storageDto = StorageData.storageDto1(id1);

        Mockito.doReturn(storageEntity).when(mockStorageRepository).save(storageEntityNullId);

        var actual = storageService.create(createStorageDto);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(storageDto);
        Mockito.verify(mockStorageRepository, Mockito.times(1)).save(storageEntityNullId);
    }

    @Test
    @DisplayName("Return a list of storages")
    void getAll_FullRepository() {
        var storageDto1 = StorageData.storageDto1(id1);
        var storageDto2 = StorageData.storageDto2(id2);
        var storageDtoList = new ArrayList<StorageDto>();
        storageDtoList.add(storageDto1);
        storageDtoList.add(storageDto2);
        var storageEntity1 = StorageData.storageEntity1(id1);
        var storageEntity2 = StorageData.storageEntity2(id2);
        var storageEntityList = new ArrayList<StorageEntity>();
        storageEntityList.add(storageEntity2);
        storageEntityList.add(storageEntity1);
        var pageNumber = 0;
        var pageSize = 10;

        Mockito.doReturn(new PageImpl<>(storageEntityList)).when(mockStorageRepository).findAll(PageRequest.of(pageNumber, pageSize));
        var actual = storageService.getAll(pageNumber, pageSize);
        Assertions.assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(storageDtoList);
    }

    @Test
    @DisplayName("Return an empty list")
    void getAll_EmptyStorageRepository() {
        List<BeerEntity> storageEntityList = List.of();

        Mockito.doReturn(storageEntityList).when(mockStorageRepository).findAll();

        var actual = storageService.getAll(0, 10);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return a storage")
    void getStorage_ReturnStorage() {
        var storageDto = StorageData.storageDto1(id1);

        Mockito.doReturn(Optional.of(StorageData.storageEntity1(id1))).when(mockStorageRepository).findById(id1);

        var actual = storageService.get(id1);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(storageDto);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getStorage_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockStorageRepository).findById("0");

        var exception = assertThrows(NotFoundException.class, () -> storageService.get("0"));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }
}
