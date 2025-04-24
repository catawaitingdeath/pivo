package org.example.pivo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.AnotherStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.service.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public StoreDto post(@Valid @RequestBody AnotherStoreDto storeDto) {
        return storeService.create(storeDto);
    }

    @GetMapping
    public List<StoreDto> getAll() {
        return storeService.getAll();
    }

    @GetMapping("/{id}")
    public StoreDto getStore(@NotBlank @PathVariable String id) {
        return storeService.get(id).orElse(null);
}
