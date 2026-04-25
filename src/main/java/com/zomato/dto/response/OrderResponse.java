package com.zomato.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private Long userId;
    private Long restaurantId;
    private String restaurantName;
    private Long deliveryPartnerId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderTime;
    private List<OrderItemResponse> items;
}
