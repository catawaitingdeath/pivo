package org.example.pivo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.service.BeerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/beer")
@RequiredArgsConstructor
public class BeerController {
    private final BeerService beerService;

    @PostMapping
    public BeerDto post(@RequestBody CreateBeerDto beerDto) {
        return beerService.create(beerDto);
    }

    @GetMapping
    public List<BeerDto> getAll() {
        return beerService.getAll();
    }

    @GetMapping("/{id}")
    public BeerDto getBeer(@PathVariable Long id) {
        return beerService.get(id).orElse(null);
    }

    @GetMapping("/custom")
    public List<BeerEntity> getBeer(@RequestParam Long price, @RequestParam Long alcohol) {
        return beerService.custom(price, alcohol);
    }
}
