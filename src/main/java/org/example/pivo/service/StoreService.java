package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.AnotherStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.repository.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;


    public StoreDto create(AnotherStoreDto store) {
        var storeEntity = storeMapper.toEntity(store);
        storeEntity = storeRepository.save(storeEntity);
        return storeMapper.toDto(storeEntity);
    }

    public List<StoreDto> getAll() {
        var result = new ArrayList<StoreDto>();
        var stores = storeRepository.findAll();
        stores.forEach(t -> result.add(storeMapper.toDto(t)));
        return result;
    }

    public Optional<StoreDto> get(String id) {
        var storeEntity = storeRepository.findById(id).stream()
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Предоставлен неверный id"));
        var storeDto = storeMapper.toDto(storeEntity);
        return Optional.of(storeDto);
    }
}
