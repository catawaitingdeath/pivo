package org.example.pivo.service;

import org.example.pivo.mapper.StorageMapper;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.StorageData;
import org.example.pivo.utils.data.StoreData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class StorageServiceTests {
    private StorageService storageService;
    private StorageMapper storageMapper = Mappers.getMapper(StorageMapper.class);
    private StorageRepository mockStorageRepository = mock(StorageRepository.class);
    private String id1 = "sO4QJESdtHrFP6YVr_vvgNywK";
    private String id2 = "qHzP0ozaJvqMI5ctVTlMC7_b8";


    @BeforeEach
    void setup() {
        storageService = new StorageService(mockStorageRepository, storageMapper);
    }

    @Test
    @DisplayName("Create a storage")
    void createStorage() {
        var StorageEntity = StorageData.storageEntity1(id1);
        var StorageEntityNullId = StorageData.storageEntity1();
        var createStorageDto = StorageData.createStorageDto1();
        var StorageDto = StorageData.storageDto1(id1);

        Mockito.doReturn(StorageEntity).when(mockStorageRepository).save(StorageEntityNullId);

        var actual = storageService.create(createStorageDto);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(StorageDto);
        Mockito.verify(mockStorageRepository, Mockito.times(1)).save(StorageEntityNullId);
    }

    @Test
    @DisplayName("Return a list of storages")
    void getAll_FullRepository() {
        var StorageDto1 = StorageData.storageDto1(id1);
        var StorageDto2 = StorageData.storageDto2(id2);
        List<StorageDto> StorageDtoList = new ArrayList<>();
        StorageDtoList.add(StorageDto1);
        StorageDtoList.add(StorageDto2);
        var StorageEntity1 = StorageData.storageEntity1(id1);
        var StorageEntity2 = StorageData.storageEntity2(id2);
        List<StorageEntity> StorageEntityList = new ArrayList<>();
        StorageEntityList.add(StorageEntity2);
        StorageEntityList.add(StorageEntity1);

        Mockito.doReturn(StorageEntityList).when(mockStorageRepository).findAll();
        var actual = storageService.getAll();
        assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(StorageDtoList);
    }

    @Test
    @DisplayName("Return an empty list")
    void getAll_EmptyStorageRepository() {
        List<StorageEntity> StorageEntityList = List.of();

        Mockito.doReturn(StorageEntityList).when(mockStorageRepository).findAll();

        var actual = storageService.getAll();
        assertThat(actual)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return a storage")
    void getStorage_ReturnStorage() {
        var StorageDto = StorageData.storageDto1(id1);

        Mockito.doReturn(Optional.of(StorageData.storageEntity1(id1))).when(mockStorageRepository).findById(id1);

        var actual = storageService.get(id1);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(StorageDto);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getStorage_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockStorageRepository).findById("0");

        var exception = assertThrows(RuntimeException.class, () -> storageService.get("0"));
        assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }
}
