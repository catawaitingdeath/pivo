package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.CreateStorageDto;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.service.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
@Tag(name = "Контроллер складов")
public class StorageController {
    private final StorageService storageService;

    @Operation(summary = "Сохранение сущности склада в репозиторий")
    @PostMapping
    public StorageDto post(@Valid @RequestBody CreateStorageDto storageDto) {
        return storageService.create(storageDto);
    }

    @Operation(summary = "Поиск всех складов в репозитории")
    @GetMapping
    public Page<StorageDto> getAll(@RequestParam @Parameter(description = "Номер выводимой страницы", example = "5") Integer pageNumber,
                                   @RequestParam @Parameter(description = "Количество элементов на странице", example = "10") Integer pageSize) {

        return storageService.getAll(pageNumber, pageSize);
    }

    @Operation(summary = "Поиск склада в репозитории по его id")
    @GetMapping("/{id}")
    public StorageDto getStore(@NotBlank @PathVariable @Parameter(description = "Id искомого склада", example = "W_cPwW5eqk9kxe2OxgivJzVgu") String id) {
        return storageService.get(id);
    }
}
