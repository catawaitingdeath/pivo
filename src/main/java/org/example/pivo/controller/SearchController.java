package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.service.BeerService;
import org.example.pivo.service.SearchService;
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
    private final SearchService searchService;

    @Operation(summary = "Регистронезависимый поиск пива по части названия")
    @GetMapping("/by-name")
    public List<BeerDto> searchByName(@NotBlank @RequestParam @Parameter(description = "Полное или частичное название", example = "Troll") String name) {
        return searchService.searchByName(name);
    }

    @Operation(summary = "Поиск пива по заданным критериям (производитель, тип, цена, содержание алкоголя)")
    @GetMapping("/by-criteria")
    public List<BeerDto> searchByCriteria(@RequestParam(required = false) String producer,
                                          @RequestParam(required = false) BigDecimal minAlcohol,
                                          @RequestParam(required = false) BigDecimal maxAlcohol,
                                          @RequestParam(required = false) BigDecimal minPrice,
                                          @RequestParam(required = false) BigDecimal maxPrice,
                                          @RequestParam(required = false) String type) {
        return searchService.searchByCriteria(producer, minAlcohol, maxAlcohol, minPrice, maxPrice, type);
    }

    @Operation(summary = "Поиск магазинов, где в продаже есть конкретное пиво")
    @GetMapping("/in-stock")
    public List<StoreDto> searchInStock(@NotBlank @RequestParam(required = false) @Parameter(description = "Полное название", example = "Troll Brew IPA светлое нефильтрованное") String beerName) {
        return searchService.searchInStock(beerName);
    }
}
