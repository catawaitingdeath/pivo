package org.example.pivo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BeerInStockDto {

    @Schema(description = "Объект BeerDto")
    @NotNull
    private BeerDto beerDto;

    @Schema(example = "60", description = "Количество пива в наличии (формат BigInteger)")
    @NotNull
    @PositiveOrZero(message = "Количество пива должно быть неотрицательным")
    private BigInteger count;
}
