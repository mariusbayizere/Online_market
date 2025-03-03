package com.example.Project_Online_market.Enum;

public enum UserRole {
    ADMIN("Admin - Full Access"),
    SHOPPER("Shopper - Can browse, buy, and leave reviews"),
    BUYER("Buyer - Can only place orders and make purchases");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

