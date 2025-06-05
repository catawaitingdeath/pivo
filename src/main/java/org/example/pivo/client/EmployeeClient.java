package org.example.pivo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employeeClient", url = "${beer-application.urls.employee}")
public interface EmployeeClient {

    @GetMapping("/employee/store/{storeId}")
    StoreEmployeeDto getEmployees(@PathVariable String storeId);

    @DeleteMapping("/employee/store/{storeId}")
    void deleteEmployees(@PathVariable String storeId);

    @PostMapping("/employee")
    EmployeeDto createEmployee(@RequestBody CreateEmployeeDto request);
}
