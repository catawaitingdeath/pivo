package org.example.pivo.mapper;

import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.StoreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class StoreMapper {

    public abstract StoreDto toDto(StoreEntity entity);
    public abstract StoreEntity toEntity(CreateStoreDto dto);
}
