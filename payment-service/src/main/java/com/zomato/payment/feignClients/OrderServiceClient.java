package com.zomato.payment.feignClients;

import com.zomato.payment.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{id}")
    OrderResponse getOrderById(@PathVariable Long id);
}
