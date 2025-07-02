package org.example.pivo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.pivo.repository.BeerRepository;
import org.example.pivo.repository.StorageRepository;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.repository.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@ContextConfiguration(
        initializers = {
                KafkaInitializer.class,
                PostgresInitializer.class
        }
)
@ActiveProfiles({"kafka-listener", "tests"}) // kafka-listener, functional-stream, integration-flow
public abstract class BasicIntegrationTests {
    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(10102))
            .configureStaticDsl(true)
            .build();
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected BeerRepository beerRepository;
    @Autowired
    protected StorageRepository storageRepository;
    @Autowired
    protected StoreRepository storeRepository;
    @Autowired
    protected TypeRepository typeRepository;


    @BeforeEach
    void basicIntegrationTestsSetup() {
        storageRepository.deleteAll();
        storeRepository.deleteAll();
        beerRepository.deleteAll();
        WireMock.reset();
        WireMock.removeAllMappings();
    }
}
