package org.example.pivo.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateStorageDto {

    @Schema(description = "Id пива в формате NanoId", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 25, max = 25, message = "Id пива должен содержать 25 символов")
    private String beer;

    @Schema(description = "Id магазина в формате NanoId", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 25, max = 25, message = "Id магазина должен содержать 25 символов")
    private String store;

    @Schema(description = "Количество бутылок пива на складе (формат BigInteger)", example = "6")
    @NotNull
    @Positive(message = "Процентное содержание алкоголя должно быть положительным")
    private BigInteger count;
}
