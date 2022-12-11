package com.yaro.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DtoProductRequest {
    private static final String INVALID_NAME_MSG = "Please provide valid name";
    private static final String INVALID_DESCRIPTION_MSG = "Please provide valid description";
    private static final String INVALID_PRICE_MSG = "Price must be positive";
    private static final String INVALID_PRICE_LIMIT_MSG = "Price is above allowed limit";

    @NotBlank(message = INVALID_NAME_MSG)
    private String name;
    @NotBlank(message = INVALID_DESCRIPTION_MSG)
    private String description;
    @Min(value = 1, message = INVALID_PRICE_MSG)
    @Max(value = Long.MAX_VALUE, message = INVALID_PRICE_LIMIT_MSG)
    private BigDecimal price;
}
