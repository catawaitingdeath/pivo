package org.example.pivo.components;


import org.example.pivo.model.entity.BeerEntity;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BeerSpecification {

    public static Specification<BeerEntity> hasProducer(String producer) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("producer"), producer);
    }

    public static Specification<BeerEntity> alcoholBetween(BigDecimal minAlcohol, BigDecimal maxAlcohol) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("alcohol"), minAlcohol, maxAlcohol);
    }

    public static Specification<BeerEntity> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<BeerEntity> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
    }
}