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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public Page<BeerDto> getAll(Integer pageNumber, Integer pageSize) {
        var result = new ArrayList<BeerDto>();
        var beers = beerRepository.findAll(PageRequest.of(pageNumber, pageSize));
        if(beers == null){
            return Page.empty();
        }
        var typeIds = beers.stream()
                .map(BeerEntity::getType)
                .collect(Collectors.toSet());
        var beerTypes = typeRepository.findByIdIn(typeIds).stream()
                .collect(Collectors.toMap(TypeEntity::getId, TypeEntity::getName));

        beers.forEach(t -> result.add(beerMapper.toDto(t, beerTypes.get(t.getType()))));
        return new PageImpl<>(result, beers.getPageable(), beers.getTotalElements());
    }

    public BeerDto get(String id) {
        var beerEntity = beerRepository.findById(id)
                .orElseThrow(()-> new NotFoundPivoException("Предоставлен неверный id"));
        var type = typeRepository.findById(beerEntity.getType())
                .orElseThrow(() -> new NotFoundPivoException("Тип не найден"));
        return beerMapper.toDto(beerEntity, type);
    }

    public List<BeerEntity> custom(BigDecimal price, BigDecimal alcohol) {
        return beerRepository.findAllByPriceGreaterThanAndAlcoholOrderByPriceDesc(price, alcohol);
    }
}
