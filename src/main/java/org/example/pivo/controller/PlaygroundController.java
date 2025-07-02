package org.example.pivo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.example.pivo.client.CreateEmployeeDto;
import org.example.pivo.client.EmployeeClient;
import org.example.pivo.client.EmployeeDto;
import org.example.pivo.client.StoreEmployeeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RestController
@RequestMapping("/playground")
@RequiredArgsConstructor
@Tag(name = "????????")
public class PlaygroundController {
    private final EmployeeClient employeeClient;

    @GetMapping("/jdk")
    public void jdk() throws Exception {
        var storeId = "S7TKIwtHDfoLOESVj16e_v3ie";
        var jdkHttpClient = HttpClient.newHttpClient();
        log.info("JDK: отправляем POST #1");
        var postBody1 = """
                    {
                      "name": "Alice",
                      "surname": "Johnson",
                      "phone": "79684551122",
                      "email": "alice.johnson@example.com",
                      "position": "Cashier",
                      "salary": 30000,
                      "store": "%s"
                    }
                """.formatted(storeId);
        var post1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/employee"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody1))
                .build();
        var postResp1 = jdkHttpClient.send(post1, HttpResponse.BodyHandlers.ofString());
        log.info("JDK: ответ POST #1: {}", postResp1.body());

        log.info("JDK: отправляем POST #2");
        var postBody2 = """
                    {
                      "name": "Bob",
                      "surname": "Smith",
                      "phone": "79684551888",
                      "email": "bob.smith@example.com",
                      "position": "Manager",
                      "salary": 50000,
                      "store": "%s"
                    }
                """.formatted(storeId);
        var post2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/employee"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody2))
                .build();
        var postResp2 = jdkHttpClient.send(post2, HttpResponse.BodyHandlers.ofString());
        log.info("JDK: ответ POST #2: {}", postResp2.body());

        log.info("JDK: отправляем GET");
        var get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/employee/store/%s".formatted(storeId)))
                .GET()
                .build();
        var getResp1 = jdkHttpClient.send(get, HttpResponse.BodyHandlers.ofString());
        log.info("JDK: ответ GET до удаления: {}", getResp1.body());

        log.info("JDK: отправляем DELETE");
        var del = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/employee/store/%s".formatted(storeId)))
                .DELETE()
                .build();
        jdkHttpClient.send(del, HttpResponse.BodyHandlers.discarding());
        log.info("JDK: DELETE завершён");

        log.info("JDK: отправляем GET после удаления");
        var getResp2 = jdkHttpClient.send(get, HttpResponse.BodyHandlers.ofString());
        log.info("JDK: ответ GET после удаления: {}", getResp2.body());

    }

    @GetMapping("/ok")
    public void okHttp() throws Exception {
        var storeId = "S7TKIwtHDfoLOESVj16e_v3ie";
        var okHttpClient = new OkHttpClient();
        log.info("OkHttp: отправляем POST #1");
        var body1 = """
                    {
                      "name": "Alice",
                      "surname": "Johnson",
                      "phone": "79684551122",
                      "email": "alice.johnson@example.com",
                      "position": "Cashier",
                      "salary": 30000,
                      "store": "%s"
                    }
                """.formatted(storeId);
        var mediaType = MediaType.get("application/json; charset=utf-8");
        var postReq1 = new Request.Builder()
                .url("http://localhost:8081/employee")
                .post(RequestBody.create(body1, mediaType))
                .build();
        try (var resp = okHttpClient.newCall(postReq1).execute()) {
            log.info("OkHttp: ответ POST #1: {}", resp.body().string());
        }
        log.info("OkHttp: отправляем POST #2");
        var body2 = """
                    {
                      "name": "Bob",
                      "surname": "Smith",
                      "phone": "79684551888",
                      "email": "bob.smith@example.com",
                      "position": "Manager",
                      "salary": 50000,
                      "store": "%s"
                    }
                """.formatted(storeId);
        var postReq2 = new Request.Builder()
                .url("http://localhost:8081/employee")
                .post(RequestBody.create(body2, mediaType))
                .build();
        try (var resp = okHttpClient.newCall(postReq2).execute()) {
            log.info("OkHttp: ответ POST #2: {}", resp.body().string());
        }

        log.info("OkHttp: отправляем GET");
        var getReq = new Request.Builder()
                .url("http://localhost:8081/employee/store/%s".formatted(storeId))
                .build();
        try (var resp = okHttpClient.newCall(getReq).execute()) {
            log.info("OkHttp: ответ GET до удаления: {}", resp.body().string());
        }

        log.info("OkHttp: отправляем DELETE");
        var delReq = new Request.Builder()
                .url("http://localhost:8081/employee/store/%s".formatted(storeId))
                .delete()
                .build();
        okHttpClient.newCall(delReq).execute().close();
        log.info("OkHttp: DELETE завершён");

        log.info("OkHttp: отправляем GET после удаления");
        try (var resp = okHttpClient.newCall(getReq).execute()) {
            log.info("OkHttp: ответ GET после удаления: {}", resp.body().string());
        }
    }

    @GetMapping("/feign")
    public void feignHttp() throws Exception {
        var storeId = "W_cPwW5eqk9kxe2OxgivJzVgu";
        log.info("Feign: отправляем POST #1");
        var create1 = CreateEmployeeDto.builder()
                .name("Alice")
                .surname("Johnson")
                .phone("79684551668")
                .email("alice.johnson@example.com")
                .position("Cashier")
                .salary(BigInteger.valueOf(30000))
                .store(storeId)
                .build();
        EmployeeDto emp1 = employeeClient.createEmployee(create1);
        log.info("Feign: ответ POST #1: {}", emp1);

        log.info("Feign: отправляем POST #2");
        var create2 = CreateEmployeeDto.builder()
                .name("Bob")
                .surname("Smith")
                .phone("79684551888")
                .email("bob.smith@example.com")
                .position("Manager")
                .salary(BigInteger.valueOf(50000))
                .store(storeId)
                .build();
        EmployeeDto emp2 = employeeClient.createEmployee(create2);
        log.info("Feign: ответ POST #2: {}", emp2);

        log.info("Feign: отправляем GET");
        StoreEmployeeDto storeBefore = employeeClient.getEmployees(storeId);
        log.info("Feign: ответ GET до удаления: {}", storeBefore);

        log.info("Feign: отправляем DELETE");
        employeeClient.deleteEmployees(storeId);
        log.info("Feign: DELETE завершён");

        log.info("Feign: отправляем GET после удаления");
        StoreEmployeeDto storeAfter = employeeClient.getEmployees(storeId);
        log.info("Feign: ответ GET после удаления: {}", storeAfter);

    }
}
