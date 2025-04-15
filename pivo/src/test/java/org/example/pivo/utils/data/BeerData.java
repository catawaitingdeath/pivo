package org.example.pivo.utils.data;

import org.example.pivo.model.dto.BeerDto;
import org.example.pivo.model.dto.CreateBeerDto;
import org.example.pivo.model.entity.BeerEntity;

import java.util.Objects;

public class BeerData {

    public static BeerEntity beerEntity(Long id, String typeName) {
        if(Objects.equals(typeName, "ale")){
            return BeerEntity.builder()
                    .id(id)
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer( "Арвиай")
                    .price(120L)
                    .alcohol(8L)
                    .type(2L)
                    .build();
        }
        else{
            return BeerEntity.builder()
                    .id(id)
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания'")
                    .price(70L)
                    .alcohol(5L)
                    .type(1L)
                    .build();
        }
    }

    public static BeerEntity beerEntity(String typeName) {
        if(Objects.equals(typeName, "ale")){
            return BeerEntity.builder()
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer( "Арвиай")
                    .price(120L)
                    .alcohol(8L)
                    .type(2L)
                    .build();
        }
        else{
            return BeerEntity.builder()
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания'")
                    .price(70L)
                    .alcohol(5L)
                    .type(1L)
                    .build();
        }
    }

    public static BeerDto beerDto(String typeName) {
        if(Objects.equals(typeName, "ale")){
            return BeerDto.builder()
                    .id(2L)
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer( "Арвиай")
                    .price(120L)
                    .alcohol(8L)
                    .typeName(typeName)
                    .build();
        }
        else{
            return BeerDto.builder()
                    .id(1L)
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания'")
                    .price(70L)
                    .alcohol(5L)
                    .typeName(typeName)
                    .build();
        }
    }

    public static CreateBeerDto createBeerDto(String typeName) {
        if(Objects.equals(typeName, "lager")){
            return CreateBeerDto.builder()
                    .name("Жигули Барное светлое фильтрованное")
                    .producer("Московская пивоваренная компания'")
                    .price(70L)
                    .alcohol(5L)
                    .typeName(typeName)
                    .build();
        }
        else{
            return CreateBeerDto.builder()
                    .name("Troll Brew IPA светлое нефильтрованное")
                    .producer("Арвиай")
                    .price(120L)
                    .alcohol(8L)
                    .typeName(typeName)
                    .build();
        }
    }
}
