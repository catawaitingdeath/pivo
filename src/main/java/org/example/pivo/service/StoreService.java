package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.exceptions.NotFoundPivoException;
import org.example.pivo.repository.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    public Page<StoreDto> getAll(Integer pageNumber, Integer pageSize) {
        var result = new ArrayList<StoreDto>();
        var stores = storeRepository.findAll(PageRequest.of(pageNumber, pageSize));
        if(stores == null){
            return Page.empty();
        }
        stores.forEach(t -> result.add(storeMapper.toDto(t)));
        return new PageImpl<>(result, stores.getPageable(), stores.getTotalElements());
    }

    public StoreDto get(String id) {
        return storeRepository.findById(id)
                .map(storeMapper::toDto)
                .orElseThrow(()-> new NotFoundPivoException("Предоставлен неверный id"));
    }
}
