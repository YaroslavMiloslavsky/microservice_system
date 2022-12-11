package com.yaro.product.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.UUID;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @Field("product_id")
    private UUID id;
    @Field("name")
    private String name;
    @Field("description")
    private String description;
    @Field("price")
    private BigDecimal price;
    @Field("amount")
    private int amount;
}
