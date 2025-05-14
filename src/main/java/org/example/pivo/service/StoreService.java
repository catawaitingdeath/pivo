package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.exceptions.NotFoundException;
import org.example.pivo.repository.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;


    public StoreDto create(CreateStoreDto store) {
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

    public StoreDto get(String id) {
        return storeRepository.findById(id)
                .map(storeMapper::toDto)
                .orElseThrow(()-> new NotFoundException("Предоставлен неверный id"));
    }
}
