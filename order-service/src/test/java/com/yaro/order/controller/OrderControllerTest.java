package com.yaro.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaro.order.dto.DtoOrderItemRequest;
import com.yaro.order.dto.DtoOrderRequest;
import com.yaro.order.repository.OrderRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.31").withDatabaseName("order");

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

    @Resource(name = "orderRepository")
    private OrderRepository orderRepository;

    @BeforeEach
    public void clearRepository() {
        orderRepository.deleteAll();
    }

    @Test
    public void placeOrder_basicTest() throws Exception {

        DtoOrderRequest orderRequest = DtoOrderRequest.builder()
                .items(mockDtoOrderItemsRequest())
                .build();
        String requestString = objectMapper.writeValueAsString(orderRequest);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString)).andExpect(status().isCreated()).andReturn();
        assertThat(orderRepository.findAll().size(), equalTo(1));

        String responseString = response.getResponse().getContentAsString();
        assertThat(responseString, containsString("Code_1"));
        assertThat(responseString, containsString("Code_2"));
    }

    @Test
    public void placeOrder_alreadyExistsTest() throws Exception {

        DtoOrderRequest orderRequest = DtoOrderRequest.builder()
                .items(mockDtoOrderItemsRequest())
                .build();
        String requestString = objectMapper.writeValueAsString(orderRequest);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString)).andExpect(status().isCreated()).andReturn();
        assertThat(orderRepository.findAll().size(), equalTo(1));
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString)).andExpect(status().isBadRequest()).andReturn();
        assertThat(orderRepository.findAll().size(), equalTo(1));

        String responseString = response.getResponse().getContentAsString();
        assertThat(responseString, containsString("Duplicate entry"));
    }

    private List<DtoOrderItemRequest> mockDtoOrderItemsRequest() {
        List<DtoOrderItemRequest> itemsDtos = new ArrayList<>();

        itemsDtos.add(DtoOrderItemRequest.builder()
                .code("Code_1")
                .price(new BigDecimal(12345))
                .quantity(2)
                .build());
        itemsDtos.add(DtoOrderItemRequest.builder()
                .code("Code_2")
                .price(new BigDecimal(1122))
                .quantity(69)
                .build());
        return itemsDtos;
    }
}
