package com.zomato.order.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "delivery-service")
public interface DeliveryServiceClient {

    @GetMapping("/api/delivery-partners/nearest")
    List<Long> getAvailablePartner(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusKm
    );

    @PutMapping("/api/delivery-partners/{id}/unavailable")
    String markUnavailable(@PathVariable Long id);
}
