package org.example.pivo.repository;

import org.example.pivo.model.entity.BeerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BeerRepository extends CrudRepository<BeerEntity, String> {

    List<BeerEntity> findAll();
    List<BeerEntity> findAllByPriceGreaterThanAndAlcoholOrderByPriceDesc(BigDecimal price, BigDecimal alcohol);
}
