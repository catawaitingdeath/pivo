package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.service.BeerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/beer/search")
@RequiredArgsConstructor
@Tag(name = "Контроллер поиска пив")
public class SearchController {
    private final BeerService beerService;

    @Operation(summary = "Регистронезависимый поиск пива по части названия")
    @GetMapping("/by-name")
    public List<BeerDto> searchByName(@NotBlank @RequestParam @Parameter(description = "Полное или частичное название", example = "Troll") String name) {
        return beerService.caseInsensitiveSearch(name);
    }

    @Operation(summary = "Поиск пива по заданным критериям (производитель, тип, цена, содержание алкоголя)")
    @GetMapping("/by-criteria")
    public List<BeerDto> searchByCriteria(@RequestParam(required = false) String producer,
                                          @RequestParam(required = false) BigDecimal minAlcohol,
                                          @RequestParam(required = false) BigDecimal maxAlcohol,
                                          @RequestParam(required = false) BigDecimal minPrice,
                                          @RequestParam(required = false) BigDecimal maxPrice,
                                          @RequestParam(required = false) String type) {
        return beerService.searchByCriteria(producer, minAlcohol, maxAlcohol, minPrice, maxPrice, type);
    }
}
