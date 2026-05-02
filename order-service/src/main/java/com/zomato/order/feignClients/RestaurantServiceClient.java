package com.zomato.order.feignClients;

import com.zomato.order.dto.response.MenuItemResponse;
import com.zomato.order.dto.response.RestaurantResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantServiceClient {

    @GetMapping("/api/restaurants/{id}")
    RestaurantResponse getRestaurant(@PathVariable Long id);

    @GetMapping("/api/menu-items/{id}")
    MenuItemResponse getMenuItem(@PathVariable Long id);
}
