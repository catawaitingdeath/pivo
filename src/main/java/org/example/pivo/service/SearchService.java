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

    public List<BeerDto> searchByName(String name) {
        List<BeerEntity> result = beerRepository.findByNameContainingIgnoreCase(name);
        return result.stream()
                .map(b -> beerMapper.toDto(b, typeRepository.findById(b.getType())
                        .orElseThrow(() -> new NotFoundException("Тип не найден"))))
                .toList();
    }

    public List<BeerDto> searchByCriteria(String producer, BigDecimal minAlcohol, BigDecimal maxAlcohol, BigDecimal minPrice, BigDecimal maxPrice, String type) {
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
        return all.stream()
                .map(b -> beerMapper.toDto(b, typeRepository.findById(b.getType())
                        .orElseThrow(() -> new NotFoundException("Тип не найден"))))
                .toList();
    }

    public List<StoreDto> searchInStock(String beerName) {
        var beerId = beerRepository.findByName(beerName).getId();
        Specification<StorageEntity> spec = Specification.where(null);
        spec = spec.and(beerSpecification.correctBeer(beerId));
        spec = spec.and(beerSpecification.hasBeer());
        //var spec = beerSpecification.correctBeer(beerId).and(beerSpecification.hasBeer());
        var allStorages = storageRepository.findAll(spec);
        if (allStorages.isEmpty()) {
            return List.of();
        }
        List<StoreEntity> all = allStorages.stream()
                .map(b -> storeRepository.findById(b.getStore())
                        .orElseThrow(()-> new NotFoundException("Магазин не найден")))
                .toList();
        return all.stream()
                .map(storeMapper::toDto)
                .toList();
    }

    public List<StoreDto> searchForStores(List<BeerEntity> beers) {
        Set<StoreDto> stores = new HashSet<>(searchInStock(beers.getFirst().getName()));
        for (BeerEntity beer : beers) {
            var storesList = searchInStock(beer.getName());
            stores.retainAll(storesList);
            if (stores.isEmpty()) {
                return List.of();
            }
        }
        return new ArrayList<>(stores);
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
