package com.yaro.product.service;

import com.yaro.product.domain.Product;
import com.yaro.product.dto.DtoProductRequest;
import com.yaro.product.repository.ProductRepository;
import com.yaro.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;

    @Test
    public void saveProduct_BasicTest() {
        // Such Product does not exist
        Mockito.when(productRepository.findByName(anyString())).thenReturn(null);
        Mockito.when(productRepository.save(any())).thenReturn(mockProductValid());

        Product product = productService.saveProduct(mockProductRequest());

        Mockito.verify(productRepository, Mockito.times(1)).findByName(anyString());
        Mockito.verify(productRepository, Mockito.times(1)).save(any());

        assertThat(product, notNullValue());
        assertThat(product.getName(), equalToIgnoringCase("test request name"));
        assertThat(product.getDescription(), equalToIgnoringCase("test request description"));
        assertThat(product.getPrice(), is(new BigDecimal(122223)));
        assertThat(product.getAmount(), is(1));
    }

    @Test
    public void saveProduct_productAlreadyExistsTest() {
        Mockito.when(productRepository.findByName(anyString())).thenReturn(mockProductAlreadyExistsValid());
        Mockito.when(productRepository.save(any())).thenReturn(mockProductValid());

        Product product = productService.saveProduct(mockProductRequest());

        Mockito.verify(productRepository, Mockito.times(1)).findByName(anyString());
        Mockito.verify(productRepository, Mockito.times(1)).save(any());

        assertThat(product, notNullValue());
        assertThat(product.getName(), equalToIgnoringCase("test name"));
        assertThat(product.getDescription(), equalToIgnoringCase("test request description"));
        assertThat(product.getPrice(), is(new BigDecimal(12345)));
        assertThat(product.getAmount(), is(2));
    }

    @Test
    public void saveProduct_nullTest() {
        Product product = productService.saveProduct(null);

        Mockito.verify(productRepository, Mockito.times(0)).findByName(anyString());
        Mockito.verify(productRepository, Mockito.times(0)).save(any());

        assertThat(product, nullValue());
    }

    @Test
    public void saveProduct_invalidProductTooBigPriceTest() {
        DtoProductRequest productRequest = mockProductRequest();
        productRequest.setPrice(new BigDecimal(Long.MAX_VALUE + 1L));
        Product product = productService.saveProduct(productRequest);

        Mockito.verify(productRepository, Mockito.times(0)).findByName(anyString());
        Mockito.verify(productRepository, Mockito.times(0)).save(any());

        assertThat(product, nullValue());
    }

    @Test
    public void saveProduct_invalidProductEmptyNameTest() {
        DtoProductRequest productRequest = mockProductRequest();
        productRequest.setName("");
        Product product = productService.saveProduct(productRequest);

        Mockito.verify(productRepository, Mockito.times(0)).findByName(anyString());
        Mockito.verify(productRepository, Mockito.times(0)).save(any());

        assertThat(product, nullValue());
    }

    public void saveProduct_invalidProductEmptyDescriptionTest() {
        DtoProductRequest productRequest = mockProductRequest();
        productRequest.setDescription("");
        Product product = productService.saveProduct(productRequest);

        Mockito.verify(productRepository, Mockito.times(0)).findByName(anyString());
        Mockito.verify(productRepository, Mockito.times(0)).save(any());

        assertThat(product, nullValue());
    }

    @Test
    public void saveProduct_invalidProductTest() {
        DtoProductRequest productRequest = DtoProductRequest.builder().build();
        Product product = productService.saveProduct(productRequest);

        Mockito.verify(productRepository, Mockito.times(0)).findByName(anyString());
        Mockito.verify(productRepository, Mockito.times(0)).save(any());

        assertThat(product, nullValue());
    }

    private Product mockProductValid() {
        return Product.builder()
                .id(UUID.randomUUID())
                .name("Test Name")
                .description("Test Description")
                .price(new BigDecimal(12345))
                .build();
    }

    private Product mockProductAlreadyExistsValid() {
        return Product.builder()
                .id(UUID.randomUUID())
                .name("Test Name")
                .description("Test Description")
                .price(new BigDecimal(12345))
                .amount(1)
                .build();
    }

    private DtoProductRequest mockProductRequest() {
        return DtoProductRequest.builder()
                .name("Test Request Name")
                .description("Test Request Description")
                .price(new BigDecimal(122223))
                .build();
    }

}
