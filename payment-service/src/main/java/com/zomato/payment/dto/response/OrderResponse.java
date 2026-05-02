package com.zomato.payment.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private String status;
    private BigDecimal totalAmount;
}
