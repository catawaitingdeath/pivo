package org.example.pivo.utils.data;

import org.example.pivo.model.dto.CreateStorageDto;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.utils.FileReaderUtility;

public class StorageData {

    public static StorageEntity storageEntity1(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity1.json", StorageEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StorageEntity storageEntity2(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity2.json", StorageEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StorageEntity storageEntity1() {
        return FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity1.json", StorageEntity.class);
    }

    public static StorageEntity storageEntity2() {
        return FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity2.json", StorageEntity.class);
    }

    public static StorageDto storageDto1(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/storage/dto/storageDto1.json", StorageDto.class);
        dto.setId(id);
        return dto;
    }

    public static StorageDto storageDto2(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/storage/dto/storageDto2.json", StorageDto.class);
        dto.setId(id);
        return dto;
    }

    public static CreateStorageDto createStorageDto1(String beerId, String storeId) {
        var createDto = FileReaderUtility.readFile("/controllerFiles/createStorage/createStorageDto1.json", CreateStorageDto.class);
        createDto.setBeer(beerId);
        createDto.setStore(storeId);
        return createDto;
    }

    public static CreateStorageDto createStorageDto1() {
        return FileReaderUtility.readFile("/controllerFiles/createStorage/createStorageDto1.json", CreateStorageDto.class);
    }
}
