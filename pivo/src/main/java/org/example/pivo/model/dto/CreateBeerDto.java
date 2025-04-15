package org.example.pivo.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateBeerDto {

    @NotNull
    private String name;
    @NotNull
    private String producer;
    @NotNull
    private Long price;
    @NotNull
    private Long alcohol;
    @NotNull
    private String typeName;
}
