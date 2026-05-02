package com.zomato.order.service;

import com.zomato.order.dto.request.OrderItemRequest;
import com.zomato.order.dto.request.OrderRequest;
import com.zomato.order.dto.response.MenuItemResponse;
import com.zomato.order.dto.response.OrderItemResponse;
import com.zomato.order.dto.response.OrderResponse;
import com.zomato.order.dto.response.RestaurantResponse;
import com.zomato.order.dto.response.UserResponse;
import com.zomato.order.entity.Order;
import com.zomato.order.entity.OrderItem;
import com.zomato.order.exception.ResourceNotFoundException;
import com.zomato.order.feignClients.DeliveryServiceClient;
import com.zomato.order.feignClients.RestaurantServiceClient;
import com.zomato.order.feignClients.UserServiceClient;
import com.zomato.order.repository.OrderRepository;
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
    private final UserServiceClient userClient;
    private final RestaurantServiceClient restaurantClient;
    private final DeliveryServiceClient deliveryClient;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        UserResponse user = userClient.getUser(request.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + request.getUserId());
        }

        RestaurantResponse restaurant = restaurantClient.getRestaurant(request.getRestaurantId());
        if (restaurant == null) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId());
        }
        Order order = Order.builder()
                .userId(request.getUserId())
                .restaurantId(request.getRestaurantId())
                .restaurantName(restaurant.getName())
                .status(Order.OrderStatus.PLACED)
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItemResponse menuItem = restaurantClient.getMenuItem(itemReq.getMenuItemId());
            if (menuItem == null) {
                throw new ResourceNotFoundException(
                        "Menu item not found with id: " + itemReq.getMenuItemId());
            }

            BigDecimal itemTotal = menuItem.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItemId(menuItem.getId())
                    .itemName(menuItem.getItemName())
                    .price(menuItem.getPrice())
                    .quantity(itemReq.getQuantity())
                    .build();

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // 5. Try assign nearest delivery partner using restaurant location
        if (restaurant.getLocation() != null
                && restaurant.getLocation().getLatitude() != null
                && restaurant.getLocation().getLongitude() != null) {

            List<Long> nearestPartners = deliveryClient.getAvailablePartner(
                    restaurant.getLocation().getLatitude(),
                    restaurant.getLocation().getLongitude(),
                    3.0
            );

            if (nearestPartners != null && !nearestPartners.isEmpty()) {
                Long partnerId = nearestPartners.get(0);
                order.setDeliveryPartnerId(partnerId);
                deliveryClient.markUnavailable(partnerId);
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
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setRestaurantId(order.getRestaurantId());
        response.setRestaurantName(order.getRestaurantName());
        response.setDeliveryPartnerId(order.getDeliveryPartnerId());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderTime(order.getOrderTime());

        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponse i = new OrderItemResponse();
                    i.setId(item.getId());
                    i.setMenuItemId(item.getMenuItemId());
                    i.setItemName(item.getItemName());
                    i.setQuantity(item.getQuantity());
                    i.setPrice(item.getPrice());
                    return i;
                })
                .toList();
        response.setItems(items);
        return response;
    }
}
