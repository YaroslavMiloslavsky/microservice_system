package com.yaro.order.controller;

import com.yaro.order.domain.Order;
import com.yaro.order.dto.DtoOrderRequest;
import com.yaro.order.service.OrderLogicService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private final OrderLogicService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Validated @RequestBody DtoOrderRequest orderRequest) {
        Order order = orderService.placeOrder(orderRequest);
        HttpStatus httpStatus = order == null || CollectionUtils.isEmpty(order.getItems()) ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(order);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> handleSQLExceptions(SQLIntegrityConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("sqlState", exception.getSQLState());
        errors.put("error", exception.getMessage());
        return errors;
    }
}
