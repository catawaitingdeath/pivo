package org.example.pivo.model.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class IAmATeapotPivoException extends PivoException {
    public IAmATeapotPivoException(String message) {
        super(message);
    }

    public static IAmATeapotPivoException of() {
        return new IAmATeapotPivoException(null);
    }

    public static IAmATeapotPivoException of(String message) {
        return new IAmATeapotPivoException(message);
    }

    public static IAmATeapotPivoException of(@NotNull String message, @NotNull Object... args) {
        return IAmATeapotPivoException.of(message.formatted(args));
    }
}

