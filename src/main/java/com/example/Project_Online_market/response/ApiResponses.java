package com.example.Project_Online_market.response;

public class ApiResponses {
    private String message;
    private int statusCode;
    private Object data;

    // Constructor for success response with data
    public ApiResponses(String message, int statusCode, Object data) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }

    // Constructor for error response without data
    public ApiResponses(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = null;  // No data for error responses
    }

    // Getter and setter methods for message, statusCode, and data

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // Method to create a successful response
    public static ApiResponses success(String message, int statusCode, Object data) {
        return new ApiResponses(message, statusCode, data);
    }

    // Method to create an error response
    public static ApiResponses error(String message, int statusCode) {
        return new ApiResponses(message, statusCode);
    }
}

