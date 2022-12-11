package com.yaro.product.service.impl;

import com.yaro.product.domain.Product;
import com.yaro.product.dto.DtoProductRequest;
import com.yaro.product.repository.ProductRepository;
import com.yaro.product.service.ProductService;
import com.yaro.product.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service(value = "productServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product saveProduct(DtoProductRequest productRequest) {
        Product product = null;
        if (ProductUtils.isValidRequest(productRequest)) {
            product = Product.builder()
                    .id(UUID.randomUUID())
                    .name(productRequest.getName())
                    .price(productRequest.getPrice())
                    .description(productRequest.getDescription())
                    .build();
            Product existingProduct = productRepository.findByName(product.getName());
            if(existingProduct != null){
                log.info("{} already exists", product.getName());
                existingProduct.setAmount(existingProduct.getAmount() + 1);
                existingProduct.setDescription(product.getDescription());
                productRepository.save(existingProduct);
                product = existingProduct;
            }
            else {
                product.setAmount(1);
                productRepository.save(product);
            }
        }
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(String productName) throws NoSuchElementException{
        Product product = productRepository.findByName(productName);
        if(product == null) throw new NoSuchElementException(productName);
        return product;
    }
}
