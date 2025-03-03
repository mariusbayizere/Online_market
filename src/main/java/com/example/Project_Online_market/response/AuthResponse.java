package com.example.Project_Online_market.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
// @NoArgsConstructor
public class AuthResponse {
    private String jwt;
    private String message;
    private Boolean status;
    private String role;

    public AuthResponse(boolean status, String message, String jwt) {
        this.status = status;
        this.message = message;
        this.jwt = jwt;
    }

    public AuthResponse() {}

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    } 

    public void setRole(String role) {
        this.role = role;
    } 

}
