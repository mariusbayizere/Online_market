package com.example.Project_Online_market.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.Project_Online_market.Enum.PaymentStatus;
import com.example.Project_Online_market.Model.Orders;
import com.example.Project_Online_market.Model.Payment;
import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.OrdersRepository;
import com.example.Project_Online_market.Repository.PaymentRepository;
import com.example.Project_Online_market.dto.EmailMessage;
import com.example.Project_Online_market.dto.PaymentMessage;

@Service
public class PaymentIntegrationService {
    
    private final String FLW_SECRET_KEY = "FLWSECK_TEST-3bd8451c8f2c33ab9422955ec4e296f5-X"; 
    private final String FLW_PAYMENT_URL = "https://api.flutterwave.com/v3/payments";
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Transactional
    public Map<String, Object> initiatePaymentForOrder(int orderId, String email, String phoneNumber, String paymentMethod) {
        Map<String, Object> response = new HashMap<>();
        
        // Find the order
        Optional<Orders> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            response.put("status", "error");
            response.put("message", "Order not found");
            return response;
        }
        
        Orders order = orderOptional.get();
        
        // Check if a payment already exists for this order
        Optional<Payment> existingPayment = paymentRepository.findByOrder(order);
        if (existingPayment.isPresent() && existingPayment.get().getPayment_Status() == PaymentStatus.COMPLETED) {
            response.put("status", "error");
            response.put("message", "Payment already completed for this order");
            return response;
        }
        
        // Calculate order amount
        double amount = order.getProduct().getProduct_Price() * order.getQuantity();
        
        // Create or update payment record
        Payment payment;
        if (existingPayment.isPresent()) {
            payment = existingPayment.get();
        } else {
            payment = new Payment();
            payment.setOrder(order);
        }
        
        payment.setPayment_Method(paymentMethod);
        payment.setPayment_Status(PaymentStatus.COMPLETED);
        payment.setPayment_Date(new Date());
        payment.setPayment_Amount(amount);
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // Send payment message via RabbitMQ
        PaymentMessage paymentMessage = new PaymentMessage(
            savedPayment.getPayment_ID(),
            order.getOrder_ID(),
            paymentMethod,
            savedPayment.getPayment_Status().toString(),
            savedPayment.getPayment_Date(),
            savedPayment.getPayment_Amount()
        );
        rabbitMQSender.sendPaymentMessage(paymentMessage);
    
        // Declare jsonResponse before the try block
        JSONObject jsonResponse = null;
    
