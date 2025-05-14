package org.example.pivo.components;


import org.example.pivo.model.entity.BeerEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

public class BeerSpecification {

    public static Specification<BeerEntity> hasProducer(String producer) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("producer"), producer);
    }

    public static Specification<BeerEntity> alcoholLessThan(BigDecimal maxAlcohol) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("alcohol"), maxAlcohol);
    }

    public static Specification<BeerEntity> alcoholGreaterThan(BigDecimal minAlcohol) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("alcohol"), minAlcohol);
    }

    public static Specification<BeerEntity> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<BeerEntity> priceLessThan(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<BeerEntity> priceGreaterThan(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }
}