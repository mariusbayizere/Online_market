package com.example.Project_Online_market.Model;

import java.util.Map;

public class PaymentRequest {
    private String amount;
    private String currency;
    private String email;
    private String phoneNumber;
    private String paymentMethod;
    private Map<String, String> customer;  // Nested customer information
    private Map<String, String> mobileMoney;  // For mobile money details (optional)
    
    // Getters and Setters
    public String getAmount() {
        return amount;
    }
    
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Map<String, String> getCustomer() {
        return customer;
    }

    public void setCustomer(Map<String, String> customer) {
        this.customer = customer;
    }

    public Map<String, String> getMobileMoney() {
        return mobileMoney;
    }

    public void setMobileMoney(Map<String, String> mobileMoney) {
        this.mobileMoney = mobileMoney;
    }
}
