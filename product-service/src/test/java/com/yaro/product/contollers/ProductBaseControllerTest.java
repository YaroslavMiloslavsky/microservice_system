package com.yaro.product.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaro.product.dto.DtoProductRequest;
import com.yaro.product.repository.ProductRepository;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test
 * @author Yarolsav Miloslavsky
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProductBaseControllerTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.3");

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Resource(name = "productRepository")
    private ProductRepository productRepository;

    @BeforeEach
    void clearRepository() {
        productRepository.deleteAll();
    }

    @Test
    public void createProductSuccessfulTest() throws Exception {
        DtoProductRequest productRequest = DtoProductRequest.builder()
                .name("Product_A")
                .description("Some product to test")
                .price(new BigDecimal(12345))
                .build();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString)
        ).andExpect(status().isCreated());
        assertThat(productRepository.findAll().size(), equalTo(1));
        assertThat(productRepository.findByName("Product_A"), notNullValue());
        assertThat(productRepository.findAll().get(0), notNullValue());
        assertThat(productRepository.findAll().get(0).getName(), equalTo("Product_A"));
        assertThat(productRepository.findAll().get(0).getDescription(), equalTo("Some product to test"));
        assertThat(productRepository.findAll().get(0).getPrice(), equalTo(new BigDecimal(12345)));
    }

    @Test
    public void createProductEmptyNameTest() throws Exception {
        DtoProductRequest productRequest = DtoProductRequest.builder()
                .name("")
                .description("Some product to test")
                .price(new BigDecimal(12345))
                .build();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        assertThat(productRepository.findAll().size(), equalTo(0));
        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString)
        ).andExpect(status().isBadRequest()).andReturn();
        String responseString = response.getResponse().getContentAsString();
        assertThat(responseString, notNullValue());
        assertThat(responseString, containsString("Please provide valid name"));
    }

    @Test
    public void createProductEmptyDescriptionTest() throws Exception {
        DtoProductRequest productRequest = DtoProductRequest.builder()
                .name("Product_A")
                .description("")
                .price(new BigDecimal(12345))
                .build();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        assertThat(productRepository.findAll().size(), equalTo(0));
        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString)
        ).andExpect(status().isBadRequest()).andReturn();
        String responseString = response.getResponse().getContentAsString();
        assertThat(responseString, notNullValue());
        assertThat(responseString, containsString("Please provide valid description"));
    }

    @Test
    public void createProductPriceNegativeTest() throws Exception {
        DtoProductRequest productRequest = DtoProductRequest.builder()
                .name("Product_A")
                .description("A A A ")
                .price(new BigDecimal(-2))
                .build();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        assertThat(productRepository.findAll().size(), equalTo(0));
        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString)
        ).andExpect(status().isBadRequest()).andReturn();
        String responseString = response.getResponse().getContentAsString();
        assertThat(responseString, notNullValue());
        assertThat(responseString, containsString("Price must be positive"));
    }

    @Test
    public void getAllProductsExistTest() throws Exception {
        DtoProductRequest productRequestA = DtoProductRequest.builder()
                .name("Product_A")
                .description("First Product ")
                .price(new BigDecimal(234))
                .build();
        DtoProductRequest productRequestB = DtoProductRequest.builder()
                .name("Product_B")
                .description("Second Product ")
                .price(new BigDecimal(234))
                .build();
        DtoProductRequest productRequestC = DtoProductRequest.builder()
                .name("Product_A")
                .description("First Product Again")
                .price(new BigDecimal(234))
                .build();
        assertThat(productRepository.findAll().size(), equalTo(0));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestA))
        ).andExpect(status().isCreated());
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestB))
        ).andExpect(status().isCreated());
        assertThat(productRepository.findAll().size(), equalTo(2));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestC))
        ).andExpect(status().isCreated());
        assertThat(productRepository.findAll().size(), equalTo(2));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String responseString = response.getResponse().getContentAsString();

        assertThat(responseString, containsString("Product_A"));
        assertThat(productRepository.findAll().size(), equalTo(2));
        assertThat(productRepository.findAll().get(0).getAmount(), equalTo(2));
    }

    @Test
    public void getAllProductsEmptyTest() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String responseString = response.getResponse().getContentAsString();

        assertThat(responseString, equalTo("[]"));
    }

    @Test
    public void getProductExistsTest() throws Exception {
        DtoProductRequest productRequestA = DtoProductRequest.builder()
                .name("Product_A")
                .description("First Product ")
                .price(new BigDecimal(234))
                .build();
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestA))
        ).andExpect(status().isCreated());

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/explore?productName=Product_A")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String responseString = response.getResponse().getContentAsString();

        assertThat(responseString, containsString("Product_A"));
    }

    @Test
    public void getProductDoesNotExistTest() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/explore?productName=Product_A")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
        String responseString = response.getResponse().getContentAsString();

        assertThat(responseString,containsString("No such product Product_A"));
    }
}
