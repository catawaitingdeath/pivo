package org.example.pivo.service;

import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.TypeEntity;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.TypeRepository;
import org.example.pivo.utils.FileReaderUtility;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.TypeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.pivo.utils.data.BeerData.beerEntity;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class BeerServiceTests {
    private BeerService beerService;
    private BeerMapper beerMapper = Mappers.getMapper(BeerMapper.class);
    private TypeRepository mockTypeRepository = mock(TypeRepository.class);
    private BeerRepository mockBeerRepository = mock(BeerRepository.class);



    @BeforeEach
    void setup() {
        beerService = new BeerService(mockBeerRepository, mockTypeRepository, beerMapper);
    }

    @Test
    @DisplayName("Create beer with a type from type repository")
    void createBeer_TypePresent() {
        var typeName = "lager";
        var typeEntity = TypeData.typeEntity(1L, typeName);
        var beerEntity = BeerData.beerEntity(1L, typeName);
        var beerEntityNullId = BeerData.beerEntity(null, typeName);
        var createBeerDto = BeerData.createBeerDto(typeName);
        var beerDto = BeerData.beerDto(typeName);

        Mockito.doReturn(Optional.of(typeEntity)).when(mockTypeRepository).findByName(typeName);
        Mockito.doReturn(typeEntity).when(mockTypeRepository).save(typeEntity);
        Mockito.doReturn(beerEntity).when(mockBeerRepository).save(beerEntityNullId);

        var actual = beerService.create(createBeerDto);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(beerDto);
        Mockito.verify(mockTypeRepository, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(mockBeerRepository, Mockito.times(1)).save(beerEntityNullId);
    }

    @Test
    @DisplayName("Create beer with a new type")
    void createBeer_TypeAbsent() {
        var typeName = "ale";
        var createBeerDto = BeerData.createBeerDto(typeName);
        var beerDto = BeerData.beerDto(typeName);
        var typeEntity = TypeData.newTypeEntity(typeName);
        var typeEntityWithId = TypeData.newTypeEntityWithId(typeName);
        var beerEntity = BeerData.beerEntity(2L, typeName);
        var beerEntityNullId = BeerData.beerEntity(typeName);

        Mockito.doReturn(Optional.empty()).when(mockTypeRepository).findByName(typeName);
        Mockito.doReturn(typeEntityWithId).when(mockTypeRepository).save(typeEntity);
        Mockito.doReturn(beerEntity).when(mockBeerRepository).save(beerEntityNullId);

        var actual = beerService.create(createBeerDto);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(beerDto);
        Mockito.verify(mockTypeRepository, Mockito.times(1)).save(typeEntity);
        Mockito.verify(mockBeerRepository, Mockito.times(1)).save(beerEntityNullId);
    }


    @Test
    @DisplayName("Return a list of beers with correct types")
    void getAll_FullRepositories() {
        var beerDtoLager = BeerData.beerDto("lager");
        var beerDtoAle = BeerData.beerDto("ale");
        List<BeerDto> beerDtoList = new ArrayList<>();
        beerDtoList.add(beerDtoLager);
        beerDtoList.add(beerDtoAle);
        var beerEntityLager = BeerData.beerEntity(1L, "lager");
        var beerEntityAle = BeerData.beerEntity(2L, "ale");
        List<BeerEntity> beerEntityList = new ArrayList<>();
        beerEntityList.add(beerEntityAle);
        beerEntityList.add(beerEntityLager);
        var typeEntityLager = TypeData.typeEntity(1L, "lager");
        var typeEntityAle = TypeData.typeEntity(2L, "ale");

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll();
        Mockito.doReturn(Optional.of(typeEntityLager)).when(mockTypeRepository).findById(1L);
        Mockito.doReturn(Optional.of(typeEntityAle)).when(mockTypeRepository).findById(2L);

        var actual = beerService.getAll();
        assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(beerDtoList);
    }

    @Test
    @DisplayName("Return an empty list")
    void getAll_EmptyBeerRepository() {
        List<BeerEntity> beerEntityList = List.of();

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll();

        var actual = beerService.getAll();
        assertThat(actual)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return a beer")
    void getBeer_ReturnBeer() {
        var typeName = "lager";
        var id = 1L;
        var typeEntity = TypeData.typeEntity(id, typeName);
        var beerDto = BeerData.beerDto(typeName);

        Mockito.doReturn(Optional.of(beerEntity(id, typeName))).when(mockBeerRepository).findById(id);
        Mockito.doReturn(Optional.of(typeEntity)).when(mockTypeRepository).findById(id);

        var actual = beerService.get(id);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(Optional.of(beerDto));
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getBeer_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockBeerRepository).findById(0L);

        var exception = assertThrows(RuntimeException.class, () -> beerService.get(0L));
        assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }
}
