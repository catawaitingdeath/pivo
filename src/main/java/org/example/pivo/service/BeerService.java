package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.TypeEntity;
import org.example.pivo.model.exceptions.NotFoundException;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.TypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
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
                .orElseThrow(()-> new NotFoundException("Тип не найден"));
        beerEntity.setType(typeEntity.getId());
        beerEntity = beerRepository.save(beerEntity);
        return beerMapper.toDto(beerEntity, typeEntity);
    }

    public Page<BeerDto> getAll(Integer pageNumber, Integer pageSize) {
        var result = new ArrayList<BeerDto>();
        var beers = beerRepository.findAll(PageRequest.of(pageNumber, pageSize));
        var beerTypes = beers.stream()
                .map(BeerEntity::getType)
                .distinct()
                .map(typeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(TypeEntity::getId, Function.identity()));

        beers.forEach(t -> result.add(beerMapper.toDto(t, beerTypes.get(t.getType()))));
        return new PageImpl<>(result, beers.getPageable(), beers.getTotalElements());
    }

    public BeerDto get(String id) {
        var beerEntity = beerRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Предоставлен неверный id"));
        return beerMapper.toDto(
                beerEntity, typeRepository.findById(beerEntity.getType()).get());
    }

    public List<BeerEntity> custom(BigDecimal price, BigDecimal alcohol) {
        return beerRepository.findAllByPriceGreaterThanAndAlcoholOrderByPriceDesc(price, alcohol);
    }

    public List<BeerDto> caseInsensitiveSearch(String name) {
        //https://stackoverflow.com/questions/47752663/how-to-search-on-all-fields-with-partial-search-string-in-spring-data-elastic-se
        List<BeerEntity> result = beerRepository.findByNameIgnoreCase(name);
        return result.stream()
                .map(b -> beerMapper.toDto(b, typeRepository.findById(b.getType()).get()))
                .toList();
    }

    public List<BeerDto> searchByCriteria(String producer, BigDecimal minAlcohol, BigDecimal maxAlcohol, BigDecimal minPrice, BigDecimal maxPrice,String type) {
        Specification<BeerEntity> spec = Specification.where(null);

        if (producer != null) {
            spec = spec.and(BeerSpecification.hasProducer(producer));
        }
        if (minAlcohol != null) {
            spec = spec.and(BeerSpecification.alcoholGreaterThan(minAlcohol));
        }
        if (maxAlcohol != null) {
            spec = spec.and(BeerSpecification.alcoholLessThan(maxAlcohol));
        }
        if (minPrice != null) {
            spec = spec.and(BeerSpecification.priceGreaterThan(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(BeerSpecification.priceLessThan(maxPrice));
        }
        if (type != null) {
            spec = spec.and(BeerSpecification.hasType(type));
        }
        List<BeerEntity> all = beerRepository.findAll(spec);
        return all.stream()
                .map(b -> beerMapper.toDto(b, typeRepository.findById(b.getType()).get()))
                .toList();
    }

}
