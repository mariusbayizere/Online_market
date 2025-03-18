package com.example.Project_Online_market.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Project_Online_market.SecurityConfig.RabbitMQConfig;
import com.example.Project_Online_market.dto.EmailMessage;
import com.example.Project_Online_market.dto.OrderMessage;
import com.example.Project_Online_market.dto.PaymentMessage;

@Service
public class RabbitMQSender {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendOrderMessage(OrderMessage message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, 
                                      RabbitMQConfig.ORDER_ROUTING_KEY, 
                                      message);
        System.out.println("Order message sent: " + message.getOrderId());
    }
    
    public void sendPaymentMessage(PaymentMessage message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PAYMENT_EXCHANGE, 
                                      RabbitMQConfig.PAYMENT_ROUTING_KEY, 
                                      message);
        System.out.println("Payment message sent: " + message.getPaymentId());
    }
    
    public void sendEmailMessage(EmailMessage message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PAYMENT_EXCHANGE, 
                                      RabbitMQConfig.EMAIL_ROUTING_KEY, 
                                      message);
        System.out.println("Email message sent to: " + message.getRecipient());
    }
}