        try {
            // Initiate Flutterwave payment
            String flutterwaveResponse = initiateFlutterwavePayment(
                String.valueOf(amount), 
                "RWF", // Currency
                email,
                phoneNumber,
                paymentMethod,
                String.valueOf(savedPayment.getPayment_ID()) // Payment ID as reference
            );
    
            jsonResponse = new JSONObject(flutterwaveResponse);
            
            if (jsonResponse.getString("status").equals("success")) {
                JSONObject data = jsonResponse.getJSONObject("data");
                String paymentLink = data.getString("link");
    
                // Update order status
                order.setOrder_Status("Shipped");
                orderRepository.save(order);
    
                sendPaymentConfirmationEmail(order, savedPayment);
                
                response.put("status", "success");
                response.put("message", "Payment initiated successfully");
                response.put("paymentId", savedPayment.getPayment_ID());
                response.put("paymentLink", paymentLink);
    
                // Send email via RabbitMQ instead of direct call
                EmailMessage emailMessage = createPaymentConfirmationEmailMessage(order, savedPayment);
                rabbitMQSender.sendEmailMessage(emailMessage);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to initiate payment with provider");
                response.put("providerResponse", flutterwaveResponse);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error processing payment: " + e.getMessage());
        }
    
        return response;
    }


private EmailMessage createPaymentConfirmationEmailMessage(Orders order, Payment payment) {
    Users user = order.getUser();
    Products product = order.getProduct();
    
    String subject = "Payment Confirmation - Order #" + order.getOrder_ID();
    
    String content = "Dear " + user.getFirst_Name() + ",\n\n" +
        "Thank you for your payment! Your order has been successfully processed and is now being shipped.\n\n" +
        "Order Details:\n" +
        "- Order ID: " + order.getOrder_ID() + "\n" +
        "- Product: " + product.getProduct_Name() + "\n" +
        "- Quantity: " + order.getQuantity() + "\n" +
        "- Total Amount: " + payment.getPayment_Amount() + " RWF\n" +
        "- Payment Method: " + payment.getPayment_Method() + "\n" +
        "- Order Status: " + order.getOrder_Status() + "\n\n" +
        "We will notify you once your order has been delivered.\n\n" +
        "Thank you for shopping with us!\n\n" +
        "Best regards,\n" +
        "Online Market Team";
    
    return new EmailMessage(user.getEmail(), subject, content);
}


    
    /**
     * Handles the webhook/callback from Flutterwave to update payment status
     */
    @Transactional
    public void handlePaymentCallback(String transactionReference, String status) {
        // The transaction reference should contain your payment ID
        try {
            int paymentId = Integer.parseInt(transactionReference);
            Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
            
            if (paymentOptional.isPresent()) {
                Payment payment = paymentOptional.get();
                
                if ("successful".equalsIgnoreCase(status)) {
                    payment.setPayment_Status(PaymentStatus.COMPLETED);
                    
                    // Update the order status as well
                    Orders order = payment.getOrder();
                    order.setOrder_Status("Paid");
                    orderRepository.save(order);
                } else {
                    payment.setPayment_Status(PaymentStatus.FAILED);
                }
                
                paymentRepository.save(payment);
            }
        } catch (NumberFormatException e) {
            // Handle invalid reference format
            System.err.println("Invalid payment reference: " + transactionReference);
        }
    }

    private void sendPaymentConfirmationEmail(Orders order, Payment payment) {
        try {
            EmailMessage emailMessage = createPaymentConfirmationEmailMessage(order, payment);
            rabbitMQSender.sendEmailMessage(emailMessage);
        } catch (Exception e) {
            System.err.println("Error queuing payment confirmation email: " + e.getMessage());
        }
    }
    
    /**
     * Verify payment status directly with Flutterwave
     */
    public boolean verifyPayment(String transactionId) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + FLW_SECRET_KEY);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                "https://api.flutterwave.com/v3/transactions/" + transactionId + "/verify",
                String.class,
                entity
            );
            
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.getString("status").equals("success")) {
                JSONObject data = jsonResponse.getJSONObject("data");
                return "successful".equalsIgnoreCase(data.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Helper method to initiate Flutterwave payment
     */
    private String initiateFlutterwavePayment(String amount, String currency, String email, 
                                             String phoneNumber, String paymentMethod, String reference) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + FLW_SECRET_KEY);

        // Validate & map payment method
        String validPaymentMethod = mapPaymentMethod(paymentMethod);

        // Create request body following Flutterwave format
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tx_ref", reference);
        requestBody.put("amount", amount);
        requestBody.put("currency", currency);
        requestBody.put("payment_type", validPaymentMethod);
        requestBody.put("redirect_url", "https://your-domain.com/api/payment/callback"); // You'll need to implement this endpoint

        // Customer details
        Map<String, Object> customer = new HashMap<>();
        customer.put("email", email);
        customer.put("phonenumber", phoneNumber);
        customer.put("name", "Customer"); // Ideally, get this from user details
        requestBody.put("customer", customer);

        // Mobile money-specific fields
        if ("mobilemoneyrw".equals(validPaymentMethod)) {
            Map<String, Object> mobileMoneyData = new HashMap<>();
            mobileMoneyData.put("phone_number", phoneNumber);
            requestBody.put("meta", mobileMoneyData);
        }

        // Send request
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(FLW_PAYMENT_URL, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    private String mapPaymentMethod(String method) {
        switch (method.toLowerCase()) {
            case "card":
                return "card";
            case "bank_transfer":
                return "bank_transfer";
            case "mobilemoneyrw":
                return "mobilemoneyrw"; // Rwanda Mobile Money
            default:
                return "card"; // Default to card payments
        }
    }
}