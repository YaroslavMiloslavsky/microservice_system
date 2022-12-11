package com.yaro.product.controller;

import com.yaro.product.domain.Product;
import com.yaro.product.dto.DtoProductRequest;
import com.yaro.product.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductBaseController {

    @Resource(name = "productServiceImpl")
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/explore")
    public ResponseEntity<Product> getProduct(@RequestParam String productName) {
        Product product = productService.getProduct(productName);
        HttpStatus httpStatus = product == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
        return ResponseEntity.status(httpStatus).body(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody DtoProductRequest productRequest) {
        Product product = productService.saveProduct(productRequest);
        HttpStatus httpStatus = product == null ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(product);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(NoSuchElementException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("product", "No such product " + ex.getMessage());
        return errors;
    }
}
