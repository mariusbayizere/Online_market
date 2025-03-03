package com.example.Project_Online_market.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Online_market.Repository.OrdersRepository;
import com.example.Project_Online_market.service.PaymentIntegrationService;

@RestController
@RequestMapping("/api1/payment-integration")
public class PaymentIntegrationController {

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;
    
    @Autowired
    private OrdersRepository orderRepository;
    
    /**
     * Initiate payment for an order with intergrating Database
     */
    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<Map<String, Object>> initiatePayment(
            @PathVariable int orderId,
            @RequestBody Map<String, String> paymentDetails) {
        
        String email = paymentDetails.get("email");
        String phoneNumber = paymentDetails.get("phoneNumber");
        String paymentMethod = paymentDetails.get("paymentMethod");
        
        // Validation
        if (email == null || phoneNumber == null || paymentMethod == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Missing required fields: email, phoneNumber, or paymentMethod");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> result = paymentIntegrationService.initiatePaymentForOrder(
            orderId, email, phoneNumber, paymentMethod
        );
        
        if ("error".equals(result.get("status"))) {
            return ResponseEntity.badRequest().body(result);
        }
        
        return ResponseEntity.ok(result);
    }
}
 