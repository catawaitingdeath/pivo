package org.example.pivo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pivo.config.property.KafkaProperties;
import org.example.pivo.model.dto.CreateStoreWithEmployeeDto;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncStoreService {
    private final KafkaProperties properties;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void create(CreateStoreWithEmployeeDto createStoreDto) {
        log.info("Работает. Получены данные: {}", createStoreDto);

        var topic = properties.getFlows().getEmployeeCreation().getTopic();
        var source = properties.getFlows().getEmployeeCreation().getSource();

        createStoreDto.getEmployees().forEach(emp -> {
            var msg = MessageBuilder
                    .withPayload(emp)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("source", source)
                    .build();

            kafkaTemplate.send(msg);
            log.info("Sent employee to topic {}: {}", topic, emp);
        });

    }
}
