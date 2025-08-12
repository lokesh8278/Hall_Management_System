package com.hallbooking.vendor.controller;

import com.hallbooking.enums.VendorRole;
import com.hallbooking.vendor.model.PendingVendorRequest;
import com.hallbooking.vendor.model.Vendor;
import com.hallbooking.vendor.repository.PendingVendorRepository;
import com.hallbooking.vendor.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor/admin")
public class AdminVendorApprovalController {

    @Autowired
    private PendingVendorRepository pendingVendorRepo;

    @Autowired
    private VendorRepository vendorRepo;

    // ✅ SECURED: Only Admin can view pending requests
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PendingVendorRequest> getPendingRequests() {
        return pendingVendorRepo.findAll();
    }

    // ✅ SECURED: Only Admin can approve
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        PendingVendorRequest pending = pendingVendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Vendor vendor = new Vendor();
        vendor.setName(pending.getName());
        vendor.setEmail(pending.getEmail());
        vendor.setPassword(pending.getPassword());
        vendor.setPhoneNumber(pending.getPhoneNumber());
        vendor.setCountryCode(pending.getCountryCode());

        try {
            // ✅ Convert stored string role into enum safely
            VendorRole resolvedRole = VendorRole.valueOf(pending.getRole());
            vendor.setRole(resolvedRole);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Invalid vendor role: " + pending.getRole());
        }

        vendor.setFeatures(pending.getFeatures());

        vendorRepo.save(vendor);
        pendingVendorRepo.deleteById(id);

        return ResponseEntity.ok("✅ Vendor approved successfully.");
    }

    // ✅ SECURED: Only Admin can reject
    @DeleteMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        if (!pendingVendorRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("❌ Vendor request not found.");
        }
        pendingVendorRepo.deleteById(id);
        return ResponseEntity.ok("❌ Vendor request rejected.");
    }
}
