package com.zomato.service;


import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zomato.entity.DeliveryPartner;
import com.zomato.entity.Order;
import com.zomato.entity.Restaurant;
import com.zomato.repository.DeliveryPartnerRepository;
import com.zomato.repository.OrderRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class OrderAssignmentService {
    private final OrderRepository orderRepository;
    private final DeliveryPartnerRedisService redisService;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    @Scheduled(fixedRate=30000)
    public void assignPartner(){
        List<Order> orders =  orderRepository.findByDeliveryPartnerIsNullAndStatus(Order.OrderStatus.PLACED);
        for(Order order : orders){
            Restaurant restaurant = order.getRestaurant(); 
            List<Long> nearestPartners = redisService.findNearestAvailablePartner(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude(), 10.0);
            if(!nearestPartners.isEmpty()){
                Long partnerId = nearestPartners.get(0); 
                DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId).orElse(null);
                if(partner!=null){
                    order.setDeliveryPartner(partner);
                    redisService.markUnavailable(partnerId);
                    orderRepository.save(order);
                }
            }
        }
    }
}
