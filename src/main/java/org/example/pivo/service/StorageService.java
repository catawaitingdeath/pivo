package org.example.pivo.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pivo.mapper.StorageMapper;
import org.example.pivo.model.dto.BeerShipmentDto;
import org.example.pivo.model.dto.CreateStorageDto;
import org.example.pivo.model.dto.StorageDto;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.model.exceptions.NotFoundPivoException;
import org.example.pivo.model.exceptions.BadRequestPivoException;
import org.example.pivo.repository.StorageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

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
                .orElseThrow(()-> new NotFoundPivoException("Предоставлен неверный id"));
    }

    @Transactional
    public void ship(@Valid BeerShipmentDto beerShipmentDto) {
        for(var storeShipment : beerShipmentDto.getStoreShipments()) {
            var uniqueBeerIdCount = storeShipment.getPosition().stream()
                    .map(BeerShipmentDto.Position::getBeerId)
                    .distinct()
                    .count();
            if(storeShipment.getPosition().size() != uniqueBeerIdCount) {
                throw new BadRequestPivoException("Позиции пива не уникальны");
            }
            for(var position : storeShipment.getPosition()){
                var beerId = position.getBeerId();
                var count = position.getCount();
                var storageEntity = StorageEntity.builder()
                        .store(storeShipment.getStoreId())
                        .beer(beerId)
                        .count(count)
                        .build();
                storageRepository.save(storageEntity);
            }
        }

    }
}
