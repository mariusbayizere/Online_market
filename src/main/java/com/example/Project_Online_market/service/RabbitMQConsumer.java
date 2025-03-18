package com.example.Project_Online_market.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Project_Online_market.Model.EmailDetails;
import com.example.Project_Online_market.SecurityConfig.RabbitMQConfig;
import com.example.Project_Online_market.dto.EmailMessage;
import com.example.Project_Online_market.dto.OrderMessage;
import com.example.Project_Online_market.dto.PaymentMessage;

@Service
public class RabbitMQConsumer {
    
    @Autowired
    private PaymentIntegrationService paymentIntegrationService;
    
    @Autowired
    private EmailService emailService;
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void receiveOrderMessage(OrderMessage message) {
        System.out.println("Received order message: " + message);
        
        // Process the order message
        // This could trigger the payment process or other business logic
        // For example, you could call paymentIntegrationService here
    }
    
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void receivePaymentMessage(PaymentMessage message) {
        System.out.println("Received payment message: " + message);
        
        // Process the payment message
        // Update payment status, notify other services, etc.
    }
    
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void receiveEmailMessage(EmailMessage message) {
        System.out.println("Received email message for: " + message.getRecipient());
        
        // Send actual email
        EmailDetails details = new EmailDetails();
        details.setRecipient(message.getRecipient());
        details.setSubject(message.getSubject());
        details.setMsgBody(message.getContent());
        
        emailService.sendSimpleMail(details);
    }
}