package org.example.pivo.client;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class EmployeeMapper {

    public abstract EmployeeDto toDto(CreateEmployeeDto dto);

    public abstract CreateEmployeeDto toCreateDto(EmployeeDto dto);
    
    public abstract StoreEmployeeDto toStoreEmployeeDto(String id, List<EmployeeDto> employees);
}
