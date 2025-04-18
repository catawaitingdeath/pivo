package org.example.pivo.config.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "beer-application.nanoid")
public class NanoIdProperty {

    @NotBlank
    @Pattern(regexp = "^(?!.*(.).*\1).{2,}$", message = "Строка должна содержать минимум два уникальных символа")
    private String alphabet;

    @NotNull
    @Positive(message = "Размер nanoId должен быть положительным")
    private Integer size;

}
