package com.example.Project_Online_market.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Online_market.Model.Payment;
import com.example.Project_Online_market.Repository.OrdersRepository;
import com.example.Project_Online_market.Repository.PaymentRepository;
import com.example.Project_Online_market.service.PaymentIntegrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for managing payment operations.
 * Provides endpoints for payment creation, retrieval, updates and deletion.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "APIs for managing payment operations")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final OrdersRepository orderRepository;
    
    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    /**
     * Constructor for PaymentController.
     * 
     * @param paymentRepository repository for payment operations
     * @param orderRepository repository for order operations
     */
    public PaymentController(PaymentRepository paymentRepository, OrdersRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Initiate payment for an order with integrating Database.
     * 
     * @param orderId ID of the order to process payment for
     * @param paymentDetails Map containing payment details (email, phoneNumber, paymentMethod)
     * @return ResponseEntity with payment processing result
     */
    @Operation(
        summary = "Initiate payment for an order", 
        description = "Process a payment request for a specific order"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment initiated successfully",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or payment processing error",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<Map<String, Object>> initiatePayment(
            @Parameter(description = "ID of the order to process payment for", required = true)
            @PathVariable int orderId,
            
            @Parameter(description = "Payment details including email, phoneNumber, and paymentMethod", required = true)
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

    /**
     * Get all payments.
     * 
     * @return ResponseEntity with list of all payments
     */
    @Operation(summary = "Get all payments", description = "Retrieves a list of all payments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all payments",
        content = @Content(mediaType = "application/json", 
        array = @ArraySchema(schema = @Schema(implementation = Payment.class))))
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    /**
     * Get payment by ID.
     * 
     * @param id ID of the payment to retrieve
     * @return ResponseEntity with the payment
     * @throws RuntimeException if payment not found
     */
    @Operation(summary = "Get payment by ID", description = "Retrieves a specific payment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the payment",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Payment.class))),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(
            @Parameter(description = "ID of the payment to retrieve", required = true)
            @PathVariable int id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return ResponseEntity.ok(payment);
    }
    /**
     * Update an existing payment.
     * 
     * @param id ID of the payment to update
     * @param updatedPayment updated payment details
     * @return ResponseEntity with the updated payment
     * @throws RuntimeException if payment not found
     */
    @Operation(summary = "Update a payment", description = "Updates a payment based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Payment.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePayment(
            @Parameter(description = "ID of the payment to update", required = true)
            @PathVariable int id, 
            @Parameter(description = "Updated payment details", required = true)
            @Valid @RequestBody Payment updatedPayment) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        existingPayment.setPayment_Method(updatedPayment.getPayment_Method());
        existingPayment.setPayment_Status(updatedPayment.getPayment_Status());
        existingPayment.setPayment_Date(updatedPayment.getPayment_Date());
        existingPayment.setPayment_Amount(updatedPayment.getPayment_Amount());

        Payment savedPayment = paymentRepository.save(existingPayment);
        return ResponseEntity.ok(savedPayment);
    }

    /**
     * Delete a payment.
     * 
     * @param id ID of the payment to delete
     * @return ResponseEntity with a success message
     * @throws RuntimeException if payment not found
     */
    @Operation(summary = "Delete a payment", description = "Deletes a payment based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePayment(
            @Parameter(description = "ID of the payment to delete", required = true)
            @PathVariable int id) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        paymentRepository.delete(existingPayment);
        return ResponseEntity.ok("Payment deleted successfully");
    }
}