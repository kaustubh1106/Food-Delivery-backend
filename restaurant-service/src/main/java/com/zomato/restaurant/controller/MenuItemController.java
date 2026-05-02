package com.zomato.restaurant.controller;

import com.zomato.restaurant.dto.request.MenuItemRequest;
import com.zomato.restaurant.dto.response.MenuItemResponse;
import com.zomato.restaurant.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemResponse> addMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.addMenuItem(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getMenuItem(id));
    }
}
