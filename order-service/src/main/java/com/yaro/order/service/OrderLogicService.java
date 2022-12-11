package com.yaro.order.service;

import com.yaro.order.domain.Order;
import com.yaro.order.dto.DtoOrderRequest;

public interface OrderLogicService {
    Order placeOrder(DtoOrderRequest orderRequest);
}
