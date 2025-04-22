package org.example.pivo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.service.BeerService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/beer")
@RequiredArgsConstructor
public class BeerController {
    private final BeerService beerService;

    @PostMapping
    public BeerDto post(@Valid @RequestBody CreateBeerDto beerDto) {
        return beerService.create(beerDto);
    }

    @GetMapping
    public List<BeerDto> getAll() {
        return beerService.getAll();
    }

    @GetMapping("/{id}")
    public BeerDto getBeer(@NotBlank @PathVariable String id) {
        return beerService.get(id).orElse(null);
    }

    @GetMapping("/custom")
    public List<BeerEntity> getBeer(@RequestParam BigDecimal price, @RequestParam BigDecimal alcohol) {
        return beerService.custom(price, alcohol);
    }
}
