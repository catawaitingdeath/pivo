package org.example.pivo.repository;

import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StorageEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StorageRepository extends CrudRepository<StorageEntity, String> {

    List<StorageEntity> findAll(Specification<StorageEntity> spec);
}
