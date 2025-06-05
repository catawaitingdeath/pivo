package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.client.StoreEmployeeDto;
import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.dto.StoreEmployeeInfoDto;
import org.example.pivo.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Page<StoreDto> getAll(
            @RequestParam
            @Parameter(description = "Номер выводимой страницы", example = "5")
            Integer pageNumber,
            @RequestParam
            @Parameter(description = "Количество элементов на странице", example = "10")
            Integer pageSize
    ) {

        return storeService.getAll(pageNumber, pageSize);
    }

    @Operation(summary = "Поиск магазина в репозитории по его id")
    @GetMapping("/{id}")
    public StoreDto getStore(
            @NotBlank
            @PathVariable
            @Parameter(description = "Id искомого магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
            String id
    ) {
        return storeService.get(id);
    }

    @Operation(summary = "Удаление сотрудников магазина и самого магазина из репозитория")
    @DeleteMapping("/{id}")
    public void deleteStore(
            @NotBlank
            @PathVariable
            @Parameter(description = "Id магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
            String id
    ) {
        storeService.delete(id);
    }

    @Operation(summary = "Поиск магазина в репозитории по его id")
    @GetMapping("/{id}/employees")
    public StoreEmployeeInfoDto getStoreEmployeeInfo(
            @NotBlank
            @PathVariable
            @Parameter(description = "Id искомого магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
            String id
    ) {
        return storeService.getStoreEmployeeInfo(id);
    }

    @Operation(summary = "Сохранение работников магазина в репозиторий")
    @PostMapping("/{id}/registerEmployees")
    public StoreEmployeeDto registerEmployees(
            @NotBlank
            @PathVariable
            @Parameter(description = "Id магазина", example = "W_cPwW5eqk9kxe2OxgivJzVgu")
            String id,
            @Valid
            @RequestBody
            List<CreateEmployeeDto> employees

    ) {
        return storeService.registerEmployees(id, employees);
    }
}