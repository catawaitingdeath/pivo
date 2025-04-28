package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundPivoException extends PivoException {
    public NotFoundPivoException(String message) {
        super(message);
    }

    public static NotFoundPivoException of() {
        return new NotFoundPivoException(null);
    }

    public static NotFoundPivoException of(String message) {
        return new NotFoundPivoException(message);
    }

    public static NotFoundPivoException of(@NotNull String message, @NotNull Object... args) {
        return NotFoundPivoException.of(message.formatted(args));
    }
}
