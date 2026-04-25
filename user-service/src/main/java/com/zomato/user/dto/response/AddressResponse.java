package com.zomato.user.dto.response;

import lombok.Data;

@Data
public class AddressResponse {

    private Long id;
    private String street;
    private String city;
    private String state;
    private String pincode;
}
