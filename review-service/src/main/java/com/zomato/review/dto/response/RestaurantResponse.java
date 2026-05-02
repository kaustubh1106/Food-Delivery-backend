package com.zomato.review.dto.response;

import lombok.Data;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private Double rating;
}
