package com.yaro.order.repository;

import com.yaro.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("orderItemRepository")
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
