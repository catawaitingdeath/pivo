package org.example.pivo.components;


import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StorageEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BeerSpecification {

    public Specification<BeerEntity> hasProducer(String producer) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("producer"), producer);
    }

    public Specification<BeerEntity> alcoholLessThan(BigDecimal maxAlcohol) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("alcohol"), maxAlcohol);
    }

    public Specification<BeerEntity> alcoholGreaterThan(BigDecimal minAlcohol) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("alcohol"), minAlcohol);
    }

    public Specification<BeerEntity> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public Specification<BeerEntity> priceLessThan(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public Specification<BeerEntity> priceGreaterThan(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }
}