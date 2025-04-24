package org.example.pivo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store")
public class StoreEntity {

    @Id
    private String id;

    @Column(nullable = false)
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Адрес магазина не должен содержать больше 50 символов")
    private String address;

    @Column(nullable = false)
    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 25, message = "Телефон магазина не должен содержать больше 25 символов")
    private String phone;

}
