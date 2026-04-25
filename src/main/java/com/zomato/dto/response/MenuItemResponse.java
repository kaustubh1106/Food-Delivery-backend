package com.zomato.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemResponse {

    private Long id;
    private String itemName;
    private String description;
    private BigDecimal price;
    private String category;
    private Long restaurantId;
}
