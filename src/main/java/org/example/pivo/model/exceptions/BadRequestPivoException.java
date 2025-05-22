package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestPivoException extends PivoException {
    public BadRequestPivoException(String message) {
        super(message);
    }

    public static BadRequestPivoException of() {
        return new BadRequestPivoException(null);
    }

    public static BadRequestPivoException of(String message) {
        return new BadRequestPivoException(message);
    }

    public static BadRequestPivoException of(@NotNull String message, @NotNull Object... args) {
        return BadRequestPivoException.of(message.formatted(args));
    }
}
