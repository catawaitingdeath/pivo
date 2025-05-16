package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.mapper.StorageMapper;
import org.example.pivo.model.dto.CreateStorageDto;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.exceptions.NotFoundException;
import org.example.pivo.repository.StorageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;


    public StorageDto create(CreateStorageDto storage) {
        var storageEntity = storageMapper.toEntity(storage);
        storageEntity = storageRepository.save(storageEntity);
        return storageMapper.toDto(storageEntity);
    }

    public Page<StorageDto> getAll(Integer pageNumber, Integer pageSize) {
        var result = new ArrayList<StorageDto>();
        var storages = storageRepository.findAll(PageRequest.of(pageNumber, pageSize));
        if(storages == null){
            return Page.empty();
        }
        storages.forEach(t -> result.add(storageMapper.toDto(t)));
        return new PageImpl<>(result, storages.getPageable(), storages.getTotalElements());
    }

    public StorageDto get(String id) {
        return storageRepository.findById(id)
                .map(storageMapper::toDto)
                .orElseThrow(()-> new NotFoundException("Предоставлен неверный id"));
    }
}
