package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends PivoException {
    public BadRequestException(String message) {
        super(message);
    }

    public static BadRequestException of() {
        return new BadRequestException(null);
    }

    public static BadRequestException of(String message) {
        return new BadRequestException(message);
    }

    public static BadRequestException of(@NotNull String message, @NotNull Object... args) {
        return BadRequestException.of(message.formatted(args));
    }
}
