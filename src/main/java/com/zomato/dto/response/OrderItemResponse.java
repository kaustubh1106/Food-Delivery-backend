package com.zomato.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private Long id;
    private String itemName;
    private Integer quantity;
    private BigDecimal price;
}
