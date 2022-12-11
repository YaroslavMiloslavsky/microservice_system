package com.yaro.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DtoOrderItemRequest {
    private static final String EMPTY_CODE_MSG = "Code cannot be empty";
    private static final String PRICE_MIN_MSG = "Price must be positive number";
    private static final String PRICE_MAX_MSG = "Price is above limit";
    private static final String ILLEGAL_QUANTITY_MIN_MSG = "Quantity must be positive number";
    private static final String ILLEGAL_QUANTITY_MAX_MSG = "Quantity is above limit";

    @NotEmpty(message = EMPTY_CODE_MSG)
    private String code;
    @Min(value = 1, message = PRICE_MIN_MSG)
    @Min(value = Long.MAX_VALUE - 10, message = PRICE_MAX_MSG)
    private BigDecimal price;
    @Min(value = 0, message = ILLEGAL_QUANTITY_MIN_MSG)
    @Max(value = Integer.MAX_VALUE - 10, message = ILLEGAL_QUANTITY_MAX_MSG)
    private Integer quantity;
}
