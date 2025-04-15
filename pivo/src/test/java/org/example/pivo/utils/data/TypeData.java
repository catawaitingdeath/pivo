package org.example.pivo.utils.data;

import org.example.pivo.model.entity.TypeEntity;

import java.util.Objects;

public class TypeData {

    public static TypeEntity typeEntity(Long id, String name) {
        return TypeEntity.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static TypeEntity newTypeEntityWithId(String typeName) {
        if(Objects.equals(typeName, "lager")){
            return TypeEntity.builder()
                    .id(1L)
                    .name("lager")
                    .build();
        }
        else{
            return TypeEntity.builder()
                    .id(2L)
                    .name("ale")
                    .build();
        }
    }

    public static TypeEntity newTypeEntity(String typeName) {
        return TypeEntity.builder()
                .name(typeName)
                .build();
    }
}
