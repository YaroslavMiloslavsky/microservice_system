package com.yaro.product.repository;

import com.yaro.product.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository(value = "productRepository")
public interface ProductRepository extends MongoRepository<Product, UUID> {
    Product findByName(String name);
}
