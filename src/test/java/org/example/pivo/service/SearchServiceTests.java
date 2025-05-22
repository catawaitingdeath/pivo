package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.constants.BeerIds;
import org.example.pivo.constants.StoreIds;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StorageEntity;
import org.example.pivo.model.entity.TypeEntity;
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
        var actual = searchService.searchByCriteria(null, null, null, null, null, null, pageNumber, pageSize);
        Assertions.assertThat(actual).isNotNull().isEqualTo(Page.empty());
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
        var storageEntity = StorageData.storageEntity1();
        storageEntity.setBeer(BeerIds.beerId1);
        storageEntity.setStore(StoreIds.storeId1);
        var beerEntity = BeerData.beerEntityLager(BeerIds.beerId1);
        var storeEntity = StoreData.storeEntity1(StoreIds.storeId1);
        var storeDto = StoreData.storeDto1(StoreIds.storeId1);

        Mockito.doReturn(true).when(mockBeerRepository).existsById(BeerIds.beerId1);
        Mockito.doReturn(List.of(storeEntity))
                .when(mockStoreRepository)
                .findStoresByBeerFromStorage(beerEntity.getId());

        var actual = searchService.searchInStock(BeerIds.beerId1, pageNumber, pageSize);
        Assertions.assertThat(actual.getContent())
                .isNotNull()
                .isEqualTo(List.of(storeDto));
    }

    @Test
    @DisplayName("Return an empty page")
    void searchInStockTest_NoStores() {

        Mockito.doReturn(true).when(mockBeerRepository).existsById(BeerIds.beerId1);
        Mockito.doReturn(List.of()).when(mockStoreRepository).findStoresByBeerFromStorage(BeerIds.beerId1);

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

    /*@Test
    @DisplayName("Return result")
    void searchForStoresTest_Success() {
        var beerEntityLager = BeerData.beerEntityLager(beerId1);
        var beerEntityAle = BeerData.beerEntityAle(beerId2);
        var beerEntityPorter = BeerData.beerEntityPorter(beerId3);
        var beerEntityStout = BeerData.beerEntityStout(beerId4);
        var storages1 = new ArrayList<>();
        var storages2 = new ArrayList<>();
        var storages3 = new ArrayList<>();
        var storages4 = new ArrayList<>();
        var stores = new ArrayList<>(List.of(StoreData.storeEntity1(storeId1), StoreData.storeEntity2(storeId2),
                StoreData.storeEntity3(storeId3), StoreData.storeEntity4(storeId4)));
        storages1 = new ArrayList<>(List.of(buildStorageEntity(beerId1,storeId1), buildStorageEntity(beerId3,storeId1)));
        storages2 = new ArrayList<>(List.of(buildStorageEntity(beerId2,storeId2), buildStorageEntity(beerId3,storeId2)));
        storages3 = new ArrayList<>(List.of(buildStorageEntity(beerId1,storeId3), buildStorageEntity(beerId2,storeId3),
                              buildStorageEntity(beerId3,storeId3), buildStorageEntity(beerId4,storeId3)));
        storages4.add(buildStorageEntity(beerId4,storeId4));
        var expected = new HashSet<>(List.of(StoreData.storeDto2(storeId2), StoreData.storeDto3(storeId3)));


        Mockito.doReturn(beerEntityLager).when(mockBeerRepository).findByName(beerEntityLager.getName());
        Mockito.doReturn(beerEntityAle).when(mockBeerRepository).findByName(beerEntityAle.getName());
        Mockito.doReturn(beerEntityPorter).when(mockBeerRepository).findByName(beerEntityPorter.getName());
        Mockito.doReturn(beerEntityStout).when(mockBeerRepository).findByName(beerEntityStout.getName());

        Mockito.doReturn(List.of(storages1.get(0), storages3.get(0))).when(mockStorageRepository).findAllByBeerAndCountGreaterThan(beerId1, BigInteger.ZERO);
        Mockito.doReturn(List.of(storages2.get(0), storages3.get(1))).when(mockStorageRepository).findAllByBeerAndCountGreaterThan(beerId2, BigInteger.ZERO);
        Mockito.doReturn(List.of(storages1.get(1), storages2.get(1), storages3.get(2))).when(mockStorageRepository).findAllByBeerAndCountGreaterThan(beerId3, BigInteger.ZERO);
        Mockito.doReturn(storages4).when(mockStorageRepository).findAllByBeerAndCountGreaterThan(beerId4, BigInteger.ZERO);
        Mockito.doReturn(Optional.of(stores.get(0))).when(mockStoreRepository).findById(storeId1);
        Mockito.doReturn(Optional.of(stores.get(1))).when(mockStoreRepository).findById(storeId2);
        Mockito.doReturn(Optional.of(stores.get(2))).when(mockStoreRepository).findById(storeId3);
        Mockito.doReturn(Optional.of(stores.get(3))).when(mockStoreRepository).findById(storeId4);

        var actual = searchService.searchForStores(List.of(beerEntityAle.getName(), beerEntityPorter.getName()));
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }*/

    @Test
    @DisplayName("Return result")
    void searchForBeerTest_Success() {
        var beerEntity1 = BeerData.beerEntityLager(BeerIds.beerId1);
        var beerEntity2 = BeerData.beerEntityAle(BeerIds.beerId2);
        var beerDtoLager = BeerData.beerDtoLager(BeerIds.beerId1);
        var beerDtoAle = BeerData.beerDtoAle(BeerIds.beerId2);
        var typeEntityLager = TypeData.typeEntityLager(BigInteger.valueOf(2));
        var typeEntityAle = TypeData.typeEntityAle(BigInteger.valueOf(1));
        var page = new PageImpl<>(List.of(beerEntity1, beerEntity2), PageRequest.of(pageNumber, pageSize), 2);

        Mockito.doReturn(List.of(typeEntityLager, typeEntityAle))
                .when(mockTypeRepository)
                .findByIdIn(Set.of(BigInteger.valueOf(2), BigInteger.valueOf(1)));
        Mockito.doReturn(page)
                .when(mockStoreRepository)
                .findBeersByStoreId(StoreIds.storeId1, PageRequest.of(pageNumber, pageSize));

        var actual = searchService.searchForBeer(StoreIds.storeId1, pageNumber, pageSize);
        Assertions.assertThat(actual.getContent())
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(List.of(beerDtoLager, beerDtoAle));
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


    StorageEntity buildStorageEntity(String beerId, String storeId) {
        return StorageEntity.builder()
                .beer(beerId)
                .store(storeId)
                .count(BigInteger.valueOf(10))
                .build();
    }
}
