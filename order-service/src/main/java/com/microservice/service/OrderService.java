package com.microservice.service;

import com.microservice.common.Payment;
import com.microservice.common.TransactionRequest;
import com.microservice.common.TransactionResponse;
import com.microservice.entity.Order;
import com.microservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestTemplate restTemplate;

    public TransactionResponse saveOrder(TransactionRequest request) {
        String responseMsg = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());

        //Rest call
        Payment pmtResponse = restTemplate.postForObject("http://localhost:9191/payment/doPayment", payment, Payment.class);

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
