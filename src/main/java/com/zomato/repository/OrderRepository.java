package com.zomato.repository;

import com.zomato.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByDeliveryPartnerIsNullAndStatus(Order.OrderStatus order);
    List<Order> findByUserId(Long userId);
}
