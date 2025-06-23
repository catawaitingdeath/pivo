package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.BeerInStockDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.TypeEntity;
import org.example.pivo.model.exceptions.BadRequestPivoException;
import org.example.pivo.model.exceptions.NotFoundPivoException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        var beers = beerRepository.findByNameContainingIgnoreCase(name, PageRequest.of(pageNumber, pageSize));
        var typeIds = beers.stream()
                .map(BeerEntity::getType)
                .collect(Collectors.toSet());
        var beerTypes = typeRepository.findByIdIn(typeIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(TypeEntity::getId, TypeEntity::getName));
        return beers.map(b -> beerMapper.toDto(b, beerTypes.get(b.getType())));
    }

    public Page<BeerDto> searchByCriteria(
            String producer,
            BigDecimal minAlcohol,
            BigDecimal maxAlcohol,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String type,
            Integer pageNumber,
            Integer pageSize
    ) {
        if (producer == null && minAlcohol == null && maxAlcohol == null && minPrice == null && maxPrice == null
                && type == null) {
            throw new BadRequestPivoException("Критерии не были заданы");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
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
        Page<BeerEntity> all = beerRepository.findAll(spec, pageable);
        var typeIds = all.stream()
                .map(BeerEntity::getType)
                .collect(Collectors.toSet());
        var beerTypes = typeRepository.findByIdIn(typeIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(TypeEntity::getId, TypeEntity::getName));
        var result = all.stream()
                .map(b -> beerMapper.toDto(b, beerTypes.get(b.getType())))
                .toList();
        return new PageImpl<>(result, pageable, all.getTotalElements());
    }

    public Page<StoreDto> searchInStock(String beerId, Integer pageNumber, Integer pageSize) {
        if (!beerRepository.existsById(beerId)) {
            throw new NotFoundPivoException("Не найдено пива с таким id");
        }
        var stores = storeRepository.findStoresByBeerFromStorage(beerId, PageRequest.of(pageNumber, pageSize));
        if (stores.isEmpty()) {
            return Page.empty();
        }
        return stores.map(storeMapper::toDto);
    }

    public Page<StoreDto> searchForStores(List<String> beers, Integer pageNumber, Integer pageSize) {
        var storeIds = storageRepository.findStoreIdsWithAllBeers(beers);
        if(storeIds.isEmpty()) {
            return Page.empty();
        }
        var storeEntities = storeRepository.findStoresByIdIn(storeIds, PageRequest.of(pageNumber, pageSize));
        var stores = storeEntities.getContent().stream()
                .map(storeMapper::toDto)
                .toList();
        return new PageImpl<>(stores, PageRequest.of(pageNumber, pageSize), storeEntities.getTotalElements());
    }

    public Page<BeerInStockDto> searchForBeer(String storeId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        var beers = storeRepository.findBeersByStoreId(storeId, pageable);
        if (beers == null || beers.isEmpty()) {
            throw new NotFoundPivoException("Пиво в указанном магазине не было найдено");
        }
        return beers.map(beerInStock -> {
            var typeId = beerInStock.getType();
            var typeEntity = typeRepository.findById(typeId);
            var beerDto = beerMapper.toDto(beerInStock, typeEntity.get());
            var count = storageRepository.findByBeerAndStore(beerDto.getId(), storeId).getCount();

            return new BeerInStockDto(beerDto, count);
        });
    }
}
