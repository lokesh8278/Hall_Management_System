package com.hallbooking.userservice.dto.response;


public class LoginResponse {
    private String token;
    private String name;
    private String role;
    private Long userId;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LoginResponse() {
    }

    public LoginResponse(String token, String name, String role,Long userId ) {
        this.token = token;
        this.name = name;
        this.role = role;
        this.userId=userId;
    }
}
