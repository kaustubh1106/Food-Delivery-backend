package com.zomato.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestaurantRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Cuisine is required")
    private String cuisine;

    @NotBlank(message = "Operating hours is required")
    private String operatingHours;

    private LocationRequest location;
}
