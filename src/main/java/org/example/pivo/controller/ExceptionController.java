package org.example.pivo.controller;

import lombok.RequiredArgsConstructor;
import org.example.pivo.model.exceptions.IAmATeapotPivoException;
import org.example.pivo.model.exceptions.InternalErrorPivoException;
import org.example.pivo.model.exceptions.NotFoundPivoException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Этот контроллер можно удалить как только ты разберешься с идеей
 */
@RestController
@RequestMapping("/exception")
@RequiredArgsConstructor
public class ExceptionController {

    @GetMapping("/NotFoundPivoException")
    public void notFoundException() {
        throw NotFoundPivoException.of("Пиво . Какое-то там пиво не найдено");
    }

    @GetMapping("/InternalErrorPivoException")
    public void internalErrorPivoException() {
        throw InternalErrorPivoException
                .of("Внутренняя ошибка. А дальше короткий ряд простых чисел: %s, %s, %s, %s", 2, 3, 5, 7);
    }

    @GetMapping("/IAmATeapotPivoException")
    public void iAmATeapotException() {
        throw IAmATeapotPivoException.of();
    }

}
