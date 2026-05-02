package com.zomato.payment.service;

import com.zomato.payment.dto.request.PaymentRequest;
import com.zomato.payment.dto.response.OrderResponse;
import com.zomato.payment.dto.response.PaymentResponse;
import com.zomato.payment.entity.Payment;
import com.zomato.payment.exception.ResourceNotFoundException;
import com.zomato.payment.feignClients.OrderServiceClient;
import com.zomato.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderClient;

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {
        OrderResponse order = orderClient.getOrderById(request.getOrderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with id: " + request.getOrderId());
        }

        // 2. Prevent duplicate payment for same order
        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Payment already recorded for order id: " + request.getOrderId());
        }

        // 3. Create payment with amount snapshot from order
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .amount(order.getTotalAmount())
                .paymentStatus(Payment.PaymentStatus.COMPLETED)
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for order id: " + orderId));
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setAmount(payment.getAmount());
        response.setPaymentStatus(payment.getPaymentStatus().name());
        response.setPaymentTime(payment.getPaymentTime());
        return response;
    }
}
