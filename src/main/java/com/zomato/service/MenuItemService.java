package com.zomato.service;

import com.zomato.dto.request.MenuItemRequest;
import com.zomato.dto.response.MenuItemResponse;
import com.zomato.entity.MenuItem;
import com.zomato.entity.Restaurant;
import com.zomato.exception.ResourceNotFoundException;
import com.zomato.repository.MenuItemRepository;
import com.zomato.repository.RestaurantRepository;
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
}
