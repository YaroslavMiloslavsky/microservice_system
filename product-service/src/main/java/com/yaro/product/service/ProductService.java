package com.yaro.product.service;

import com.yaro.product.domain.Product;
import com.yaro.product.dto.DtoProductRequest;

import java.util.List;

public interface ProductService {
    Product saveProduct(DtoProductRequest productRequest);
    List<Product> getAllProducts();
    Product getProduct(String productName);
}
