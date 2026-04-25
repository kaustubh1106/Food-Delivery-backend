package com.zomato.service;

import com.zomato.dto.request.RestaurantRequest;
import com.zomato.dto.response.MenuItemResponse;
import com.zomato.dto.response.RestaurantResponse;
import com.zomato.entity.Restaurant;
import com.zomato.exception.ResourceNotFoundException;
import com.zomato.repository.MenuItemRepository;
import com.zomato.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public RestaurantResponse addRestaurant(RestaurantRequest request) {
        Restaurant restaurant = modelMapper.map(request, Restaurant.class);
        Restaurant saved = restaurantRepository.save(restaurant);
        return modelMapper.map(saved, RestaurantResponse.class);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(r -> modelMapper.map(r, RestaurantResponse.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getRestaurantMenu(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(item -> {
                    MenuItemResponse response = modelMapper.map(item, MenuItemResponse.class);
                    response.setRestaurantId(restaurantId);
                    return response;
                })
                .toList();
    }
}
