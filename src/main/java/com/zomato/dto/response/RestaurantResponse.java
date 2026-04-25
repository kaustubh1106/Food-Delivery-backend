package com.zomato.dto.response;

import lombok.Data;

@Data
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String cuisine;
    private Double rating;
    private String operatingHours;
}
