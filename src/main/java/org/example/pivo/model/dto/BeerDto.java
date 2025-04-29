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

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BeerDto {

    @Schema(example = "W_cPwW5eqk9kxe2OxgivJzVgu", description = "Id в формате NanoId")
    @NotBlank
    @Size(min = 25, max = 25)
    private String id;

    @Schema(example = "Жигули Барное светлое фильтрованное", description = "Название пива с большой буквы")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Название пива не должно содержать больше 100 символов")
    private String name;

    @Schema(example = "Московская пивоваренная компания", description = "Название производителя с большой буквы")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Имя производителя пива не должно содержать больше 50 символов")
    private String producer;

    @Schema(example = "60", description = "Цена пива в рублях (формат BigDecimal)")
    @NotNull
    @Positive(message = "Цена пива должна быть положительной")
    private BigDecimal price;

    @Schema(example = "4.5", description = "Процент алкоголя в пиве (формат BigDecimal)")
    @NotNull
    @Positive(message = "Процентное содержание алкоголя должно быть положительным")
    private BigDecimal alcohol;

    @Schema(example = "лагер", description = "Тип пива с маленькой буквы")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 20, message = "Тип пива не должен содержать больше 20 символов")
    private String typeName;
}
