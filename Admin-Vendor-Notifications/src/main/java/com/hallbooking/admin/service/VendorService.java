package com.hallbooking.admin.service;

import com.hallbooking.admin.dto.PendingVendorDTO;
import com.hallbooking.admin.dto.VendorResponse;
import com.hallbooking.enums.NotificationType;
import com.hallbooking.enums.VendorStatus;
import com.hallbooking.notification.service.NotificationRouter;
import com.hallbooking.vendor.model.PendingVendorRequest;
import com.hallbooking.vendor.model.Vendor;
import com.hallbooking.vendor.repository.PendingVendorRepository;
import com.hallbooking.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.hallbooking.enums.VendorRole;



@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final PendingVendorRepository pendingVendorRepository;
    private final NotificationRouter notificationRouter;
    // ✅ Injected



    // ✅ Get all pending vendor requests
    // ✅ Get all pending vendor requests
    public List<PendingVendorDTO> getPendingVendors() {
        return pendingVendorRepository.findAll()
                .stream()
                .map(p -> new PendingVendorDTO(
                        p.getId(),
                        p.getName(),
                        p.getEmail(),
                        p.getCountryCode(),
                        p.getPhoneNumber()  // ✅ MATCHES DTO NOW
                ))
                .toList();
    }


    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll()
                .stream()
                .map(v -> new VendorResponse(
                        v.getId(),
                        v.getName(),
                        v.getEmail(),
                        v.getCountryCode() + " " + v.getPhoneNumber(),
                        v.getStatus() != null ? v.getStatus().name() : "UNKNOWN" // matches `status` field
                                  // matches `phoneNumber` field
                ))
                .toList();
    }



    // ✅ Approve a pending vendor
    public String approveVendor(Long id) {
        System.out.println("🔍 Approving vendor with ID: " + id);

        PendingVendorRequest pending = pendingVendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Pending vendor not found for ID: " + id));

        Vendor v = new Vendor();
        v.setName(pending.getName());
        v.setEmail(pending.getEmail());
        v.setPassword(pending.getPassword());
        v.setPhoneNumber(pending.getPhoneNumber());
        v.setCountryCode(pending.getCountryCode());

        // ✅ Convert stored string role into VendorRole enum
        try {
            v.setRole(VendorRole.valueOf(pending.getRole())); // ✅ string to enum
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("❌ Invalid role value in pending request: " + pending.getRole());
        }

        v.setStatus(VendorStatus.APPROVED);
        v.setFeatures(pending.getFeatures());

        vendorRepository.save(v);
        pendingVendorRepository.deleteById(id);

        notificationRouter.dispatch(
                NotificationType.EMAIL,
                v.getEmail(),
                "Vendor Approved",
                "Hi " + v.getName() + ",\n\nYour vendor registration request has been approved! You can now log in to your vendor account.\n\nThank you!"
        );

        System.out.println("✅ Vendor approved and moved to active list: " + v.getEmail());
        return "✅ Vendor approved and moved to active list.";
    }

    // ✅ Reject = Delete from pending list
    public String rejectVendor(Long id) {
        System.out.println("🚫 Rejecting vendor with ID: " + id);

        PendingVendorRequest pending = pendingVendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Pending vendor not found for ID: " + id));

        // ✅ Send rejection email
        notificationRouter.dispatch(
                NotificationType.EMAIL,
                pending.getEmail(),
                "Vendor Registration Rejected",
                "Dear " + pending.getName() + ",\n\nWe regret to inform you that your vendor registration has been rejected by the admin.\n\nRegards,\nTeam"
        );

        pendingVendorRepository.deleteById(id);
        System.out.println("🗑️ Vendor request rejected and removed.");
        return "❌ Vendor request rejected and removed.";
    }
}
