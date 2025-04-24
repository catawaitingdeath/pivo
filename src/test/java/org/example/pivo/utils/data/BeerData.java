package org.example.pivo.utils.data;

import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class BeerData {

    public static BeerEntity beerEntity(String id, String typeName) {

        if (Objects.equals(typeName, "эль")) {
            return BeerEntity.builder()
                    .id(id)
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer("Арвиай")
                    .price(BigDecimal.valueOf(120))
                    .alcohol(BigDecimal.valueOf(8))
                    .type(BigInteger.valueOf(1))
                    .build();
        } else {
            return BeerEntity.builder()
                    .id(id)
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания")
                    .price(BigDecimal.valueOf(70))
                    .alcohol(BigDecimal.valueOf(5))
                    .type(BigInteger.valueOf(2))
                    .build();
        }
    }

    public static BeerEntity beerEntity(String typeName) {
        if (Objects.equals(typeName, "эль")) {
            return BeerEntity.builder()
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer("Арвиай")
                    .price(BigDecimal.valueOf(120))
                    .alcohol(BigDecimal.valueOf(8))
                    .type(BigInteger.valueOf(1))
                    .build();
        } else {
            return BeerEntity.builder()
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания")
                    .price(BigDecimal.valueOf(70))
                    .alcohol(BigDecimal.valueOf(5))
                    .type(BigInteger.valueOf(2))
                    .build();
        }
    }

    public static BeerDto beerDto(String id, String typeName) {
        if (Objects.equals(typeName, "эль")) {
            return BeerDto.builder()
                    .id(id)
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer("Арвиай")
                    .price(BigDecimal.valueOf(120))
                    .alcohol(BigDecimal.valueOf(8))
                    .typeName(typeName)
                    .build();
        } else {
            return BeerDto.builder()
                    .id(id)
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания")
                    .price(BigDecimal.valueOf(70))
                    .alcohol(BigDecimal.valueOf(5))
                    .typeName(typeName)
                    .build();
        }
    }

    public static CreateBeerDto createBeerDto(String typeName) {
        if (Objects.equals(typeName, "лагер")) {
            return CreateBeerDto.builder()
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания")
                    .price(BigDecimal.valueOf(70))
                    .alcohol(BigDecimal.valueOf(5))
                    .typeName(typeName)
                    .build();
        } else {
            return CreateBeerDto.builder()
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer("Арвиай")
                    .price(BigDecimal.valueOf(120))
                    .alcohol(BigDecimal.valueOf(5))
                    .typeName(typeName)
                    .build();
        }
    }
}
