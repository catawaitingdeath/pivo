package org.example.pivo.utils.data;

import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.StoreEntity;
import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.utils.FileReaderUtility;

public class StoreData {

    public static StoreEntity storeEntity1(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntity1.json", StoreEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StoreEntity storeEntity2(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntity2.json", StoreEntity.class);
        entity.setId(id);
        return entity;
    }

    public static StoreEntity storeEntity1() {
        return FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntity1.json", StoreEntity.class);
    }

    public static StoreEntity storeEntity2() {
        return FileReaderUtility.readFile("/controllerFiles/store/entity/storeEntity2.json", StoreEntity.class);
    }

    public static StoreDto storeDto1(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/store/dto/storeDto1.json", StoreDto.class);
        dto.setId(id);
        return dto;
    }

    public static StoreDto storeDto2(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/store/dto/storeDto2.json", StoreDto.class);
        dto.setId(id);
        return dto;
    }

    public static CreateStoreDto createStoreDto1() {
        return FileReaderUtility.readFile("/controllerFiles/createStore/createStoreDto1.json", CreateStoreDto.class);
    }

    public static CreateStoreDto CreateStoreDto2() {
        return FileReaderUtility.readFile("/controllerFiles/createStore/createStoreDto2.json", CreateStoreDto.class);
    }
}
