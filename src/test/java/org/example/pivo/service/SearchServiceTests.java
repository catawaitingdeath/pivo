package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.constants.BeerIds;
import org.example.pivo.constants.StoreIds;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.StoreDto;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.model.entity.StoreEntity;
import org.example.pivo.model.entity.TypeEntity;
import org.example.pivo.model.exceptions.BadRequestPivoException;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.repository.TypeRepository;
import org.example.pivo.utils.data.BeerData;
import org.example.pivo.utils.data.StorageData;
import org.example.pivo.utils.data.StoreData;
import org.example.pivo.utils.data.TypeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class SearchServiceTests {
    private SearchService searchService;
    private BeerMapper beerMapper = Mappers.getMapper(BeerMapper.class);
    private StoreMapper storeMapper = Mappers.getMapper(StoreMapper.class);
    private TypeRepository mockTypeRepository;
    private BeerRepository mockBeerRepository;
    private StorageRepository mockStorageRepository;
    private StoreRepository mockStoreRepository;
    private BeerSpecification mockBeerSpecification;
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private StorageEntity storageEntity = StorageData.storageEntity100();
    private BeerEntity beerEntityLager = BeerData.beerEntityLager(BeerIds.beerId1);
    private BeerEntity beerEntityAle = BeerData.beerEntityAle(BeerIds.beerId2);
    private BeerDto beerDtoLager = BeerData.beerDtoLager(BeerIds.beerId1);
    private BeerDto beerDtoAle = BeerData.beerDtoAle(BeerIds.beerId2);
    private TypeEntity typeEntityLager = TypeData.typeEntityLager(BigInteger.valueOf(2));
    private TypeEntity typeEntityAle = TypeData.typeEntityAle(BigInteger.valueOf(1));
    private StoreDto storeDto1 = StoreData.storeDtoLeningradskoe(StoreIds.storeId1);
    private StoreDto storeDto2 = StoreData.storeDtoProstornaya(StoreIds.storeId2);
    private StoreEntity storeEntity1 = StoreData.storeEntityLenigradskoe(StoreIds.storeId1);
    private StoreEntity storeEntity2 = StoreData.storeEntityProstornaya(StoreIds.storeId2);
    private List<String> beerIds = List.of(BeerIds.beerId1, BeerIds.beerId2, BeerIds.beerId3);
    private List<String> storeIds = List.of(StoreIds.storeId1, StoreIds.storeId2);


    @BeforeEach
    void setup() {
        mockTypeRepository = mock(TypeRepository.class);
        mockBeerRepository = mock(BeerRepository.class);
        mockStorageRepository = mock(StorageRepository.class);
        mockStoreRepository = mock(StoreRepository.class);
        mockBeerSpecification = mock(BeerSpecification.class);
        searchService = new SearchService(mockBeerRepository,
                mockTypeRepository,
                mockStorageRepository,
                mockStoreRepository,
                beerMapper,
                storeMapper,
                mockBeerSpecification);
    }

    @Test
    @DisplayName("Return a beer, containing 'OlL' in name")
    void searchByNameTest_Success() {
        var pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<BeerEntity> beerEntityPage = new PageImpl<>(List.of(BeerData.beerEntityAle()), pageRequest, 1);
        var name = "OlL";
        var type = TypeData.typeEntityAle(BigInteger.ONE);

        Mockito.doReturn(beerEntityPage).when(mockBeerRepository).findByNameContainingIgnoreCase(name, pageRequest);
        Mockito.doReturn(List.of(type)).when(mockTypeRepository).findByIdIn(Set.of(BigInteger.valueOf(1)));

        var actual = searchService.searchByName(name, pageNumber, pageSize);
        Assertions.assertThat(actual.getContent()).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Return an empty list")
    void searchByNameTest_Fail() {
        Page<BeerEntity> beerEntityPage = Page.empty();
        var name = "Garage";

        Mockito.doReturn(beerEntityPage).when(mockBeerRepository)
                .findByNameContainingIgnoreCase(name, PageRequest.of(pageNumber, pageSize));

        var actual = searchService.searchByName(name, pageNumber, pageSize);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("Return a single beer, which satisfies all criteria")
    void searchByCriteriaTest_Success() {
        var beerEntityList = List.of(BeerData.beerEntityLager(BeerIds.beerId1));
        var type = TypeData.typeEntityLager(BigInteger.TWO);
        var beerDtoList = List.of(BeerData.beerDtoLager(BeerIds.beerId1));

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findAll(Mockito.<Specification<BeerEntity>>any());
        Mockito.doReturn(List.of(type)).when(mockTypeRepository).findByIdIn(Set.of(BigInteger.valueOf(2)));

        var minPrice = BigDecimal.valueOf(10);
        var maxPrice = BigDecimal.valueOf(80);
        var maxAlcohol = BigDecimal.valueOf(5);
        var actual = searchService.searchByCriteria(null,
                null,
                maxAlcohol,
                minPrice,
                maxPrice,
                null,
                pageNumber,
                pageSize);
        Assertions.assertThat(actual.getContent()).isNotNull().isEqualTo(beerDtoList);
        Mockito.verify(mockBeerSpecification, Mockito.times(1)).alcoholLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(1)).priceGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(1)).priceLessThan(Mockito.any());
    }


    @Test
    @DisplayName("Return an empty page")
    void searchByCriteriaTest_NoCriteria() {
        assertThatThrownBy(() -> searchService.searchByCriteria(null, null, null, null, null, null, pageNumber, pageSize))
                .isInstanceOf(BadRequestPivoException.class);
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).alcoholLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).priceGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).priceLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).hasProducer(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).alcoholGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).hasType(Mockito.any());
    }

    @Test
    @DisplayName("Return a page with one StoreDto")
    void searchInStockTest_Success() {
        storageEntity.setBeer(BeerIds.beerId1);
        storageEntity.setStore(StoreIds.storeId1);

        Mockito.doReturn(true).when(mockBeerRepository).existsById(BeerIds.beerId1);
        Mockito.doReturn(new PageImpl<>(List.of(storeEntity1), PageRequest.of(pageNumber, pageSize), 1))
                .when(mockStoreRepository)
                .findStoresByBeerFromStorage(beerEntityLager.getId(), PageRequest.of(pageNumber, pageSize));

        var actual = searchService.searchInStock(BeerIds.beerId1, pageNumber, pageSize);
        Assertions.assertThat(actual.getContent())
                .isNotNull()
                .isEqualTo(List.of(storeDto1));
    }

    @Test
    @DisplayName("Return an empty page")
    void searchInStockTest_NoStores() {
        Mockito.doReturn(true).when(mockBeerRepository).existsById(BeerIds.beerId1);
        Mockito.doReturn(Page.empty()).when(mockStoreRepository).findStoresByBeerFromStorage(BeerIds.beerId1, PageRequest.of(pageNumber, pageSize));

        var actual = searchService.searchInStock(BeerIds.beerId1, pageNumber, pageSize);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("Throw exception that no type was found")
    void searchInStockTest_NoBeers() {

        Mockito.doReturn(false).when(mockBeerRepository).existsById(BeerIds.beerId1);

        assertThatThrownBy(() -> searchService.searchInStock(BeerIds.beerId1, pageNumber, pageSize))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Return a page of two storeDtos")
    void searchForStoresTest_Success() {
        var page = new PageImpl<>(List.of(storeEntity1, storeEntity2), PageRequest.of(pageNumber, pageSize), 2);

        Mockito.doReturn(storeIds)
                .when(mockStorageRepository)
                .findStoreIdsWithAllBeers(beerIds);
        Mockito.doReturn(page)
                .when(mockStoreRepository)
                .findStoresByIdIn(storeIds, PageRequest.of(pageNumber, pageSize));

        var actual = searchService.searchForStores(beerIds, pageNumber, pageSize);
        Assertions.assertThat(actual.getContent())
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(List.of(storeDto1, storeDto2));
    }

    @Test
    @DisplayName("Return an empty page")
    void searchForStoresTest_NoStores() {
        var beerIds = List.of(BeerIds.beerId1, BeerIds.beerId2, BeerIds.beerId3);

        Mockito.doReturn(List.of())
                .when(mockStorageRepository)
                .findStoreIdsWithAllBeers(beerIds);

        var actual = searchService.searchForStores(beerIds, pageNumber, pageSize);
        Assertions.assertThat(actual.getContent())
                .isEmpty();
    }

    @Test
    @DisplayName("Return beer from a certain store")
    void searchForBeerTest_Success() {
        var storageEntityLager = StorageEntity.builder()
                .beer(BeerIds.beerId1)
                .store(StoreIds.storeId1)
                .count(BigInteger.TEN)
                .build();
        var storageEntityAle = StorageEntity.builder()
                .beer(BeerIds.beerId2)
                .store(StoreIds.storeId1)
                .count(BigInteger.valueOf(100))
                .build();
        var page = new PageImpl<>(List.of(beerEntityLager, beerEntityAle), PageRequest.of(pageNumber, pageSize), 2);

        Mockito.doReturn(Optional.of(typeEntityLager))
                .when(mockTypeRepository)
                .findById(beerEntityLager.getType());
        Mockito.doReturn(Optional.of(typeEntityAle))
                .when(mockTypeRepository)
                .findById(beerEntityAle.getType());
        Mockito.doReturn(page)
                .when(mockStoreRepository)
                .findBeersByStoreId(StoreIds.storeId1, PageRequest.of(pageNumber, pageSize));
        Mockito.doReturn(storageEntityLager)
                .when(mockStorageRepository)
                .findByBeerAndStore(BeerIds.beerId1, StoreIds.storeId1);
        Mockito.doReturn(storageEntityAle)
                .when(mockStorageRepository)
                .findByBeerAndStore(BeerIds.beerId2, StoreIds.storeId1);

        var actual = searchService.searchForBeer(StoreIds.storeId1, pageNumber, pageSize);
        Assertions.assertThat(actual.getContent())
                .isNotNull()
                .extracting(result -> result.get("beer"))
                .containsExactlyInAnyOrder(beerDtoLager, beerDtoAle);
        Assertions.assertThat(actual.getContent())
                .extracting(result -> result.get("quantity"))
                .containsExactlyInAnyOrder(BigInteger.TEN, BigInteger.valueOf(100));
    }

    @Test
    @DisplayName("Throw exception that no beer was found")
    void searchForBeerTest_NoBeers() {
        Mockito.doReturn(Page.empty())
                .when(mockStoreRepository)
                .findBeersByStoreId(StoreIds.storeId1, PageRequest.of(pageNumber, pageSize));

        assertThatThrownBy(() -> searchService.searchForBeer(BeerIds.beerId1, pageNumber, pageSize))
                .isInstanceOf(RuntimeException.class);
    }

}
