package org.example.pivo.client;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class EmployeeMapper {

    public abstract EmployeeDto toDto(CreateEmployeeDto dto);

    public abstract CreateEmployeeDto toCreateDto(EmployeeDto dto);
}
