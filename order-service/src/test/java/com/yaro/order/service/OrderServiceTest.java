package com.yaro.order.service;

import com.yaro.order.domain.Order;
import com.yaro.order.domain.OrderItem;
import com.yaro.order.dto.DtoOrderItemRequest;
import com.yaro.order.dto.DtoOrderRequest;
import com.yaro.order.repository.OrderItemRepository;
import com.yaro.order.repository.OrderRepository;
import com.yaro.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Captor
    private ArgumentCaptor<Order> orderArgumentCaptor;

    @Test
    public void placeOrder_basicTest() {
        Mockito.when(orderRepository.save(any())).thenReturn(mockOrder());
        Order placedOrder = orderService.placeOrder(mockOrderRequest());
        assertThat(placedOrder, notNullValue());

        Mockito.verify(orderRepository).save(orderArgumentCaptor.capture());
        Order savedOrder = orderArgumentCaptor.getValue();

        assertThat(savedOrder.getItems(), notNullValue());
        assertThat(savedOrder.getItems().size(), equalTo(2));
        assertThat(savedOrder.getItems().get(0).getCode(), equalTo("Code_1"));
        assertThat(savedOrder.getItems().get(1).getCode(), equalTo("Code_2"));
    }

    @Test
    public void placeOrder_invalidRequestTest() {
        Order placedOrder = orderService.placeOrder(null);
        assertThat(placedOrder, nullValue());
    }

    private DtoOrderRequest mockOrderRequest() {
        return DtoOrderRequest.builder()
                .items(mockDtoOrderItemsRequest())
                .build();
    }

    private List<DtoOrderItemRequest> mockDtoOrderItemsRequest() {
        List<DtoOrderItemRequest> itemsDtos = new ArrayList<>();

        itemsDtos.add(DtoOrderItemRequest.builder()
                .code("Code_1")
                .price(new BigDecimal(12345))
                .quantity(2)
                .build());
        itemsDtos.add(DtoOrderItemRequest.builder()
                .code("Code_2")
                .price(new BigDecimal(1122))
                .quantity(69)
                .build());
        return itemsDtos;
    }

    private Order mockOrder() {
        return Order.builder()
                .orderNumber("Test Order Number")
                .id(234L)
                .items(mockOrderItems())
                .build();
    }

    private List<OrderItem> mockOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        items.add(OrderItem.builder()
                        .code("Code_1")
                        .id(123L)
                        .quantity(23)
                        .price(new BigDecimal(2345))
                        .build());
        return items;
    }
}
