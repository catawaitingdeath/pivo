package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.service.BeerService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/beer")
@RequiredArgsConstructor
@Tag(name = "Контроллер пива")
public class BeerController {
    private final BeerService beerService;
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Operation(summary = "Сохранение сущности пива в репозиторий")
    @PostMapping
    public BeerDto post(@Valid @RequestBody CreateBeerDto beerDto) {
        return beerService.create(beerDto);
    }

    @Operation(summary = "Поиск всех сущностей пива в репозитории")
    @GetMapping
    public List<BeerDto> getAll() {
        return beerService.getAll();
    }

    @Operation(summary = "Поиск пива в репозитории по его id")
    @GetMapping("/{id}")
    public BeerDto getBeer(@NotBlank @PathVariable @Parameter(description = "Id искомого пива", example = "W_cPwW5eqk9kxe2OxgivJzVgu") String id) {
        return beerService.get(id);
    }

    @Operation(summary = "Поиск пива, у которого цена больше или равна переданной и алкоголь равен переданному")
    @GetMapping("/custom")
    public List<BeerEntity> getBeer(@RequestParam @Parameter(description = "Миниальная цена искомого пива", example = "100.99") BigDecimal price, @RequestParam BigDecimal alcohol) {
        return beerService.custom(price, alcohol);
    }

    @Operation(summary = "Регистронезависимый поиск пива по части названия")
    @GetMapping("/searchByName")
    public List<BeerDto> searchByName(@NotBlank @RequestParam @Parameter(description = "Полное или частичное название", example = "Troll") String name) {
        return beerService.caseInsensitiveSearch(name);
    }

    @Operation(summary = "Поиск пива по заданным критериям (производитель, тип, цена, содержание алкоголя)")
    @GetMapping("/searchByCriteria")
    public List<BeerDto> searchByCriteria(@RequestParam(required = false) String producer,
                                @RequestParam(required = false) BigDecimal minAlcohol,
                                @RequestParam(required = false) BigDecimal maxAlcohol,
                                @RequestParam(required = false) BigDecimal minPrice,
                                @RequestParam(required = false) BigDecimal maxPrice,
                                @RequestParam(required = false) String type) {
        return beerService.searchByCriteria(producer, minAlcohol, maxAlcohol, minPrice, maxPrice, type);
    }
}
