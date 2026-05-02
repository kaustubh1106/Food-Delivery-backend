package com.zomato.review.feignClients;

import com.zomato.review.dto.response.RestaurantResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantServiceClient {

    @GetMapping("/api/restaurants/{id}")
    RestaurantResponse getRestaurant(@PathVariable Long id);
}
