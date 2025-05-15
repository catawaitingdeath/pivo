package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.exceptions.NotFoundException;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.TypeRepository;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.TypeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class BeerServiceTests {
    private BeerService beerService;
    private BeerMapper beerMapper = Mappers.getMapper(BeerMapper.class);
    private TypeRepository mockTypeRepository;
    private BeerRepository mockBeerRepository;
    private BeerSpecification mockBeerSpecification;
    private String idLager = "W_cPwW5eqk9kxe2OxgivJzVgu";
    private String idAle = "9kxe2OW_cPwW5exgivJ";


    @BeforeEach
    void setup() {
        mockTypeRepository = mock(TypeRepository.class);
        mockBeerRepository = mock(BeerRepository.class);
        mockBeerSpecification = mock(BeerSpecification.class);
        beerService = new BeerService(mockBeerRepository, mockTypeRepository, beerMapper);
    }

    @Test
    @DisplayName("Create beer with a type from type repository")
    void createBeer_TypePresent() {
        var typeEntity = TypeData.typeEntityLager(BigInteger.valueOf(2));
        var beerEntity = BeerData.beerEntityLager(idLager);
        var beerEntityNullId = BeerData.beerEntityLager(null);
        var createBeerDto = BeerData.createBeerDtoLager();
        var beerDto = BeerData.beerDtoLager(idLager);

        Mockito.doReturn(Optional.of(typeEntity)).when(mockTypeRepository).findByName("лагер");
        Mockito.doReturn(typeEntity).when(mockTypeRepository).save(typeEntity);
        Mockito.doReturn(beerEntity).when(mockBeerRepository).save(beerEntityNullId);

        var actual = beerService.create(createBeerDto);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(beerDto);
        Mockito.verify(mockTypeRepository, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(mockBeerRepository, Mockito.times(1)).save(beerEntityNullId);
    }

    @Test
    @DisplayName("Throw exception that no type was found")
    void createBeer_TypeAbsent() {
        var typeName = "эль";
        var createBeerDto = BeerData.createBeerDtoAle();

        Mockito.doReturn(Optional.empty()).when(mockTypeRepository).findByName(typeName);

        assertThatThrownBy(() -> beerService.create(createBeerDto))
                .isInstanceOf(RuntimeException.class);
    }


    @Test
    @DisplayName("Return a list of beers with correct types")
    void getAll_FullRepositories() {
        var beerDtoLager = BeerData.beerDtoLager(idLager);
        var beerDtoAle = BeerData.beerDtoAle(idAle);
        List<BeerDto> beerDtoList = new ArrayList<>();
        beerDtoList.add(beerDtoLager);
        beerDtoList.add(beerDtoAle);
        var beerEntityLager = BeerData.beerEntityLager(idLager);
        var beerEntityAle = BeerData.beerEntityAle(idAle);
        List<BeerEntity> beerEntityList = new ArrayList<>();
        beerEntityList.add(beerEntityAle);
        beerEntityList.add(beerEntityLager);
        var typeEntityLager = TypeData.typeEntityLager(BigInteger.valueOf(2));
        var typeEntityAle = TypeData.typeEntityAle(BigInteger.valueOf(1));
        var pageNumber = 1;
        var pageSize = 10;

        Mockito.doReturn(new PageImpl<>(beerEntityList)).when(mockBeerRepository).findAll(PageRequest.of(pageNumber, pageSize));
        Mockito.doReturn(Optional.of(typeEntityLager)).when(mockTypeRepository).findById(BigInteger.valueOf(2));
        Mockito.doReturn(Optional.of(typeEntityAle)).when(mockTypeRepository).findById(BigInteger.valueOf(1));

        var actual = beerService.getAll(pageNumber, pageSize);
        Assertions.assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(beerDtoList);
    }

    @Test
    @DisplayName("Return an empty list")
    void getAll_EmptyBeerRepository() {
        List<BeerEntity> beerEntityList = List.of();

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll();

        var actual = beerService.getAll(5, 10);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return a beer")
    void getBeer_ReturnBeer() {
        var typeEntity = TypeData.typeEntityLager(BigInteger.valueOf(2));
        var beerDto = BeerData.beerDtoLager(idLager);

        Mockito.doReturn(Optional.of(BeerData.beerEntityLager(idLager))).when(mockBeerRepository).findById(idLager);
        Mockito.doReturn(Optional.of(typeEntity)).when(mockTypeRepository).findById(BigInteger.valueOf(2));

        var actual = beerService.get(idLager);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(beerDto);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getBeer_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockBeerRepository).findById("0");

        var exception = assertThrows(NotFoundException.class, () -> beerService.get("0"));
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }

}
