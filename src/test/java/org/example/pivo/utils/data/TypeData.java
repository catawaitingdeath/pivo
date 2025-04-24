package org.example.pivo.utils.data;

import org.example.pivo.model.entity.TypeEntity;

import java.math.BigInteger;

public class TypeData {

    public static TypeEntity typeEntityLager(BigInteger id) {
        return TypeEntity.builder()
                .id(id)
                .name("лагер")
                .build();
    }

    public static TypeEntity typeEntityAle(BigInteger id) {
        return TypeEntity.builder()
                .id(id)
                .name("эль")
                .build();
    }
}
