// package com.example.Project_Online_market.service;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// @Service
// public class PaymentService {

//     @Value("${flutterwave.api.key}")
//     private String flutterwaveApiKey;

//     private final String FLW_PAYMENT_URL = "https://api.flutterwave.com/v3/charges";

//     public String initiatePayment(String amount, String currency, String email, String phoneNumber, String paymentMethod) {
//         RestTemplate restTemplate = new RestTemplate();

//         // Prepare headers
//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);
//         headers.set("Authorization", "Bearer " + flutterwaveApiKey);

//         // Validate & map payment method
//         String validPaymentMethod = mapPaymentMethod(paymentMethod);

//         // Request body
//         Map<String, Object> requestBody = new HashMap<>();
//         requestBody.put("tx_ref", "TXN_" + System.currentTimeMillis());
//         requestBody.put("amount", amount);
//         requestBody.put("currency", currency);
//         requestBody.put("payment_options", validPaymentMethod);  // ✅ Use correct field
//         requestBody.put("redirect_url", "https://your-redirect-url.com");
//         requestBody.put("email", email);

//         // Customer details
//         Map<String, Object> customer = new HashMap<>();
//         customer.put("email", email);
//         customer.put("phonenumber", phoneNumber);
//         customer.put("name", "Test User");
//         requestBody.put("customer", customer);

//         // Handle Mobile Money extra fields
//         if ("mobilemoneyrw".equals(validPaymentMethod)) {
//             handleMobileMoney(requestBody, phoneNumber);
//         }

//         // Send request
//         HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//         try {
//             ResponseEntity<String> response = restTemplate.postForEntity(FLW_PAYMENT_URL, request, String.class);
//             return response.getBody();
//         } catch (Exception e) {
//             return "Error: " + e.getMessage();
//         }
//     }

//     // ✅ Correctly handle Mobile Money (MTN MoMo)
//     private void handleMobileMoney(Map<String, Object> requestBody, String phoneNumber) {
//         Map<String, String> mobileMoneyDetails = new HashMap<>();
//         mobileMoneyDetails.put("provider", "mtn");  // ✅ Correct provider for Rwanda
//         mobileMoneyDetails.put("country", "RW");  // ✅ Ensure country is "RW"
//         mobileMoneyDetails.put("phone_number", phoneNumber);  // ✅ Include phone number

//         requestBody.put("mobile_money", mobileMoneyDetails);
//     }

//     private String mapPaymentMethod(String paymentMethod) {
//         switch (paymentMethod.toLowerCase()) {
//             case "card":
//                 return "card";  // Credit Card payment
//             case "mobilemoney":
//             case "mobilemoneyrw":
//             case "mtn_momo":
//                 return "mobilemoneyrw";  // MTN MoMo Rwanda payment
//             default:
//                 throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
//         }
//     }    
// }


package com.example.Project_Online_market.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {
    
    // private final String FLW_SECRET_KEY = "FLWSECK_TEST-3bd8451c8f2c33ab9422955ec4e296f5-X"; // Store securely!
    private final String FLW_SECRET_KEY = "FLWSECK_TEST-3bd8451c8f2c33ab9422955ec4e296f5-X"; 
    private final String FLW_PAYMENT_URL = "https://api.flutterwave.com/v3/payments"; // Correct endpoint

    public String initiatePayment(String amount, String currency, String email, String phoneNumber, String paymentMethod) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + FLW_SECRET_KEY);

        // Validate & map payment method
        String validPaymentMethod = mapPaymentMethod(paymentMethod);

        // Create request body following Flutterwave format
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tx_ref", "TXN_" + System.currentTimeMillis());
        requestBody.put("amount", amount);
        requestBody.put("currency", currency);
        requestBody.put("payment_type", validPaymentMethod);
        requestBody.put("redirect_url", "https://your-redirect-url.com");

        // Customer details
        Map<String, Object> customer = new HashMap<>();
        customer.put("email", email);
        customer.put("phonenumber", phoneNumber);
        customer.put("name", "John Doe"); // Replace with real user data
        requestBody.put("customer", customer);

        // Mobile money-specific fields
        if ("mobilemoneyrw".equals(validPaymentMethod)) {
            handleMobileMoney(requestBody, phoneNumber);
        }

        // Send request
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(FLW_PAYMENT_URL, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace(); // Proper logging
            return "Payment processing failed. Please try again.";
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

    private void handleMobileMoney(Map<String, Object> requestBody, String phoneNumber) {
        Map<String, Object> mobileMoneyData = new HashMap<>();
        mobileMoneyData.put("phone_number", phoneNumber);
        requestBody.put("meta", mobileMoneyData);
    }
}
