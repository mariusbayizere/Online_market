package com.example.Project_Online_market.dto;

import java.io.Serializable;

public class OrderMessage implements Serializable {
    private int orderId;
    private int userId;
    private String productId;
    private int quantity;
    private String status;
    
    // Default constructor required for Jackson
    public OrderMessage() {}
    
    public OrderMessage(int orderId, int userId, String productId, int quantity, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
    }
    
    // Getters and setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "OrderMessage [orderId=" + orderId + ", userId=" + userId + ", productId=" + productId
                + ", quantity=" + quantity + ", status=" + status + "]";
    }
}
