package org.example.pivo.utils.data;

import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.StoreEntity;
import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.utils.FileReaderUtility;

public class StoreData {

    public static StoreEntity storeEntityLenigradskoe(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntityLenigradskoe.json", StoreEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StoreEntity storeEntityProstornaya(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntityProstornaya.json", StoreEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StoreEntity storeEntityLenigradskoe() {
        return FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntityLenigradskoe.json", StoreEntity.class);
    }

    public static StoreEntity storeEntityProstornaya() {
        return FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntityProstornaya.json", StoreEntity.class);
    }

    public static StoreEntity storeEntityLetnaya() {
        return FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntityLetnaya.json", StoreEntity.class);
    }

    public static StoreEntity storeEntityDzerzhinskogo() {
        return FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntityDzerzhinskogo.json", StoreEntity.class);
    }

    public static StoreDto storeDtoLeningradskoe(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/store/dto/storeDtoLeningradskoe.json", StoreDto.class);
        dto.setId(id);
        return dto;
    }

    public static StoreDto storeDtoProstornaya(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/store/dto/storeDtoProstornaya.json", StoreDto.class);
        dto.setId(id);
        return dto;
    }

    public static CreateStoreDto createStoreDtoLeningradskoe() {
        return FileReaderUtility.readFile("/controllerFiles/createStore/createStoreDtoLeningradskoe.json", CreateStoreDto.class);
    }
}
