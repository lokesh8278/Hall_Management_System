package com.hallbooking.vendor.dto;

import java.util.List;

public class RegisterRequest {
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
    private String countryCode;
    private String vendorType;
    private List<String> features;

    // === Getters and Setters ===

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getVendorType() {
        return vendorType;
    }
    public void setVendorType(String vendorType) {
        this.vendorType = vendorType;
    }

    public List<String> getFeatures() {
        return features;
    }
    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
