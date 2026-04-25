package com.zomato.dto.response;

import lombok.Data;

@Data
public class DeliveryPartnerResponse {

    private Long id;
    private String name;
    private String phone;
    private String vehicleType;
    private String availabilityStatus;
    private String currentLocation;
    private Double rating;
}
