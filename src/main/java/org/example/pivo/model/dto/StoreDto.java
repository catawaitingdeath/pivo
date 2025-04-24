package org.example.pivo.model.dto;


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
public class StoreDto {

    @NotBlank
    @Size(min = 25, max = 25)
    private String id;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Адрес магазина не должен содержать больше 50 символов")
    private String address;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 25, message = "Телефон магазина не должен содержать больше 25 символов")
    private String phone;
}
