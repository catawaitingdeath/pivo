package org.example.pivo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.pivo.client.CreateEmployeeDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class CreateStoreWithEmployeeDto extends CreateStoreDto {

    private List<CreateEmployeeDto> employees;

    public CreateStoreWithEmployeeDto updateStoreId(String id) {
        employees = Optional.ofNullable(employees).orElse(new ArrayList<>())
                .stream()
                .map(CreateEmployeeDto::toBuilder)
                .map(e -> e.store(id))
                .map(CreateEmployeeDto.CreateEmployeeDtoBuilder::build)
                .toList();
        return this;
    }
}
