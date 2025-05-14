package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundStorageException extends PivoException {
    public NotFoundStorageException(String message) {
        super(message);
    }

    public static NotFoundStorageException of() {
        return new NotFoundStorageException(null);
    }

    public static NotFoundStorageException of(String message) {
        return new NotFoundStorageException(message);
    }

    public static NotFoundStorageException of(@NotNull String message, @NotNull Object... args) {
        return NotFoundStorageException.of(message.formatted(args));
    }
}
