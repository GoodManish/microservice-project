package com.microservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.common.Payment;
import com.microservice.common.TransactionRequest;
import com.microservice.common.TransactionResponse;
import com.microservice.entity.Order;
import com.microservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
@Slf4j
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    @Value("${microservice.payment-service.endpoints.endpoint.uri}")
    private String PAYMENT_SERVICE_URL;

    public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
        String responseMsg = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        log.info("Order-Service request : {}", new ObjectMapper().writeValueAsString(request));

        //Rest call
        Payment pmtResponse = restTemplate.postForObject(PAYMENT_SERVICE_URL, payment, Payment.class);
        log.info("Payment-Service response : {}", new ObjectMapper().writeValueAsString(pmtResponse ));

        responseMsg = pmtResponse.getPaymentStatus().equals("success") ? "Payment Processed & Order placed successfully" : "Payment FAILURE! Order added back to cart";


        orderRepository.save(order);
//      return new TransactionResponse(order, pmtResponse.getAmount(), pmtResponse.getTransactionId(),responseMsg);
        return TransactionResponse.builder()
                .order(order)
                .amount(pmtResponse.getAmount())
                .transactionId(pmtResponse.getTransactionId())
                .message(responseMsg)
                .build();
    }
}
