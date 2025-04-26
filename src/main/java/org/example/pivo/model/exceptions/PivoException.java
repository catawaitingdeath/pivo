package org.example.pivo.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class PivoException extends RuntimeException {
    public PivoException(String message) {
        super(message);
    }
}
