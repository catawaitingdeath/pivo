package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends PivoException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of() {
        return new NotFoundException(null);
    }

    public static NotFoundException of(String message) {
        return new NotFoundException(message);
    }

    public static NotFoundException of(@NotNull String message, @NotNull Object... args) {
        return NotFoundException.of(message.formatted(args));
    }
}
