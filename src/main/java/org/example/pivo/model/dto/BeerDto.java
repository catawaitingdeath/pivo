package org.example.pivo.model.dto;

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

    @NotBlank
    @Size(min = 25, max = 25)
    private String id;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Название пива не должно содержать больше 100 символов")
    private String name;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Имя производителя пива не должно содержать больше 50 символов")
    private String producer;

    @NotNull
    @Positive(message = "Цена пива должна быть положительной")
    private BigDecimal price;

    @NotNull
    @Positive(message = "Процентное содержание алкоголя должно быть положительным")
    private BigDecimal alcohol;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 20, message = "Тип пива не должен содержать больше 20 символов")
    private String typeName;
}
