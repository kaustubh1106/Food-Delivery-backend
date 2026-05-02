package com.zomato.payment.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paymentTime;
}
