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
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    private String storeId1 = "S7TKIwtHDfoLOESVj16e_v3ie";
    private String storeId2 = "inSV3fZx2Ai1bn0CjaDvFkIxw";
    private String storageId1 = "phpIoHFCT8fc5GEZqCZQimYxD";


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
        Mockito.doReturn(List.of(storageEntity)).when(mockStorageRepository).findAll(Mockito.<Specification<StorageEntity>>any());
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
        Mockito.doReturn(emptyList).when(mockStorageRepository).findAll(Mockito.<Specification<StorageEntity>>any());

        var actual = searchService.searchInStock(beerEntity.getName());
        Assertions.assertThat(actual).isNotNull().isEqualTo(emptyList);
    }
}
