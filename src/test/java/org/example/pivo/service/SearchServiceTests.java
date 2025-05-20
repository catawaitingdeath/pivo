package org.example.pivo.service;

import org.assertj.core.api.Assertions;
import org.example.pivo.components.BeerSpecification;
import org.example.pivo.mapper.BeerMapper;
import org.example.pivo.mapper.StoreMapper;
import org.example.pivo.model.entity.BeerEntity;
import org.example.pivo.model.entity.StorageEntity;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    private String idLager = "W_cPwW5eqk9kxe2OxgivJzVgu";
    private String idAle = "9kxe2OW_cPwW5exgivJ";
    private String beerId1 = "2IVPACIrIT-Tr2Gw-JoApXZKT";
    private String beerId2 = "Ota-_XO_6Dc2nCKEU7LEmsi1K";
    private String beerId3 = "RIFXvJ-4y2kmWY84Pqyku";
    private String beerId4 = "i0k-Ch3R0B5NNZh1x6IL2";
    private String storeId1 = "S7TKIwtHDfoLOESVj16e_v3ie";
    private String storeId2 = "inSV3fZx2Ai1bn0CjaDvFkIxw";
    private String storeId3 = "XFPSsDSfIlQ77CKTRkkBq";
    private String storeId4 = "W_cPwW5eqk9kxe2OxgivJzVgu";
    private Integer pageNumber = 0;
    private Integer pageSize = 10;


    @BeforeEach
    void setup() {
        mockTypeRepository = mock(TypeRepository.class);
        mockBeerRepository = mock(BeerRepository.class);
        mockStorageRepository = mock(StorageRepository.class);
        mockStoreRepository = mock(StoreRepository.class);
        mockBeerSpecification = mock(BeerSpecification.class);
        searchService = new SearchService(mockBeerRepository, mockTypeRepository, mockStorageRepository, mockStoreRepository, beerMapper, storeMapper, mockBeerSpecification);
    }

    @Test
    @DisplayName("Return a full list")
    void searchByNameTest_Success() {
        List<BeerEntity> beerEntityList = List.of(BeerData.beerEntityAle(idAle));
        var name = "OlL";
        var type = TypeData.typeEntityAle(BigInteger.ONE);

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findByNameContainingIgnoreCase(name);
        Mockito.doReturn(Optional.of(type)).when(mockTypeRepository).findById(BigInteger.ONE);

        var actual = searchService.searchByName(name, pageNumber, pageSize);
        Assertions.assertThat(actual).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Return an empty list")
    void searchByNameTest_Fail() {
        List<BeerEntity> beerEntityList = List.of();
        var name = "Garage";

        Mockito.doReturn(beerEntityList).when(mockBeerRepository).findByNameContainingIgnoreCase(name);

        var actual = searchService.searchByName(name, pageNumber, pageSize);
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
        var actual = searchService.searchByCriteria(null, null, maxAlcohol, minPrice, maxPrice, null, pageNumber, pageSize);
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

        var actual = searchService.searchByCriteria(null, null, null, null, null, null, pageNumber, pageSize);
        Assertions.assertThat(actual).isNotNull().isEqualTo(emptyList);
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).alcoholLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).priceGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).priceLessThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).hasProducer(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).alcoholGreaterThan(Mockito.any());
        Mockito.verify(mockBeerSpecification, Mockito.times(0)).hasType(Mockito.any());
    }

    @Test
    @DisplayName("Return result")
    void searchInStockTest_Success() {
        var storageEntity = StorageData.storageEntity1();
        storageEntity.setBeer(beerId1);
        storageEntity.setStore(storeId1);
        var beerEntity = BeerData.beerEntityLager(beerId1);
        var storeEntity = StoreData.storeEntity1(storeId1);
        var storeDto = StoreData.storeDto1(storeId1);

        Mockito.doReturn(beerEntity).when(mockBeerRepository).findByName(beerEntity.getName());
        Mockito.doReturn(List.of(storageEntity)).when(mockStorageRepository).findAllByBeerAndCountGreaterThan(beerEntity.getId(), BigInteger.ZERO);
        Mockito.doReturn(Optional.of(storeEntity)).when(mockStoreRepository).findById(storeId1);

        var actual = searchService.searchInStock(beerEntity.getName());
        Assertions.assertThat(actual)
                .isNotNull()
                .isEqualTo(List.of(storeDto));
    }

    @Test
    @DisplayName("Return an empty list")
    void searchInStockTest_NoBeers() {
        var beerEntity = BeerData.beerEntityLager(beerId1);
        var emptyList = List.of();

        Mockito.doReturn(beerEntity).when(mockBeerRepository).findByName(beerEntity.getName());
        Mockito.doReturn(emptyList).when(mockStorageRepository).findAllByBeerAndCountGreaterThan(beerEntity.getId(), BigInteger.ZERO);

        var actual = searchService.searchInStock(beerEntity.getName());
        Assertions.assertThat(actual).isNotNull().isEqualTo(emptyList);
    }

    @Test
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
    }

    @Test
    @DisplayName("Return result")
    void searchForBeerTest_Success() {
        var beerEntity1 = BeerData.beerEntityLager(beerId1);
        var beerEntity2 = BeerData.beerEntityAle(beerId2);
        var storeEntity = StoreData.storeEntity1(storeId1);
        var storageEntity1 = StorageEntity.builder()
                .beer(beerEntity1.getId())
                .store(storeEntity.getId())
                .build();
        var storageEntity2 = StorageEntity.builder()
                .beer(beerEntity2.getId())
                .store(storeEntity.getId())
                .build();
        storageEntity2.setId(storageEntity1.getId());
        var beerDtoLager = BeerData.beerDtoLager(beerId1);
        var beerDtoAle = BeerData.beerDtoAle(beerId2);
        var typeEntityLager = TypeData.typeEntityLager(BigInteger.valueOf(2));
        var typeEntityAle = TypeData.typeEntityAle(BigInteger.valueOf(1));

        Mockito.doReturn(Optional.of(typeEntityLager)).when(mockTypeRepository).findById(BigInteger.valueOf(2));
        Mockito.doReturn(Optional.of(typeEntityAle)).when(mockTypeRepository).findById(BigInteger.valueOf(1));
        Mockito.doReturn(Optional.of(beerEntity1)).when(mockBeerRepository).findById(beerEntity1.getId());
        Mockito.doReturn(Optional.of(beerEntity2)).when(mockBeerRepository).findById(beerEntity2.getId());
        Mockito.doReturn(List.of(storageEntity1, storageEntity2)).when(mockStorageRepository).findAll(Mockito.<Specification<StorageEntity>>any());

        var actual = searchService.searchForBeer(storeId1, pageNumber, pageSize);
        Assertions.assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(List.of(beerDtoLager, beerDtoAle));
    }

    @Test
    @DisplayName("Return an empty list")
    void searchForBeerTest_NoBeers() {
        var beerEntity = BeerData.beerEntityLager(beerId1);

        Mockito.doReturn(null).when(mockStorageRepository).findAll(Mockito.<Specification<StorageEntity>>any());

        var actual = searchService.searchForBeer(beerEntity.getName(), pageNumber, pageSize);
        Assertions.assertThat(actual).isNotNull().isEqualTo(Page.empty());
    }




    StorageEntity buildStorageEntity(String beerId, String storeId) {
        return StorageEntity.builder()
                .beer(beerId)
                .store(storeId)
                .count(BigInteger.valueOf(10))
                .build();
    }
}
