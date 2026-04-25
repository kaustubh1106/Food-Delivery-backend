package com.zomato.service;

import com.zomato.dto.request.OrderItemRequest;
import com.zomato.dto.request.OrderRequest;
import com.zomato.dto.response.OrderItemResponse;
import com.zomato.dto.response.OrderResponse;
import com.zomato.entity.*;
import com.zomato.exception.ResourceNotFoundException;
import com.zomato.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final DeliveryPartnerRedisService deliveryPartnerRedisService;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + request.getRestaurantId()));

        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .status(Order.OrderStatus.PLACED)
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item not found with id: " + itemRequest.getMenuItemId()));

            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(itemRequest.getQuantity())
                    .price(menuItem.getPrice())
                    .build();

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        List<Long> nearestPartners = deliveryPartnerRedisService.findNearestAvailablePartner(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude(), 3.0);
        if(!nearestPartners.isEmpty()){
            Long partnerId = nearestPartners.get(0); 
            DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId).orElse(null);
            if(partner!=null){
                order.setDeliveryPartner(partner);
                deliveryPartnerRedisService.markUnavailable(partnerId);
            }
        }
        Order saved = orderRepository.save(order);

        return mapToOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setRestaurantId(order.getRestaurant().getId());
        response.setRestaurantName(order.getRestaurant().getName());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderTime(order.getOrderTime());

        if (order.getDeliveryPartner() != null) {
            response.setDeliveryPartnerId(order.getDeliveryPartner().getId());
        }

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setItemName(item.getMenuItem().getItemName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getPrice());
                    return itemResponse;
                })
                .toList();

        response.setItems(itemResponses);
        return response;
    }
}
