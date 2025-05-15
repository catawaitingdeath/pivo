package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.TypeRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BeerRepository beerRepository;
    private final TypeRepository typeRepository;
    private final BeerMapper beerMapper;

    public List<BeerDto> caseInsensitiveSearch(String name) {
        //https://stackoverflow.com/questions/47752663/how-to-search-on-all-fields-with-partial-search-string-in-spring-data-elastic-se
        List<BeerEntity> result = beerRepository.findByNameIgnoreCase(name);
        return result.stream()
                .map(b -> beerMapper.toDto(b, typeRepository.findById(b.getType()).get()))
                .toList();
    }

    public List<BeerDto> searchByCriteria(String producer, BigDecimal minAlcohol, BigDecimal maxAlcohol, BigDecimal minPrice, BigDecimal maxPrice, String type) {
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
