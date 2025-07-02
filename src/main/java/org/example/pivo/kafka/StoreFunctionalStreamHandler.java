package org.example.pivo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pivo.config.property.KafkaProperties;
import org.example.pivo.model.dto.CreateStoreWithEmployeeDto;
import org.example.pivo.service.AsyncStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("functional-stream")
public class StoreFunctionalStreamHandler {

    private final AsyncStoreService service;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    @Bean
    public Consumer<Message<String>> storeCreationHandler() {
        return msg -> {
            var source = resolveHeader(msg, "source");
            var expected = kafkaProperties.getFlows().getStoreCreation().getSource();
            var payload = msg.getPayload();

            log.info("Received message  headers: {}, body: {}", msg.getHeaders(), payload);

            if (!expected.equals(source)) {
                log.info("Expected source header: {}, but was: {}. Skipping", expected, source);
                return;
            }

            try {
                var dto = objectMapper.readValue(payload, CreateStoreWithEmployeeDto.class);
                service.create(dto);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    private String resolveHeader(Message<?> msg, String name) {
        var value = msg.getHeaders().get(name);
        if (value instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return value != null ? value.toString() : null;
    }
}
