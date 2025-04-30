package org.example.pivo.utils.data;

import org.example.pivo.components.NanoIdGenerator;
import org.example.pivo.config.property.NanoIdProperty;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.utils.FileReaderUtility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class BeerData {

    public static BeerEntity beerEntityAle(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/beer/entity/beerEntityAle.json", BeerEntity.class);
        entity.setId(id);
        return entity;
    }

    public static BeerEntity beerEntityLager(String id) {
        var entity = FileReaderUtility.readFile("/controllerFiles/beer/entity/beerEntityLager.json", BeerEntity.class);
        entity.setId(id);
        return entity;
    }

    public static BeerEntity beerEntityAle() {
        return FileReaderUtility.readFile("/controllerFiles/beer/entity/beerEntityAle.json", BeerEntity.class);
    }

    public static BeerEntity beerEntityLager() {
        return FileReaderUtility.readFile("/controllerFiles/beer/entity/beerEntityLager.json", BeerEntity.class);
    }

    public static BeerDto beerDtoAle(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/beer/dto/beerDtoAle.json", BeerDto.class);
        dto.setId(id);
        return dto;
    }

    public static BeerDto beerDtoLager(String id) {
        var dto = FileReaderUtility.readFile("/controllerFiles/beer/dto/beerDtoLager.json", BeerDto.class);
        dto.setId(id);
        return dto;
    }

    public static CreateBeerDto createBeerDtoAle() {
        return FileReaderUtility.readFile("/controllerFiles/createBeer/createBeerDtoAle.json", CreateBeerDto.class);
    }

    public static CreateBeerDto createBeerDtoLager() {
        return FileReaderUtility.readFile("/controllerFiles/createBeer/createBeerDtoLager.json", CreateBeerDto.class);
    }
}
