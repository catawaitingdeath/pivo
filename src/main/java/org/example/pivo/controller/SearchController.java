package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/beer/search")
@RequiredArgsConstructor
@Tag(name = "Контроллер поиска пив")
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "Регистронезависимый поиск пива по части названия")
    @GetMapping("/by-name")
    public Page<BeerDto> searchByName(@NotBlank @RequestParam @Parameter(description = "Полное или частичное название", example = "Troll") String name,
                                      @RequestParam @Parameter(description = "Номер выводимой страницы", example = "5") Integer pageNumber,
                                      @RequestParam @Parameter(description = "Количество элементов на странице", example = "10") Integer pageSize) {
        return searchService.searchByName(name, pageNumber, pageSize);
    }

    @Operation(summary = "Поиск пива по заданным критериям (производитель, тип, цена, содержание алкоголя)")
    @GetMapping("/by-criteria")
    public Page<BeerDto> searchByCriteria(@RequestParam(required = false) String producer,
                                          @RequestParam(required = false) BigDecimal minAlcohol,
                                          @RequestParam(required = false) BigDecimal maxAlcohol,
                                          @RequestParam(required = false) BigDecimal minPrice,
                                          @RequestParam(required = false) BigDecimal maxPrice,
                                          @RequestParam(required = false) String type,
                                          @RequestParam @Parameter(description = "Номер выводимой страницы", example = "5") Integer pageNumber,
                                          @RequestParam @Parameter(description = "Количество элементов на странице", example = "10") Integer pageSize) {
        return searchService.searchByCriteria(producer, minAlcohol, maxAlcohol, minPrice, maxPrice, type, pageNumber, pageSize);
    }

    @Operation(summary = "Поиск магазинов, где в продаже есть конкретное пиво")
    @GetMapping("/in-stock")
    public List<StoreDto> searchInStock(@NotBlank @RequestParam(required = false) @Parameter(description = "Полное название", example = "Troll Brew IPA светлое нефильтрованное") String beerName) {
        return searchService.searchInStock(beerName);
    }

    @Operation(summary = "Поиск магазинов, где в продаже есть все позиции из списка пива")
    @GetMapping("/stores")
    public Set<StoreDto> searchForStores(@NotBlank @RequestParam(required = false) @Parameter(description = "список сущностей искомых пив") List<String> beers) {
        return searchService.searchForStores(beers);
    }

    @Operation(summary = "Поиск пива, которое есть в продаже в конкретном магазине")
    @GetMapping("/certain-store")
    public Page<BeerDto> searchForBeers(@NotBlank @RequestParam(required = false) @Parameter(description = "Id конкретного магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu") String storeId,
                                        @RequestParam @Parameter(description = "Номер выводимой страницы", example = "5") Integer pageNumber,
                                        @RequestParam @Parameter(description = "Количество элементов на странице", example = "10") Integer pageSize) {
        return searchService.searchForBeer(storeId, pageNumber, pageSize);
    }
}
