package org.example.pivo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.model.dto.CreateStoreWithEmployeeDto;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


public class StoreKafkaListenerTests extends BasicKafkaTests {

    @Test
    void storeMessageShouldTriggerEmployeeCreation() {
        var employee = CreateEmployeeDto.builder()
                .name("Иван")
                .surname("Иванов")
                .phone("79684551122")
                .email("ivan.ivanov@example.com")
                .position("продавец")
                .salary(BigInteger.valueOf(45000))
                .store("Магазин у дома")
                .build();
        var storeWithEmployee = CreateStoreWithEmployeeDto.builder()
                .address("г. Тверь, ул. Советская, д. 12")
                .phone("79684551112")
                .employees(List.of(employee))
                .build();

        kafkaTemplate.send(createMessage(kafkaProperties.getFlows().getStoreCreation(), storeWithEmployee));
        Awaitility.waitAtMost(5, TimeUnit.SECONDS)
                .until(() -> results.size() == 1);
        var result = results.getFirst();
        Assertions.assertThat(result).isNotNull();
        var source = Optional.ofNullable(result.headers().lastHeader("source"))
                .map(h -> new String(h.value(), StandardCharsets.UTF_8))
                .orElse(null);
        Assertions.assertThat(source)
                .isEqualTo(kafkaProperties.getFlows().getEmployeeCreation().getSource());
        Assertions.assertThat(result.value())
                .isEqualTo(employee);
    }

    @Test
    void storeMessageShouldTriggerEmployeeCreationTwice() {
        var employee = CreateEmployeeDto.builder()
                .name("Иван")
                .surname("Иванов")
                .phone("79684551122")
                .email("ivan.ivanov@example.com")
                .position("продавец")
                .salary(BigInteger.valueOf(45000))
                .store("Магазин у дома")
                .build();
        var employee1 = CreateEmployeeDto.builder()
                .name("Иван")
                .surname("Иваненко")
                .phone("79684551123")
                .email("ivan.ivanenko@example.com")
                .position("продавец")
                .salary(BigInteger.valueOf(45001))
                .store("Магазин у дома")
                .build();
        var storeWithEmployee = CreateStoreWithEmployeeDto.builder()
                .address("г. Тверь, ул. Советская, д. 12")
                .phone("8-910-123-45-67")
                .employees(List.of(employee, employee1))
                .build();

        kafkaTemplate.send(createMessage(kafkaProperties.getFlows().getStoreCreation(), storeWithEmployee));
        Awaitility.waitAtMost(5, TimeUnit.SECONDS)
                .until(() -> results.size() == 2);
        List<String> sources = results.stream()
                .map(ConsumerRecord::headers)
                .map(h -> Optional.ofNullable(h.lastHeader("source")))
                .map(h -> h.map(Header::value))
                .map(o -> o.map(b -> new String(b, StandardCharsets.UTF_8)))
                .map(o -> o.orElse(null))
                .toList();
        Assertions.assertThat(sources)
                .containsOnly(kafkaProperties.getFlows().getEmployeeCreation().getSource());
        var payloads = results.stream().map(ConsumerRecord::value).toList();
        Assertions.assertThat(payloads)
                .containsExactlyInAnyOrder(employee, employee1);
    }


}
