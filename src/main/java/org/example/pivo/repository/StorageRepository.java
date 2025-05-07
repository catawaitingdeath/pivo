package org.example.pivo.repository;

import org.example.pivo.model.entity.StorageEntity;
import org.springframework.data.repository.CrudRepository;

public interface StorageRepository extends CrudRepository<StorageEntity, String> {
}
