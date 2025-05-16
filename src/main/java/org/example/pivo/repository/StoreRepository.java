package org.example.pivo.repository;

import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends CrudRepository<StoreEntity, String> {

    Page<StoreEntity> findAll(Pageable pageable);
}
