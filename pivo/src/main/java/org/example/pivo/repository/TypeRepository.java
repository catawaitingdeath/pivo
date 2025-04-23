package org.example.pivo.repository;

import org.example.pivo.model.entity.TypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends CrudRepository<TypeEntity, BigInteger> {

    Optional<TypeEntity> findByName(String typeName);
    List<TypeEntity> findAll();

}
