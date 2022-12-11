package com.yaro.order.dto;

import com.yaro.order.domain.OrderItem;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DtoOrderRequest {
    private static final String EMPTY_ITEM_LIST_MSG = "Order must contatin items";

    @Size(min = 1, message = EMPTY_ITEM_LIST_MSG)
    private List<DtoOrderItemRequest> items;
}
