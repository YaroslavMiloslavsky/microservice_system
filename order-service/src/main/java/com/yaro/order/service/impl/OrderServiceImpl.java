package com.yaro.order.service.impl;

import com.yaro.order.domain.Order;
import com.yaro.order.domain.OrderItem;
import com.yaro.order.dto.DtoOrderItemRequest;
import com.yaro.order.dto.DtoOrderRequest;
import com.yaro.order.repository.OrderItemRepository;
import com.yaro.order.repository.OrderRepository;
import com.yaro.order.service.OrderLogicService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service("orderServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderLogicService {

    @Resource(name = "orderRepository")
    private final OrderRepository orderRepository;

    @Resource(name = "orderItemRepository")
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public Order placeOrder(DtoOrderRequest orderRequest) {
        if(orderRequest != null && CollectionUtils.isNotEmpty(orderRequest.getItems())) {
            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .build();
            List<OrderItem> orderedItems = orderRequest.getItems()
                    .stream().map(this::convertFromDto).toList();
            order.setItems(orderedItems);
            Order savedOrder = orderRepository.save(order);
            if(CollectionUtils.isNotEmpty(savedOrder.getItems())){
                log.info("Saved list of size {}", savedOrder.getItems().size());
                return savedOrder;
            }
        }
        return null;
    }

    public OrderItem convertFromDto(DtoOrderItemRequest dtoOrderItemRequest) {
        log.info("Converting DTO to Order Item");
        return OrderItem.builder()
                .code(dtoOrderItemRequest.getCode())
                .price(dtoOrderItemRequest.getPrice())
                .quantity(dtoOrderItemRequest.getQuantity())
                .build();
    }
}
