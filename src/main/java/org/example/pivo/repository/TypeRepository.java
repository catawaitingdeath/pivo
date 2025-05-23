package org.example.pivo.repository;

import org.example.pivo.model.entity.TypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TypeRepository extends CrudRepository<TypeEntity, BigInteger> {

    Optional<TypeEntity> findByName(String typeName);

    List<TypeEntity> findAll();

    List<TypeEntity> findByIdIn(Set<BigInteger> ids);

}
