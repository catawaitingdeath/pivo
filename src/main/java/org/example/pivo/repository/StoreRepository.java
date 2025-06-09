package org.example.pivo.repository;

import jakarta.persistence.SqlResultSetMapping;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.BeerInStockDto;
import org.example.pivo.model.dto.StoreEmployeeInfoDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends CrudRepository<StoreEntity, String> {

    Page<StoreEntity> findAll(Pageable pageable);

    @Query(value = """
            SELECT * FROM store
            WHERE id IN (SELECT DISTINCT store FROM storage WHERE beer = ?1)
            """,
            nativeQuery = true
    )
    Page<StoreEntity> findStoresByBeerFromStorage(String beerId, Pageable pageable);

    @Query(value = """
    SELECT b.*
    FROM beer b
    JOIN storage s ON b.id = s.beer
    WHERE s.store = :storeId AND s.count > 0
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM beer b
    JOIN storage s ON b.id = s.beer
    WHERE s.store = :storeId AND s.count > 0
    """,
            nativeQuery = true)
    Page<BeerEntity> findBeersByStoreId(
            @Param("storeId")
            String storeId,
            Pageable pageable
    );

    Page<StoreEntity> findStoresByIdIn(List<String> ids, Pageable pageable);
}
