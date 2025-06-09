package org.example.pivo.repository;

import org.example.pivo.model.entity.StorageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface StorageRepository extends CrudRepository<StorageEntity, String> {

    List<StorageEntity> findAll(Specification<StorageEntity> spec);

    Page<StorageEntity> findAll(Pageable pageable);

    List<StorageEntity> findAllByBeerAndCountGreaterThan(String beer, BigInteger count);

    List<StorageEntity> findAllByStore (String storeId);

    StorageEntity findByBeerAndStore(String beerId, String storeId);

    @Query("""
    SELECT s.store
    FROM StorageEntity s
    WHERE s.beer IN :beerIds
    GROUP BY s.store
""")
    List<String> findStoreIdsWithAllBeers(
            @Param("beerIds")
            List<String> beerIds);

    String Beer(String beer);
}
