package org.example.pivo.utils.data;

import org.example.pivo.model.dto.CreateStorageDto;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.utils.FileReaderUtility;

public class StorageData {

    public static StorageEntity storageEntity100(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity100.json", StorageEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StorageEntity storageEntity10(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity10.json", StorageEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StorageEntity storageEntity100() {
        return FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity100.json", StorageEntity.class);
    }

    public static StorageEntity storageEntity10() {
        return FileReaderUtility.readFile("/controllerFiles/storage/entity/storageEntity10.json", StorageEntity.class);
    }

    public static StorageDto storageDto100(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/storage/dto/storageDto100.json", StorageDto.class);
        dto.setId(id);
        return dto;
    }

    public static StorageDto storageDto10(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/storage/dto/storageDto10.json", StorageDto.class);
        dto.setId(id);
        return dto;
    }

    public static CreateStorageDto createStorageDto100(String beerId, String storeId) {
        var createDto = FileReaderUtility.readFile("/controllerFiles/createStorage/createStorageDto100.json", CreateStorageDto.class);
        createDto.setBeer(beerId);
        createDto.setStore(storeId);
        return createDto;
    }

    public static CreateStorageDto createStorageDto100() {
        return FileReaderUtility.readFile("/controllerFiles/createStorage/createStorageDto100.json", CreateStorageDto.class);
    }
}
