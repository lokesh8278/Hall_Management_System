package com.hallbooking.vendor.dto;

import java.util.List;

public class VendorResponse {

    private Long id;
    private String name;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private List<String> features;

    public VendorResponse() { }

    public VendorResponse(String name,
                          String email,
                          String countryCode,
                          String phoneNumber,
                          List<String> features,
                          Long id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.features = features;
    }

    // === Getters & Setters ===
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String code) { this.countryCode = code; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String pn) { this.phoneNumber = pn; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
