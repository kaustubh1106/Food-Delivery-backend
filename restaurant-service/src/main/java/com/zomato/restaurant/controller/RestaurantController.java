package com.zomato.restaurant.controller;

import com.zomato.restaurant.dto.request.RestaurantRequest;
import com.zomato.restaurant.dto.response.MenuItemResponse;
import com.zomato.restaurant.dto.response.RestaurantResponse;
import com.zomato.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> addRestaurant(@Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.addRestaurant(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemResponse>> getRestaurantMenu(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantMenu(id));
    }
}
