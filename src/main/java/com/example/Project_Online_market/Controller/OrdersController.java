
package com.example.Project_Online_market.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Online_market.Model.EmailDetails;
import com.example.Project_Online_market.Model.Orders;
import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.OrdersRepository;
import com.example.Project_Online_market.Repository.ProductsRepository;
import com.example.Project_Online_market.Repository.UsersRopository;
import com.example.Project_Online_market.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('SHOPPER') or hasRole('ADMIN')")
@Tag(name = "Orders Management", description = "APIs for managing customer orders")
@SecurityRequirement(name = "bearerAuth")
public class OrdersController {
    private final OrdersRepository ordersRepository;
    private final UsersRopository usersRepository;
    private final ProductsRepository productsRepository;
    

    public OrdersController(OrdersRepository ordersRepository, UsersRopository usersRepository, ProductsRepository productsRepository) {
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.productsRepository = productsRepository;
    }

    @Autowired
    private EmailService emailService; 

    /**
     * Retrieves all orders history from Server.
     * 
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('SHOPPER') or hasRole('ADMIN')")
    @Operation(summary = "Get all orders history", description = "Retrieves the complete order history")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                 content = @Content(schema = @Schema(implementation = Orders.class))),
        @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(ordersRepository.findAll());
    }

    /**
     * Retrieves an order by its ID.
     * 
     * @param id The ID of the order to be retrieved based on ID.
     * @return ResponseEntity<ApiResponses> JSON response with message, status code, and order data.
     */
    @GetMapping("/track/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<?> getOrderById(
            @Parameter(description = "ID of the order to retrieve") @PathVariable int id) {
        Optional<Orders> orderOpt = ordersRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body(com.example.Project_Online_market.response.ApiResponses.error("Order not found", 404));
        }

        return ResponseEntity.ok(com.example.Project_Online_market.response.ApiResponses.success("Order retrieved successfully", 200, orderOpt.get()));
    }

    /**
     * Handles the addition of a new order.
     * 
     * This method validates the user and product, checks stock availability, 
     * prevents duplicate orders, and processes the order if all conditions are met.
     * 
     * @param order The order details received from the request body.
     * @return ResponseEntity<?> JSON response with a message and status code.
     * @throws IllegalArgumentException if order details are invalid.
     */
    @PostMapping("/add")
    @Operation(summary = "Add a new order", description = "Creates a new order after validating user, product, and stock availability")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation"),
        @ApiResponse(responseCode = "404", description = "User or product not found")
    })
    public ResponseEntity<?> addOrder(
            @Parameter(description = "Order details", required = true) @RequestBody Orders order) {
        Optional<Users> userOpt = usersRepository.findById(order.getUser().getUser_ID());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new com.example.Project_Online_market.response.ApiResponses("User not found", 404));
        }
        order.setUser(userOpt.get());

        Optional<Products> productOpt = productsRepository.findById(order.getProduct().getProduct_ID());
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new com.example.Project_Online_market.response.ApiResponses("Product not found", 404));
        }
        Products product = productOpt.get();
        order.setProduct(product);

        if (product.getProduct_Quantity() == 0) {
            return ResponseEntity.status(400).body(new com.example.Project_Online_market.response.ApiResponses(
                "The product is out of stock. Please choose a different product or check back later.", 400));
        }

        if (order.getQuantity() > product.getProduct_Quantity()) {
            return ResponseEntity.status(400).body(new com.example.Project_Online_market.response.ApiResponses(
                "You are ordering a quantity greater than the available stock. Please reduce the quantity.", 400));
        }

        Optional<Orders> existingOrderOpt = ordersRepository.findByUserAndProduct(order.getUser(), product);
        if (existingOrderOpt.isPresent()) {
            return ResponseEntity.status(400).body(new com.example.Project_Online_market.response.ApiResponses(
                "You have already ordered this product. Please try ordering a different product.", 400));
        }

        product.setProduct_Quantity(product.getProduct_Quantity() - order.getQuantity());
        productsRepository.save(product);

        // Save order
        Orders savedOrder = ordersRepository.save(order);

        // Send confirmation email
        try {
            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setRecipient(order.getUser().getEmail());
            emailDetails.setSubject("Order Confirmation");
            emailDetails.setMsgBody("Dear " + order.getUser().getFirst_Name() + ",\n\n" +
                    "Your order for " + order.getQuantity() + " units of " + product.getProduct_Name() + " has been successfully placed.\n" +
                    "Order ID: " + savedOrder.getOrder_ID() + "\n" +
                    "Status: " + savedOrder.getOrder_Status() + "\n\n" +
                    "Thank you for shopping with us!\n\nBest regards,\nOnline Market Team");

            emailService.sendSimpleMail(emailDetails);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

        return ResponseEntity.status(201).body(new com.example.Project_Online_market.response.ApiResponses(
                "Order successfully created and confirmation email sent", 201, savedOrder));
    }



    /**
     * Updates an existing order by ID.
     * 
     * @param id The ID of the order to be updated.
     * @param updatedOrder The new order details received in the request body.
     * @return ResponseEntity<?> JSON response with message, status code, and updated order data.
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "Update an order", description = "Updates an existing order with new details")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order updated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<?> updateOrder(
            @Parameter(description = "ID of the order to update") @PathVariable int id, 
            @Parameter(description = "Updated order details", required = true) @RequestBody Orders updatedOrder) {
        Optional<Orders> existingOrderOpt = ordersRepository.findById(id);
        if (existingOrderOpt.isEmpty()) {
            return ResponseEntity.status(404).body(com.example.Project_Online_market.response.ApiResponses.error("Order not found", 404));
        }

        Orders existingOrder = existingOrderOpt.get();
        existingOrder.setOrder_Date(updatedOrder.getOrder_Date());
        existingOrder.setOrder_Status(updatedOrder.getOrder_Status());
        existingOrder.setQuantity(updatedOrder.getQuantity());

        Orders savedOrder = ordersRepository.save(existingOrder);
        return ResponseEntity.ok(com.example.Project_Online_market.response.ApiResponses.success("Order updated successfully", 200, savedOrder));
    }

    /**
     * Deletes an order by ID.
     * 
     * @param id The ID of the order to be deleted.
     * @return ResponseEntity<?> JSON response with message and status code.
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete an order", description = "Removes an order from the system")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<?> deleteOrder(
            @Parameter(description = "ID of the order to delete") @PathVariable int id) {
        if (!ordersRepository.existsById(id)) {
            return ResponseEntity.status(404).body(com.example.Project_Online_market.response.ApiResponses.error("Order not found", 404));
        }

        ordersRepository.deleteById(id);
        return ResponseEntity.ok(com.example.Project_Online_market.response.ApiResponses.success("Order deleted successfully", 200, null));
    }

    /**
     * Tracks the status of an order.
     * 
     * @param id The ID of the order to track.
     * @return ResponseEntity<ApiResponses> JSON response with the order's status.
     */
    @GetMapping("/oderbyuser/{userId}")
    @Operation(summary = "Get orders by user", description = "Retrieves all orders placed by a specific user")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getOrdersByUser(
            @Parameter(description = "ID of the user whose orders to retrieve") @PathVariable int userId) {
        Optional<Users> userOpt = usersRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new com.example.Project_Online_market.response.ApiResponses("User not found", 404));
        }

        List<Orders> orders = ordersRepository.findByUser(userOpt.get());
        return ResponseEntity.ok(com.example.Project_Online_market.response.ApiResponses.success("Orders retrieved successfully", 200, orders));
    }
}