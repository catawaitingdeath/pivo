package org.example.pivo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.config.BasicIntegrationTests;
import org.example.pivo.config.KafkaInitializer;
import org.example.pivo.config.property.KafkaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicKafkaTests extends BasicIntegrationTests {

    @Autowired
    protected KafkaProperties kafkaProperties;
    @Autowired
    protected KafkaTemplate<String, CreateEmployeeDto> kafkaTemplate;

    protected List<ConsumerRecord<String, CreateEmployeeDto>> results = new ArrayList<>();

    @BeforeEach
    void basicKafkaTestsSetUp() {
        KafkaInitializer.resetOffsets();
        results.clear();
    }

    @KafkaListener(
            topics = "#{@kafkaProperties.flows.employeeCreation.topic}",
            groupId = "tests-${random.uuid}"
    )
    public void listen(ConsumerRecord<String, CreateEmployeeDto> record) {
        results.add(record);
    }


    protected Message<?> createMessage(KafkaProperties.FlowProperties properties, Object data) {
        return createMessage(properties.getTopic(), properties.getSource(), data);
    }

    protected Message<?> createMessage(String topic, String source, Object data) {
        return MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("source", source)
                .build();
    }
}
