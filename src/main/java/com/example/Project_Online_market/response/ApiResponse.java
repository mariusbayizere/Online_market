package com.example.Project_Online_market.response;

import lombok.Data; 

@Data
public class ApiResponse {
    private String message;
    private boolean status;

    public ApiResponse(String string, boolean b) {
       
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
