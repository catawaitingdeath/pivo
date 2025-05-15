package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
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
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

public class SearchServiceTests {
    private SearchService searchService;
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
        mockBeerSpecification = mock(BeerSpecification.class);
        searchService = new SearchService(mockBeerRepository, mockTypeRepository, beerMapper, mockBeerSpecification);
    }

    @Test
    @DisplayName("Return a full list")
    void searchByNameTest_Success() {
        List<BeerEntity> beerEntityList = List.of(BeerData.beerEntityAle(idAle));
        var name = "OlL";
        var type = TypeData.typeEntityAle(BigInteger.ONE);

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findByNameContainingIgnoreCase(name);
        Mockito.doReturn(Optional.of(type)).when(mockTypeRepository).findById(BigInteger.ONE);

        var actual = searchService.searchByName(name);
        Assertions.assertThat(actual).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Return an empty list")
    void searchByNameTest_Fail() {
        List<BeerEntity> beerEntityList = List.of();
        var name = "Garage";

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findByNameContainingIgnoreCase(name);

        var actual = searchService.searchByName(name);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("Return result")
    void searchByCriteriaTest_Success() {
        var beerEntityList = List.of(BeerData.beerEntityLager(idLager));
        var type = TypeData.typeEntityLager(BigInteger.TWO);
        var beerDtoList = List.of(BeerData.beerDtoLager(idLager));

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll(Mockito.<Specification<BeerEntity>>any());
        Mockito.doReturn(Optional.of(type)).when(mockTypeRepository).findById(BigInteger.TWO);

        var minPrice = BigDecimal.valueOf(10);
        var maxPrice = BigDecimal.valueOf(80);
        var maxAlcohol = BigDecimal.valueOf(5);
        var actual = searchService.searchByCriteria(null, null, maxAlcohol, minPrice, maxPrice, null);
        Assertions.assertThat(actual).isNotNull().isEqualTo(beerDtoList);
        Mockito.verify(mockBeerSpecification, Mockito.times(1)).alcoholLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(1)).priceGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(1)).priceLessThan(Mockito.any());
    }


    @Test
    @DisplayName("Return empty list")
    void searchByCriteriaTest_NoCriteria() {
        var emptyList = List.of();

        Mockito.doReturn(emptyList).when(mockBeerRepository).findAll(Mockito.<Specification<BeerEntity>>any());

        var actual = searchService.searchByCriteria(null, null, null, null, null, null);
        Assertions.assertThat(actual).isNotNull().isEqualTo(emptyList);
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).alcoholLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).priceGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).priceLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).hasProducer(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).alcoholGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).hasType(Mockito.any());
    }
}
