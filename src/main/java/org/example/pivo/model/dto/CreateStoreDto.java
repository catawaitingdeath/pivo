package org.example.pivo.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreDto {

    @Schema(example = "Ленинградское ш., 58с53, Москва", description = "Адрес магазина с большой буквы")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Адрес магазина не должен содержать больше 50 символов")
    private String address;

    @Schema(example = "79684551122", description = "Номер телефона магазина")
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 25, message = "Телефон магазина не должен содержать больше 25 символов")
    private String phone;
}
