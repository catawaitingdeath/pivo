package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.model.entity.StoreEntity;
import org.example.pivo.model.entity.TypeEntity;
import org.example.pivo.model.exceptions.NotFoundException;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.repository.TypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BeerRepository beerRepository;
    private final TypeRepository typeRepository;
    private final StorageRepository storageRepository;
    private final StoreRepository storeRepository;
    private final BeerMapper beerMapper;
    private final StoreMapper storeMapper;
    private final BeerSpecification beerSpecification;

    public Page<BeerDto> searchByName(String name, Integer pageNumber, Integer pageSize) {
        List<BeerEntity> beers = beerRepository.findByNameContainingIgnoreCase(name);
        var result = beers.stream()
                .map(b -> beerMapper.toDto(b, typeRepository.findById(b.getType())
                        .orElseThrow(() -> new NotFoundException("Тип не найден"))))
                .toList();
        return new PageImpl<>(result, PageRequest.of(pageNumber, pageSize), beers.size());
    }

    public Page<BeerDto> searchByCriteria(String producer, BigDecimal minAlcohol, BigDecimal maxAlcohol, BigDecimal minPrice, BigDecimal maxPrice, String type, Integer pageNumber, Integer pageSize) {
        Specification<BeerEntity> spec = Specification.where(null);

        if (producer != null) {
            spec = spec.and(beerSpecification.hasProducer(producer));
        }
        if (minAlcohol != null) {
            spec = spec.and(beerSpecification.alcoholGreaterThan(minAlcohol));
        }
        if (maxAlcohol != null) {
            spec = spec.and(beerSpecification.alcoholLessThan(maxAlcohol));
        }
        if (minPrice != null) {
            spec = spec.and(beerSpecification.priceGreaterThan(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(beerSpecification.priceLessThan(maxPrice));
        }
        if (type != null) {
            spec = spec.and(beerSpecification.hasType(type));
        }
        List<BeerEntity> all = beerRepository.findAll(spec);
        var result = all.stream()
                .map(b -> beerMapper.toDto(b, typeRepository.findById(b.getType())
                        .orElseThrow(() -> new NotFoundException("Тип не найден"))))
                .toList();
        return new PageImpl<>(result, PageRequest.of(pageNumber, pageSize), all.size());
    }

    public List<StoreDto> searchInStock(String beerName) {
        var beerId = beerRepository.findByName(beerName).getId();
        var storages = storageRepository.findAllByBeerAndCountGreaterThan(beerId, BigInteger.ZERO);
        if (storages.isEmpty()) {
            return List.of();
        }
        List<StoreEntity> all = storages.stream()
                .map(b -> storeRepository.findById(b.getStore())
                        .orElseThrow(()-> new NotFoundException("Магазин не найден")))
                .toList();
        return all.stream()
                .map(storeMapper::toDto)
                .toList();
    }

    public Set<StoreDto> searchForStores(List<String> beers) {
        Set<StoreDto> stores = new HashSet<>(searchInStock(beers.getFirst()));
        for (String beer : beers) {
            var storesList = searchInStock(beer);
            stores.retainAll(storesList);
            if (stores.isEmpty()) {
                return Set.of();
            }
        }
        return stores;
    }

    public Page<BeerDto> searchForBeer(String storeId, Integer pageNumber, Integer pageSize) {
        var storages = storageRepository.findAll(beerSpecification.correctStorages(storeId));
        if(storages == null) {
            return Page.empty();
        }
        var result = new ArrayList<BeerDto>();
        var beerIds = storages.stream()
                .map(StorageEntity::getBeer)
                .toList();
        var beers = beerIds.stream()
                .map(beerRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        var typeIds = beers.stream()
                .map(BeerEntity::getType)
                .toList();
        var types = typeIds.stream()
                .map(typeRepository::findById)
                .toList();
        var beerTypes = types.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(TypeEntity::getId, TypeEntity::getName));

        beers.forEach(t -> result.add(beerMapper.toDto(t, beerTypes.get(t.getType()))));
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new PageImpl<>(result, pageable, beers.size());
    }
}
