package org.example.pivo.service;

import org.example.pivo.components.NanoIdGenerator;
import org.example.pivo.config.property.NanoIdProperty;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.pivo.utils.data.BeerData.beerEntity;
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
    void setup() {beerService = new BeerService(mockBeerRepository, mockTypeRepository, beerMapper);}

    @Test
    @DisplayName("Create beer with a type from type repository")
    void createBeer_TypePresent() {
        var typeName = "лагер";
        var typeEntity = TypeData.typeEntity(BigInteger.valueOf(2), typeName);
        var beerEntity = BeerData.beerEntity(idLager, typeName);
        var beerEntityNullId = BeerData.beerEntity(null, typeName);
        var createBeerDto = BeerData.createBeerDto(typeName);
        var beerDto = BeerData.beerDto(idLager, typeName);

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
    @DisplayName("Throw exception that no type was found")
    void createBeer_TypeAbsent() {
        var typeName = "эль";
        var createBeerDto = BeerData.createBeerDto(typeName);

        Mockito.doReturn(Optional.empty()).when(mockTypeRepository).findByName(typeName);

        assertThatThrownBy(() -> beerService.create(createBeerDto))
                .isInstanceOf(RuntimeException.class);
    }


    @Test
    @DisplayName("Return a list of beers with correct types")
    void getAll_FullRepositories() {
        var beerDtoLager = BeerData.beerDto(idLager, "лагер");
        var beerDtoAle = BeerData.beerDto(idAle, "эль");
        List<BeerDto> beerDtoList = new ArrayList<>();
        beerDtoList.add(beerDtoLager);
        beerDtoList.add(beerDtoAle);
        var beerEntityLager = BeerData.beerEntity(idLager, "лагер");
        var beerEntityAle = BeerData.beerEntity(idAle, "эль");
        List<BeerEntity> beerEntityList = new ArrayList<>();
        beerEntityList.add(beerEntityAle);
        beerEntityList.add(beerEntityLager);
        var typeEntityLager = TypeData.typeEntity(BigInteger.valueOf(2), "лагер");
        var typeEntityAle = TypeData.typeEntity(BigInteger.valueOf(1), "эль");

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
        var typeName = "лагер";
        var typeEntity = TypeData.typeEntity(BigInteger.valueOf(2), typeName);
        var beerDto = BeerData.beerDto(idLager, typeName);

        Mockito.doReturn(Optional.of(beerEntity(idLager, typeName))).when(mockBeerRepository).findById(idLager);
        Mockito.doReturn(Optional.of(typeEntity)).when(mockTypeRepository).findById(BigInteger.valueOf(2));

        var actual = beerService.get(idLager);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(Optional.of(beerDto));
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
