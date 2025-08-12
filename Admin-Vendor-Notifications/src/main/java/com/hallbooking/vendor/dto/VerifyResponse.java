package com.hallbooking.vendor.dto;

import com.hallbooking.enums.VendorRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;

public class VerifyResponse {

    private long id;
    private String message;
    private String name;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private List<String> features;
    @Enumerated(EnumType.STRING)
    private VendorRole role;


    // === Constructors ===

    public VerifyResponse() {}

    public VerifyResponse(long id,
                          String message,
                          String name,
                          String email,
                          String countryCode,
                          String phoneNumber,
                          List<String> features,
                          VendorRole role) {
        this.id = id;
        this.message = message;
        this.name = name;
        this.email = email;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.features = features;
        this.role = role;
    }

    // === Getters & Setters ===

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getFeatures() {
        return features;
    }
    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public VendorRole getRole() { return role; }
    public void setRole(VendorRole role) { this.role = role; }

}
