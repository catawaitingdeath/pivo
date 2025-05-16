package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@Tag(name = "Контроллер магазинов")
public class StoreController {
    private final StoreService storeService;

    @Operation(summary = "Сохранение сущности магазина в репозиторий")
    @PostMapping
    public StoreDto post(@Valid @RequestBody CreateStoreDto storeDto) {
        return storeService.create(storeDto);
    }

    @Operation(summary = "Поиск всех магазинов в репозитории")
    @GetMapping
    public Page<StoreDto> getAll(@RequestParam @Parameter(description = "Номер выводимой страницы", example = "5") Integer pageNumber,
                                 @RequestParam @Parameter(description = "Количество элементов на странице", example = "10") Integer pageSize) {

        return storeService.getAll(pageNumber, pageSize);
    }

    @Operation(summary = "Поиск магазина в репозитории по его id")
    @GetMapping("/{id}")
    public StoreDto getStore(@NotBlank @PathVariable @Parameter(description = "Id искомого магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu") String id) {
        return storeService.get(id);
    }
}