package org.example.pivo.repository;

import org.example.pivo.model.entity.BeerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BeerRepository extends JpaRepository<BeerEntity, String>, JpaSpecificationExecutor<BeerEntity> {

    List<BeerEntity> findAll();

    Page<BeerEntity> findAll(Specification<BeerEntity> spec, Pageable pageable);

    List<BeerEntity> findAllByPriceGreaterThanAndAlcoholOrderByPriceDesc(BigDecimal price, BigDecimal alcohol);

    Page<BeerEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<BeerEntity> findAll(Pageable pageable);
}
