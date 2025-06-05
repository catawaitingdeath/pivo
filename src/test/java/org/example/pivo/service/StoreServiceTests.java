package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.client.EmployeeClient;
import org.example.pivo.client.EmployeeDto;
import org.example.pivo.client.EmployeeMapper;
import org.example.pivo.client.StoreEmployeeDto;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.dto.StoreEmployeeInfoDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StoreEntity;
import org.example.pivo.model.exceptions.InternalErrorPivoException;
import org.example.pivo.model.exceptions.NotFoundPivoException;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.utils.data.StoreData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class StoreServiceTests {
    private StoreService storeService;
    private StoreMapper storeMapper = Mappers.getMapper(StoreMapper.class);
    private EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);
    private StoreRepository mockStoreRepository;
    private EmployeeClient mockEmployeeClient;
    private String id1 = "W_cPwW5eqk9kxe2OxgivJzVgu";
    private String id2 = "9kxe2OW_cPwW5exgivJ";


    @BeforeEach
    void setup() {
        mockStoreRepository = mock(StoreRepository.class);
        mockEmployeeClient = mock(EmployeeClient.class);
        storeService = new StoreService(mockEmployeeClient, mockStoreRepository, storeMapper, employeeMapper);
    }

    @Test
    @DisplayName("Create a store")
    void createStore() {
        var storeEntity = StoreData.storeEntity1(id1);
        var storeEntityNullId = StoreData.storeEntity1();
        var createStoreDto = StoreData.createStoreDto1();
        var storeDto = StoreData.storeDto1(id1);

        Mockito.doReturn(storeEntity).when(mockStoreRepository).save(storeEntityNullId);

        var actual = storeService.create(createStoreDto);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(storeDto);
        Mockito.verify(mockStoreRepository, Mockito.times(1)).save(storeEntityNullId);
    }

    @Test
    @DisplayName("Return a list of stores")
    void getAll_FullRepository() {
        var storeDto1 = StoreData.storeDto1(id1);
        var storeDto2 = StoreData.storeDto2(id2);
        List<StoreDto> storeDtoList = new ArrayList<>();
        storeDtoList.add(storeDto1);
        storeDtoList.add(storeDto2);
        var storeEntity1 = StoreData.storeEntity1(id1);
        var storeEntity2 = StoreData.storeEntity2(id2);
        List<StoreEntity> storeEntityList = new ArrayList<>();
        storeEntityList.add(storeEntity2);
        storeEntityList.add(storeEntity1);
        var pageNumber = 0;
        var pageSize = 10;

        Mockito.doReturn(new PageImpl<>(storeEntityList))
                .when(mockStoreRepository)
                .findAll(PageRequest.of(pageNumber, pageSize));
        var actual = storeService.getAll(pageNumber, pageSize);
        Assertions.assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(storeDtoList);
    }

    @Test
    @DisplayName("Return an empty list")
    void getAll_EmptyStoreRepository() {
        List<BeerEntity> storeEntityList = List.of();

        Mockito.doReturn(storeEntityList).when(mockStoreRepository).findAll();

        var actual = storeService.getAll(0, 10);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return a store")
    void getStore_ReturnStore() {
        var storeDto = StoreData.storeDto1(id1);

        Mockito.doReturn(Optional.of(StoreData.storeEntity1(id1))).when(mockStoreRepository).findById(id1);

        var actual = storeService.get(id1);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(storeDto);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getStore_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockStoreRepository).findById("0");

        var exception = assertThrows(NotFoundPivoException.class, () -> storeService.get("0"));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }

    @Test
    @DisplayName("Delete a store and its employees")
    void deleteStore_WithEmployees() {
        var storeEntity = StoreData.storeEntity1(id1);

        var employee1 = EmployeeDto.builder()
                .name("Alice")
                .surname("Johnson")
                .phone("79684551668")
                .email("alice.johnson@example.com")
                .position("Cashier")
                .salary(BigInteger.valueOf(30000))
                .store(id1)
                .build();
        var employee2 = EmployeeDto.builder()
                .name("Bob")
                .surname("Smith")
                .phone("79684551888")
                .email("bob.smith@example.com")
                .position("Manager")
                .salary(BigInteger.valueOf(50000))
                .store(id1)
                .build();

        var storeEmployees = StoreEmployeeDto.builder()
                .id(id1)
                .employees(List.of(employee1, employee2))
                .build();

        Mockito.doReturn(Optional.of(storeEntity)).when(mockStoreRepository).findById(id1);
        Mockito.doReturn(storeEmployees).when(mockEmployeeClient).getEmployees(id1);

        storeService.delete(id1);
        Mockito.verify(mockEmployeeClient, Mockito.times(1)).deleteEmployees(id1);
        Mockito.verify(mockStoreRepository, Mockito.times(1)).deleteById(id1);
    }

    @Test
    @DisplayName("Delete a store without employees")
    void deleteStore_WithoutEmployees() {
        var storeEntity = StoreData.storeEntity1(id1);

        var storeEmployees = StoreEmployeeDto.builder()
                .id(id1)
                .employees(List.of())
                .build();

        Mockito.doReturn(Optional.of(storeEntity)).when(mockStoreRepository).findById(id1);
        Mockito.doReturn(storeEmployees).when(mockEmployeeClient).getEmployees(id1);

        storeService.delete(id1);
        Mockito.verify(mockEmployeeClient, Mockito.times(0)).deleteEmployees(id1);
        Mockito.verify(mockStoreRepository, Mockito.times(1)).deleteById(id1);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void deleteStore_WrongId() {
        Mockito.doReturn(Optional.empty()).when(mockStoreRepository).findById(id1);

        var exception = assertThrows(NotFoundPivoException.class, () -> storeService.get(id1));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }

    @Test
    @DisplayName("Return error: Список сотрудников не может быть null")
    void deleteStore_NullEmployees() {
        var storeEntity = StoreData.storeEntity1(id1);

        var storeEmployees = StoreEmployeeDto.builder()
                .id(id1)
                .build();

        Mockito.doReturn(Optional.of(storeEntity)).when(mockStoreRepository).findById(id1);
        Mockito.doReturn(storeEmployees).when(mockEmployeeClient).getEmployees(id1);

        var exception = assertThrows(InternalErrorPivoException.class, () -> storeService.delete(id1));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Список сотрудников не может быть null");
    }

    @Test
    @DisplayName("Return a store and its employees info")
    void getStoreEmployeeInfo_ReturnStoreEmployeeInfo() {
        var store = StoreData.storeDto1(id1);

        var employee1 = EmployeeDto.builder()
                .name("Alice")
                .surname("Johnson")
                .phone("79684551668")
                .email("alice.johnson@example.com")
                .position("Cashier")
                .salary(BigInteger.valueOf(30000))
                .store(id1)
                .build();
        var employee2 = EmployeeDto.builder()
                .name("Bob")
                .surname("Smith")
                .phone("79684551888")
                .email("bob.smith@example.com")
                .position("Manager")
                .salary(BigInteger.valueOf(50000))
                .store(id1)
                .build();

        var storeEmployees = StoreEmployeeDto.builder()
                .id(id1)
                .employees(List.of(employee1, employee2))
                .build();

        var expected = StoreEmployeeInfoDto.builder()
                .id(store.getId())
                .employees(List.of(employee1, employee2))
                .address(store.getAddress())
                .phone(store.getPhone())
                .build();

        Mockito.doReturn(Optional.of(StoreData.storeEntity1(id1))).when(mockStoreRepository).findById(id1);
        Mockito.doReturn(storeEmployees).when(mockEmployeeClient).getEmployees(id1);

        var actual = storeService.getStoreEmployeeInfo(id1);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getStoreEmployeeInfo_WrongId() {
        Mockito.doReturn(Optional.empty()).when(mockStoreRepository).findById(id1);

        var exception = assertThrows(NotFoundPivoException.class, () -> storeService.get(id1));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }

    @Test
    @DisplayName("Return error: Список сотрудников не может быть null")
    void getStoreEmployeeInfo_NullEmployees() {
        var storeEntity = StoreData.storeEntity1(id1);

        var storeEmployees = StoreEmployeeDto.builder()
                .id(id1)
                .build();

        Mockito.doReturn(Optional.of(storeEntity)).when(mockStoreRepository).findById(id1);
        Mockito.doReturn(storeEmployees).when(mockEmployeeClient).getEmployees(id1);

        var exception = assertThrows(InternalErrorPivoException.class, () -> storeService.getStoreEmployeeInfo(id1));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Список сотрудников не может быть null");
    }

    @Test
    @DisplayName("Return StoreEmployeeDto with new employees")
    void registerEmployees_ReturnStoreEmployeeDto() {
        var employee1 = CreateEmployeeDto.builder()
                .name("Alice")
                .surname("Johnson")
                .phone("79684551668")
                .email("alice.johnson@example.com")
                .position("Cashier")
                .salary(BigInteger.valueOf(30000))
                .store(id1)
                .build();
        var employee2 = CreateEmployeeDto.builder()
                .name("Bob")
                .surname("Smith")
                .phone("79684551888")
                .email("bob.smith@example.com")
                .position("Manager")
                .salary(BigInteger.valueOf(50000))
                .store(id1)
                .build();

        var storeEmployees = StoreEmployeeDto.builder()
                .id(id1)
                .employees(List.of(employeeMapper.toDto(employee1), employeeMapper.toDto(employee2)))
                .build();

        Mockito.doReturn(Optional.of(StoreData.storeEntity1(id1))).when(mockStoreRepository).findById(id1);
        Mockito.doReturn(storeEmployees).when(mockEmployeeClient).getEmployees(id1);

        var actual = storeService.registerEmployees(id1, List.of(employee1, employee2));
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(storeEmployees);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void registerEmployees_WrongId() {
        Mockito.doReturn(Optional.empty()).when(mockStoreRepository).findById(id1);

        var exception = assertThrows(NotFoundPivoException.class, () -> storeService.get(id1));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }

    @Test
    @DisplayName("Return error: Список сотрудников не может быть пустым")
    void registerEmployees_EmptyEmployees() {
        var storeEntity = StoreData.storeEntity1(id1);

        Mockito.doReturn(Optional.of(storeEntity)).when(mockStoreRepository).findById(id1);

        var exception = assertThrows(NotFoundPivoException.class, () -> storeService.registerEmployees(id1, List.of()));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Список сотрудников не может быть пустым");
    }
}
