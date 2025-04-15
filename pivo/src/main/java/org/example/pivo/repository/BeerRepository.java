package org.example.pivo.repository;

import org.example.pivo.model.entity.BeerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BeerRepository extends CrudRepository<BeerEntity, Long> {

    List<BeerEntity> findAll();
    List<BeerEntity> findAllByPriceGreaterThanAndAlcoholOrderByPriceDesc(Long price, Long alcohol);
}
