// package com.example.Project_Online_market.Controller;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.Project_Online_market.Model.PaymentRequest;
// import com.example.Project_Online_market.service.PaymentService;

// @RestController
// @RequestMapping("/apis/payment")
// public class PaymentControllerapi {

//     private final PaymentService paymentService;

//     public PaymentControllerapi(PaymentService paymentService) {
//         this.paymentService = paymentService;
//     }

//     @PostMapping("/initiate")
//     public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest requestData) {
//         if (requestData.getAmount() == null || requestData.getCurrency() == null ||
//             requestData.getEmail() == null || requestData.getPhoneNumber() == null ||
//             requestData.getPaymentMethod() == null) {
//             return ResponseEntity.badRequest().body("Missing required fields");
//         }

//         String response = paymentService.initiatePayment(
//             requestData.getAmount(), 
//             requestData.getCurrency(), 
//             requestData.getEmail(), 
//             requestData.getPhoneNumber(), 
//             requestData.getPaymentMethod()
//         );

//         return ResponseEntity.ok(response);
//     }
// }
