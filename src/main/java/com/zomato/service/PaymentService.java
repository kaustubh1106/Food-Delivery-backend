package com.zomato.service;

import com.zomato.dto.request.PaymentRequest;
import com.zomato.dto.response.PaymentResponse;
import com.zomato.entity.Order;
import com.zomato.entity.Payment;
import com.zomato.exception.ResourceNotFoundException;
import com.zomato.repository.OrderRepository;
import com.zomato.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new IllegalArgumentException("Payment already recorded for order id: " + request.getOrderId());
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .amount(order.getTotalAmount())
                .paymentStatus(Payment.PaymentStatus.COMPLETED)
                .build();

        Payment saved = paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setId(saved.getId());
        response.setOrderId(order.getId());
        response.setPaymentMethod(saved.getPaymentMethod().name());
        response.setAmount(saved.getAmount());
        response.setPaymentStatus(saved.getPaymentStatus().name());
        response.setPaymentTime(saved.getPaymentTime());
        return response;
    }
}
