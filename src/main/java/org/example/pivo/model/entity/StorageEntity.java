package org.example.pivo.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pivo.components.NanoIdGenerator;

import java.math.BigInteger;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "storage")
public class StorageEntity {

    @Id
    private String id;

    @Column(nullable = false)
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Id пива не должен содержать больше 50 символов")
    private String beer;

    @Column(nullable = false)
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Id магазина не должен содержать больше 50 символов")
    private String store;


    @NotNull
    @Column(nullable = false)
    @PositiveOrZero(message = "Число бутылок пива должно быть неотрицательным")
    @Digits(integer = 1000, fraction = 0, message = "Число бутылок пива должно быть целым")
    private BigInteger count;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = NanoIdGenerator.gen();
        }
    }
}
