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
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("integration-flow")
public class StoreKafkaIntegrationFlow {

    private final KafkaProperties kafkaProperties;
    private final AsyncStoreService service;
    private final ObjectMapper objectMapper;

    @Bean
    public KafkaMessageListenerContainer<String, String> storeKafkaContainer(ConsumerFactory<String, String> cf) {
        var props = new ContainerProperties(kafkaProperties.getFlows().getStoreCreation().getTopic());
        props.setGroupId(kafkaProperties.getGroupId());
        return new KafkaMessageListenerContainer<>(cf, props);
    }

    @Bean
    public IntegrationFlow storeCreationFlow(KafkaMessageListenerContainer<String, String> container) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(container))
                .wireTap(flow -> flow.handle(Message.class, (msg, headers) -> {
                    log.info("Received message headers: {}, body: {}", msg.getHeaders(), msg.getPayload());
                    return null;
                }))
                .filter(Message.class, this::filterBySourceHeader)
                //.transform(Message.class, this::parseJson)
                .handle(CreateStoreWithEmployeeDto.class, (dto, headers) -> {
                    service.create(dto);
                    return null;
                })
                .get();
    }

    private boolean filterBySourceHeader(Message<?> msg) {
        var source = Optional.ofNullable(msg.getHeaders().get("source"))
                .map(this::decodeHeader)
                .orElse(null);
        var expected = kafkaProperties.getFlows().getStoreCreation().getSource();
        var ok = expected.equals(source);
        if (!ok) {
            log.info("Expected source header: {}, but was: {}. Skipping", expected, source);
        }
        return ok;
    }

    private CreateStoreWithEmployeeDto parseJson(Message<?> msg) {
        try {
            return objectMapper.readValue((String) msg.getPayload(), CreateStoreWithEmployeeDto.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String decodeHeader(Object header) {
        if (header instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return header.toString();
    }
}
