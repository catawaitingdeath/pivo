package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.TypeRepository;
import org.example.pivo.utils.data.BeerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        searchService = new SearchService(mockBeerRepository, mockTypeRepository, beerMapper);
    }

    @Test
    @DisplayName("Return a full list")
    void searchByNameTest_Success() {
        var beerEntityLager = BeerData.beerEntityLager(idLager);
        var beerEntityAle = BeerData.beerEntityAle(idAle);
        List<BeerEntity> beerEntityList = new ArrayList<>();
        beerEntityList.add(beerEntityAle);
        beerEntityList.add(beerEntityLager);

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll();

        var name = "OlL";
        var actual = searchService.caseInsensitiveSearch(name);
        Assertions.assertThat(actual)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("Return an empty list")
    void searchByNameTest_Fail() {
        var beerEntityLager = BeerData.beerEntityLager(idLager);
        var beerEntityAle = BeerData.beerEntityAle(idAle);
        List<BeerEntity> beerEntityList = new ArrayList<>();
        beerEntityList.add(beerEntityAle);
        beerEntityList.add(beerEntityLager);

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll();

        var name = "Garage";
        var actual = searchService.caseInsensitiveSearch(name);
        Assertions.assertThat(actual)
                .isEmpty();
    }

    @Test
    @DisplayName("Return result")
    void searchByCriteriaTest_Success() {
        var beerDtoLager = BeerData.beerDtoLager(idLager);
        List<BeerDto> beerDtoList = new ArrayList<>();
        beerDtoList.add(beerDtoLager);
        //Specification<BeerEntity> spec = Specification.where(BeerSpecification.alcoholBetween(BigDecimal.valueOf(5), BigDecimal.valueOf(5))
        //        .and(BeerSpecification.priceBetween(BigDecimal.valueOf(10), BigDecimal.valueOf(80))));

        //Mockito.doReturn(beerDtoList).when(mockBeerRepository).findAll(Mockito.any());

        var minPrice = BigDecimal.valueOf(10);
        var maxPrice = BigDecimal.valueOf(80);
        var minAlcohol = BigDecimal.valueOf(5);
        var maxAlcohol = BigDecimal.valueOf(5);
        var actual = searchService.searchByCriteria(null, minAlcohol, maxAlcohol, minPrice, maxPrice, null);
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(beerDtoList);
    }
}
