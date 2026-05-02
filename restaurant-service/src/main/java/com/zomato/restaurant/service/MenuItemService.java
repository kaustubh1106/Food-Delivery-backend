package com.zomato.restaurant.service;

import com.zomato.restaurant.dto.request.MenuItemRequest;
import com.zomato.restaurant.dto.response.MenuItemResponse;
import com.zomato.restaurant.entity.MenuItem;
import com.zomato.restaurant.entity.Restaurant;
import com.zomato.restaurant.exception.ResourceNotFoundException;
import com.zomato.restaurant.repository.MenuItemRepository;
import com.zomato.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public MenuItemResponse addMenuItem(MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + request.getRestaurantId()));

        MenuItem menuItem = MenuItem.builder()
                .itemName(request.getItemName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .restaurant(restaurant)
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);

        MenuItemResponse response = modelMapper.map(saved, MenuItemResponse.class);
        response.setRestaurantId(restaurant.getId());
        return response;
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        MenuItemResponse response = modelMapper.map(menuItem, MenuItemResponse.class);
        response.setRestaurantId(menuItem.getRestaurant().getId());
        return response;
    }
}
