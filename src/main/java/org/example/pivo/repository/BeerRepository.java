package org.example.pivo.repository;

import org.example.pivo.model.entity.BeerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BeerRepository extends CrudRepository<BeerEntity, String>, JpaSpecificationExecutor<BeerEntity> {

    List<BeerEntity> findAll();

    List<BeerEntity> findAllByPriceGreaterThanAndAlcoholOrderByPriceDesc(BigDecimal price, BigDecimal alcohol);

    List<BeerEntity> findAllByProducer(String producer);

    List<BeerEntity> findAll(Specification<BeerEntity> spec);

    Page<BeerEntity> findAll(Pageable pageable);
}
