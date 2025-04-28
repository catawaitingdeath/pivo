package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorPivoException extends PivoException {
    public InternalErrorPivoException(String message) {
        super(message);
    }

    public static InternalErrorPivoException of() {
        return new InternalErrorPivoException(null);
    }

    public static InternalErrorPivoException of(String message) {
        return new InternalErrorPivoException(message);
    }

    public static InternalErrorPivoException of(@NotNull String message, @NotNull Object... args) {
        return InternalErrorPivoException.of(message.formatted(args));
    }
}

