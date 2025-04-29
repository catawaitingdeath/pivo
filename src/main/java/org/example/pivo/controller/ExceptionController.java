package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Контроллер исключений")
public class ExceptionController {

    @Operation(summary = "Выбрасывает пивную ошибку")
    @GetMapping("/NotFoundPivoException")
    public void notFoundException() {
        throw NotFoundPivoException.of("Пиво . Какое-то там пиво не найдено");
    }

    @Operation(summary = "Выбрасывает внутреннюю ошибку")
    @GetMapping("/InternalErrorPivoException")
    public void internalErrorPivoException() {
        throw InternalErrorPivoException
                .of("Внутренняя ошибка. А дальше короткий ряд простых чисел: %s, %s, %s, %s", 2, 3, 5, 7);
    }

    @Operation(summary = "Выбрасывает ошибку \"I am a teapot\"")
    @GetMapping("/IAmATeapotPivoException")
    public void iAmATeapotException() {
        throw IAmATeapotPivoException.of();
    }

}
