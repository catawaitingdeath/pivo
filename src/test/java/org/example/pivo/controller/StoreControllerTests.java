package org.example.pivo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.client.EmployeeDto;
import org.example.pivo.client.EmployeeMapper;
import org.example.pivo.client.StoreEmployeeDto;
import org.example.pivo.config.PostgresInitializer;
import org.example.pivo.model.dto.StoreEmployeeInfoDto;
import org.example.pivo.repository.StoreRepository;
import org.example.pivo.utils.FileReaderUtility;
import org.example.pivo.utils.data.StoreData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.util.List;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@EnableWireMock(
        {
                @ConfigureWireMock(
                        name = "wm-server",
                        port = 10102
                )
        }
)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "tests")
@ContextConfiguration(initializers = {PostgresInitializer.class})
public class StoreControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private String id = "W_cPwW5eqk9kxe2OxgivJzVgu";

    private CreateEmployeeDto createAlice = FileReaderUtility.readFile("/controllerFiles/employee/createAlice.json", CreateEmployeeDto.class);
    private CreateEmployeeDto createBob = FileReaderUtility.readFile("/controllerFiles/employee/createBob.json", CreateEmployeeDto.class);
    private String Alice = FileReaderUtility.readFile("/controllerFiles/employee/Alice.json");
    private String Bob = FileReaderUtility.readFile("/controllerFiles/employee/Bob.json");

    @BeforeEach
    public void setUp() {
        storeRepository.deleteAll();
        WireMock.reset();
        WireMock.removeAllMappings();
    }

    private void forCreation() throws JsonProcessingException {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/employee/store/" + id))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                      "id": "%s",
                                      "employees": [%s, %s]
                                    }
                                """.formatted(id, Alice, Bob))));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(createAlice)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    %s
                                """.formatted(Alice))));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/employee"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(createBob)))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    %s
                                """.formatted(Bob))));
    }

    private void forDeletion() {
        WireMock.stubFor(WireMock.delete(WireMock.urlPathEqualTo("/employee/store/" + id))
                .inScenario("employees")
                .willSetStateTo("Deleted")
                .willReturn(WireMock.aResponse()
                        .withStatus(204)));

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/employee/store/" + id))
                .inScenario("employees")
                .whenScenarioStateIs("Deleted")
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"employees\":[]}")));
    }

    private Consumer<Integer> getWith2Employees() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/employee/store/" + id))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                      "id": "%s",
                                      "employees": [%s, %s]
                                    }
                                """.formatted(id, Alice, Bob))));
        return i -> WireMock.verify(i,
                WireMock.getRequestedFor(WireMock.urlPathEqualTo("/employee/store/%s".formatted(id))));
    }

    @Test
    void createTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "Ленинградское ш., 58с53, Москва",
                                    "phone": "123456789"
                                }"""))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var store = storeRepository.findAll();
        Assertions.assertThat(store)
                .hasSize(1);
    }

    @Test
    void getAllTest() throws Exception {
        var storeEntity1 = StoreData.storeEntityLenigradskoe();
        var storeEntity2 = StoreData.storeEntityProstornaya();
        storeRepository.save(storeEntity1);
        storeRepository.save(storeEntity2);
        mockMvc.perform(MockMvcRequestBuilders.get("/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content[0].address").value("Ленинградское ш., 58с53, Москва"))
                .andExpect(jsonPath("$.content[1].address").value("Просторная ул., 6, Москва"));
    }

    @Test
    void getStoreTest() throws Exception {
        var storeEntity = StoreData.storeEntityLenigradskoe(id);
        storeRepository.save(storeEntity);
        var storeDto = StoreData.storeDtoLeningradskoe(id);
        String jsonStore = objectMapper.writeValueAsString(storeDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/store/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonStore));
    }

    @Test
    void deleteStoreTest() throws Exception {
        forCreation();
        var storeEntity = StoreData.storeEntityLenigradskoe(id);
        storeRepository.save(storeEntity);
        var employeesString = mockMvc.perform(MockMvcRequestBuilders.get("/store/{id}/employees", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        var employees = objectMapper.readValue(employeesString, StoreEmployeeDto.class);
        Assertions.assertThat(employees.getEmployees())
                .hasSize(2);
        forDeletion();
        mockMvc.perform(MockMvcRequestBuilders.delete("/store/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        var storesAfter = storeRepository.findAll();
        Assertions.assertThat(storesAfter)
                .isEmpty();
        WireMock.verify(2, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/employee/store/%s".formatted(id))));
        WireMock.verify(1,
                WireMock.deleteRequestedFor(WireMock.urlPathEqualTo("/employee/store/%s".formatted(id))));
    }

    @Test
    void deleteWrongIdTest() throws Exception {
        forDeletion();
        var id = "Im_Pwg6Rg_9kxe2OgivJzVga";
        var error = mockMvc.perform(MockMvcRequestBuilders.delete("/store/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResolvedException().getMessage();
        Assertions.assertThat(error)
                .isEqualTo("Предоставлен неверный id");
    }

    @Test
    void getStoreEmployeeInfoTest() throws Exception {
        forCreation();
        var stub = getWith2Employees();
        var storeEntity = StoreData.storeEntityLenigradskoe(id);
        storeRepository.save(storeEntity);
        var employeesString = mockMvc.perform(MockMvcRequestBuilders.get("/store/{id}/employees", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        var employees = objectMapper.readValue(employeesString, StoreEmployeeDto.class);
        var infoDto = StoreEmployeeInfoDto.builder()
                .id(storeEntity.getId())
                .employees(employees.getEmployees())
                .address(storeEntity.getAddress())
                .phone(storeEntity.getPhone())
                .build();
        String jsonInfo = objectMapper.writeValueAsString(infoDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/store/{id}/employees", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonInfo));

        stub.accept(2);
    }

    @Test
    void getStoreEmployeeInfoWrongIdTest() throws Exception {
        var id = "Im_Pwg6Rg_9kxe2OgivJzVga";
        var error = mockMvc.perform(MockMvcRequestBuilders.get("/store/{id}/employees", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .getResolvedException()
                .getMessage();
        Assertions.assertThat(error)
                .isEqualTo("Предоставлен неверный id");
    }

    @Test
    void registerEmployeesTest() throws Exception {
        forCreation();
        var storeEntity = StoreData.storeEntityLenigradskoe(id);
        storeRepository.save(storeEntity);
        var content = List.of(createAlice, createBob);
        var Alice = FileReaderUtility.readFile("/controllerFiles/employee/Alice.json", EmployeeDto.class);
        var Bob = FileReaderUtility.readFile("/controllerFiles/employee/Bob.json", EmployeeDto.class);
        var storeEmployeeDto = StoreEmployeeDto.builder()
                .id(storeEntity.getId())
                .employees(List.of(Alice, Bob))
                .build();
        String json = objectMapper.writeValueAsString(storeEmployeeDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/store/{id}/registerEmployees", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(json));;

        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/employee/store/%s".formatted(id))));
        WireMock.verify(2, WireMock.postRequestedFor(WireMock.urlPathEqualTo("/employee")));
    }

    @Test
    void registerEmployeesWrongIdTest() throws Exception {
        var content = List.of(createAlice, createBob);
        var error = mockMvc.perform(MockMvcRequestBuilders.post("/store/{id}/registerEmployees", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResolvedException().getMessage();
        Assertions.assertThat(error)
                .isEqualTo("Предоставлен неверный id");
    }
}
