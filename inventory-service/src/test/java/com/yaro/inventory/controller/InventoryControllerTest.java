package com.yaro.inventory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaro.inventory.domain.Inventory;
import com.yaro.inventory.repository.InventoryRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class InventoryControllerTest {

    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.31").withDatabaseName("inventory");

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Resource(name = "inventoryRepository")
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void clearRepository(){
        inventoryRepository.deleteAll();
    }

    @Test
    void inStock_basicTest() throws Exception {
        Inventory inventory = new Inventory();
        inventory.setCode("Inv_a");
        inventory.setId(123L);
        inventory.setQuantity(2);

        inventoryRepository.save(inventory);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/inventory?code=Inv_a")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String responseString = response.getResponse().getContentAsString();

        assertThat(responseString, equalTo("true"));
        assertThat(inventoryRepository.findAll().size(), equalTo(1));
    }

    @Test
    void inStock_notInStockTest() throws Exception {
        Inventory inventory = new Inventory();
        inventory.setCode("Inv_a");
        inventory.setId(123L);
        inventory.setQuantity(2);

        inventoryRepository.save(inventory);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/inventory?code=Inv_b")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String responseString = response.getResponse().getContentAsString();

        assertThat(responseString, equalTo("false"));
        assertThat(inventoryRepository.findAll().size(), equalTo(1));
    }
}
