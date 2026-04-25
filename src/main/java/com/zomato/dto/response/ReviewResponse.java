package com.zomato.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private Double rating;
    private String comment;
    private LocalDateTime reviewDate;
}
