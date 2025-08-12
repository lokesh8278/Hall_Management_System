package com.hallbooking.admin.dto;

public class LoginResponse {
    private String token;
    private String name;

    public LoginResponse(String token, String name) {
        this.token = token;
        this.name = name;
    }
    public LoginResponse(String message) {
        this.token = message;
        this.name = null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
