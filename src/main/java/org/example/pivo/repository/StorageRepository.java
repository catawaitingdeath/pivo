package org.example.pivo.repository;

import org.example.pivo.model.entity.StorageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface StorageRepository extends CrudRepository<StorageEntity, String> {

    List<StorageEntity> findAll(Specification<StorageEntity> spec);

    Page<StorageEntity> findAll(Pageable pageable);

    List<StorageEntity> findAllByBeerAndCountGreaterThan(String beer, BigInteger count);
}
