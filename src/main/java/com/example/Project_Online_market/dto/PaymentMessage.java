package com.example.Project_Online_market.dto;

import java.io.Serializable;
import java.util.Date;

public class PaymentMessage implements Serializable {
    private int paymentId;
    private int orderId;
    private String paymentMethod;
    private String paymentStatus;
    private Date paymentDate;
    private double paymentAmount;
    
    // Default constructor required for Jackson
    public PaymentMessage() {}
    
    public PaymentMessage(int paymentId, int orderId, String paymentMethod, 
                         String paymentStatus, Date paymentDate, double paymentAmount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
    }
    
    // Getters and setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    
    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }
    
    @Override
    public String toString() {
        return "PaymentMessage [paymentId=" + paymentId + ", orderId=" + orderId + ", paymentMethod=" + paymentMethod
                + ", paymentStatus=" + paymentStatus + ", paymentDate=" + paymentDate 
                + ", paymentAmount=" + paymentAmount + "]";
    }
}
