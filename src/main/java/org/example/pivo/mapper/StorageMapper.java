package org.example.pivo.mapper;

import org.example.pivo.model.dto.CreateStorageDto;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.entity.StorageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class StorageMapper {

    public abstract StorageDto toDto(StorageEntity entity);
    public abstract StorageEntity toEntity(CreateStorageDto dto);
}
