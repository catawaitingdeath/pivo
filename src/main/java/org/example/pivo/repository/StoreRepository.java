package org.example.pivo.repository;

import org.example.pivo.model.entity.StoreEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends CrudRepository<StoreEntity, String> {

}
