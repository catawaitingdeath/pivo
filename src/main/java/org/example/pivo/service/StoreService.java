package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.client.EmployeeClient;
import org.example.pivo.client.EmployeeDto;
import org.example.pivo.client.EmployeeMapper;
import org.example.pivo.client.StoreEmployeeDto;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.CreateStoreDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.dto.StoreEmployeeInfoDto;
import org.example.pivo.model.exceptions.InternalErrorPivoException;
import org.example.pivo.model.exceptions.NotFoundPivoException;
import org.example.pivo.repository.StoreRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final EmployeeClient employeeClient;
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final EmployeeMapper employeeMapper;


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

    public void delete(String id) {
        storeRepository.findById(id)
                .orElseThrow(()-> new NotFoundPivoException("Предоставлен неверный id"));
        var employees = employeeClient.getEmployees(id);
        if (employees != null && !employees.getEmployees().isEmpty()) {
            employeeClient.deleteEmployees(id);
        }
        try {
            storeRepository.deleteById(id);
        } catch (Exception e) {
            for(EmployeeDto employee : employees.getEmployees()) {
                var createEmployeeDto = employeeMapper.toCreateDto(employee);
                employeeClient.createEmployee(createEmployeeDto);
            }
            throw new InternalErrorPivoException("Не удалось удалить магазин");
        }

    }

    public StoreEmployeeInfoDto getStoreEmployeeInfo(String id) {
        var store = storeRepository.findById(id)
                .orElseThrow(()-> new NotFoundPivoException("Предоставлен неверный id"));
        var employees = employeeClient.getEmployees(id).getEmployees();
        if(employees == null){
            employees = Collections.emptyList();
        }
        return StoreEmployeeInfoDto.builder()
                .id(store.getId())
                .employees(employees)
                .address(store.getAddress())
                .phone(store.getPhone())
                .build();
    }

    public StoreEmployeeDto registerEmployees(String id, List<CreateEmployeeDto> employees) {
        storeRepository.findById(id)
                .orElseThrow(()-> new NotFoundPivoException("Предоставлен неверный id"));
        if(employees == null || employees.isEmpty()){
            throw new NotFoundPivoException("Список сотрудников не может быть пустым");
        }
        for(CreateEmployeeDto employee : employees) {
            employee.setStore(id);
            employeeClient.createEmployee(employee);
        }
        return employeeMapper.toStoreEmployeeDto(id, employeeClient.getEmployees(id).getEmployees());
    }
}
