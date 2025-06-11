package org.example.pivo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pivo.components.NanoIdGenerator;
import org.example.pivo.model.dto.BeerInStockDto;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "beer")
public class BeerEntity {

    @Id
    private String id;

    @Column(nullable = false)
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Название пива не должно содержать больше 100 символов")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Имя производителя пива не должно содержать больше 50 символов")
    private String producer;

    @NotNull
    @Column(nullable = false)
    @Positive(message = "Цена пива должна быть положительной")
    @Digits(integer = 6, fraction = 2, message = "Цена пива должна соответствовать заданным параметрам")
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    @Positive(message = "Процентное содержание алкоголя должно быть положительным")
    @Digits(
            integer = 2,
            fraction = 1,
            message = "Процентное содержание алкоголя должно соответствовать заданным параметрам"
    )
    private BigDecimal alcohol;

    @NotNull
    @Column(nullable = false)
    @Positive(message = "Id типа пива должен быть положительным")
    @Digits(integer = 1000, fraction = 0, message = "Id типа пива должен быть целым")
    private BigInteger type;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = NanoIdGenerator.gen();
        }
    }

}
