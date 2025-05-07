package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.mapper.StorageMapper;
import org.example.pivo.model.dto.CreateStorageDto;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.exceptions.NotFoundStorageException;
import org.example.pivo.repository.StorageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;


    public StorageDto create(CreateStorageDto store) {
        var storageEntity = storageMapper.toEntity(store);
        storageEntity = storageRepository.save(storageEntity);
        return storageMapper.toDto(storageEntity);
    }

    public List<StorageDto> getAll() {
        var result = new ArrayList<StorageDto>();
        var storages = storageRepository.findAll();
        storages.forEach(t -> result.add(storageMapper.toDto(t)));
        return result;
    }

    public StorageDto get(String id) {
        return storageRepository.findById(id)
                .map(storageMapper::toDto)
                .orElseThrow(()-> new NotFoundStorageException("Предоставлен неверный id"));
    }
}
