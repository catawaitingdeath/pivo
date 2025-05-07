package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.TypeEntity;
import org.example.pivo.model.exceptions.NotFoundPivoException;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.TypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeerService {
    private final BeerRepository beerRepository;
    private final TypeRepository typeRepository;
    private final BeerMapper beerMapper;

    public BeerDto create(CreateBeerDto beer) {
        BeerEntity beerEntity = beerMapper.toEntity(beer);
        var typeEntity = typeRepository.findByName(beer.getTypeName())
                .orElseThrow(()-> new NotFoundPivoException("Тип не найден"));
        beerEntity.setType(typeEntity.getId());
        beerEntity = beerRepository.save(beerEntity);
        return beerMapper.toDto(beerEntity, typeEntity);
    }

    public List<BeerDto> getAll() {
        var result = new ArrayList<BeerDto>();
        var beers = beerRepository.findAll();
        var beerTypes = beers.stream()
                .map(BeerEntity::getType)
                .distinct()
                .map(typeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(TypeEntity::getId, Function.identity()));
        beers.forEach(t -> result.add(beerMapper.toDto(t, beerTypes.get(t.getType()))));
        return result;
    }

    public BeerDto get(String id) {
        var beerEntity = beerRepository.findById(id)
                .orElseThrow(()-> new NotFoundPivoException("Предоставлен неверный id"));
        return beerMapper.toDto(
                beerEntity, typeRepository.findById(beerEntity.getType()).get());
    }

    public List<BeerEntity> custom(BigDecimal price, BigDecimal alcohol) {
        return beerRepository.findAllByPriceGreaterThanAndAlcoholOrderByPriceDesc(price, alcohol);
    }
}
