package org.example.pivo.controller;

import org.example.pivo.config.BasicIntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public abstract class BasicControllerTests extends BasicIntegrationTests {
    @Autowired
    protected MockMvc mockMvc;

}
