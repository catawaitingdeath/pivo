package org.example.pivo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pivo.annotation.Normalize;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateBeerDto {

    @Schema(example = "Жигули Барное светлое фильтрованное", description = "Название пива с большой буквы")
    @NotEmpty(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Название пива не должно содержать больше 100 символов")
    private String name;

    @Normalize("#this == null ? null : #this.substring(0,1).toUpperCase() + #this.substring(1).toLowerCase()")
    @Schema(example = "Московская пивоваренная компания", description = "Название производителя с большой буквы")
    @NotEmpty(message = "Поле не может быть пустым")
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
