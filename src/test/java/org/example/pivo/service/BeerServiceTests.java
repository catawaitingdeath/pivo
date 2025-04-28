package org.example.pivo.service;

import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.TypeRepository;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.TypeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class BeerServiceTests {
    private BeerService beerService;
    private BeerMapper beerMapper = Mappers.getMapper(BeerMapper.class);
    private TypeRepository mockTypeRepository = mock(TypeRepository.class);
    private BeerRepository mockBeerRepository = mock(BeerRepository.class);
    private String idLager = "W_cPwW5eqk9kxe2OxgivJzVgu";
    private String idAle = "9kxe2OW_cPwW5exgivJ";


    @BeforeEach
    void setup() {
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
        assertThat(actual)
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

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll();
        Mockito.doReturn(Optional.of(typeEntityLager)).when(mockTypeRepository).findById(BigInteger.valueOf(2));
        Mockito.doReturn(Optional.of(typeEntityAle)).when(mockTypeRepository).findById(BigInteger.valueOf(1));

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
        var typeEntity = TypeData.typeEntityLager(BigInteger.valueOf(2));
        var beerDto = BeerData.beerDtoLager(idLager);

        Mockito.doReturn(Optional.of(BeerData.beerEntityLager(idLager))).when(mockBeerRepository).findById(idLager);
        Mockito.doReturn(Optional.of(typeEntity)).when(mockTypeRepository).findById(BigInteger.valueOf(2));

        var actual = beerService.get(idLager);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(beerDto);
    }

    @Test
    @DisplayName("Return error: Предоставлен неверный id")
    void getBeer_ThrowError() {
        Mockito.doReturn(Optional.empty()).when(mockBeerRepository).findById("0");

        var exception = assertThrows(RuntimeException.class, () -> beerService.get("0"));
        assertThat(exception.getMessage())
                .isEqualTo("Предоставлен неверный id");
    }
}
