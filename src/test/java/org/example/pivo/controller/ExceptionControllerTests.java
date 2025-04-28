package org.example.pivo.controller;

import org.example.pivo.model.exceptions.IAmATeapotPivoException;
import org.example.pivo.model.exceptions.InternalErrorPivoException;
import org.example.pivo.model.exceptions.NotFoundPivoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Эти тесты можно удалить как только ты разберешься с идеей
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
public class ExceptionControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception/{exceptionType}",
                        NotFoundPivoException.class.getSimpleName()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Пиво . Какое-то там пиво не найдено"))
                .andDo(print());

    }

    @Test
    void internalError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception/{exceptionType}",
                        InternalErrorPivoException.class.getSimpleName()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Внутренняя ошибка. А дальше короткий ряд простых чисел: 2, 3, 5, 7"))
                .andDo(print());

    }

    @Test
    void iAmATeapot() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exception/{exceptionType}",
                        IAmATeapotPivoException.class.getSimpleName()))
                .andExpect(status().isIAmATeapot())
                .andExpect(jsonPath("$.message")
                        .value("I'm a teapot"))
                .andDo(print());

    }
}
