package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundStoreException extends PivoException {
    public NotFoundStoreException(String message) {
        super(message);
    }

    public static NotFoundStoreException of() {
        return new NotFoundStoreException(null);
    }

    public static NotFoundStoreException of(String message) {
        return new NotFoundStoreException(message);
    }

    public static NotFoundStoreException of(@NotNull String message, @NotNull Object... args) {
        return NotFoundStoreException.of(message.formatted(args));
    }
}
