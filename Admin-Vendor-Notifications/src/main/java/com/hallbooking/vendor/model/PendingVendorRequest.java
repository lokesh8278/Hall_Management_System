package com.hallbooking.vendor.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pending_vendor_requests")
public class PendingVendorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String countryCode; // ✅ Added field

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String role;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "pending_vendor_features",
            joinColumns = @JoinColumn(name = "pending_vendor_id")
    )
    @Column(name = "feature")
    private List<String> features;

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() { // ✅ Getter
        return countryCode;
    }

    public void setCountryCode(String countryCode) { // ✅ Setter
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
