package com.hallbooking.vendor.service.impl;

import com.hallbooking.enums.NotificationType;
import com.hallbooking.enums.VendorStatus;
import com.hallbooking.notification.service.NotificationRouter;
import com.hallbooking.notification.service.OtpService;
import com.hallbooking.util.JwtUtil;
import com.hallbooking.vendor.dto.*;
import com.hallbooking.vendor.model.PendingVendorRequest;
import com.hallbooking.vendor.model.Vendor;
import com.hallbooking.vendor.repository.PendingVendorRepository;
import com.hallbooking.vendor.repository.VendorRepository;
import com.hallbooking.vendor.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.hallbooking.enums.VendorRole;
import org.springframework.transaction.annotation.Transactional;


@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorRepository vendorRepo;

    @Autowired
    private PendingVendorRepository pendingVendorRepo;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private NotificationRouter notificationRouter;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailAsyncService emailAsyncService;


    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        long start = System.currentTimeMillis();

        Vendor vendor = vendorRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!encoder.matches(request.getPassword(), vendor.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(vendor.getEmail(), vendor.getRole().name(), vendor.getId());
        emailAsyncService.sendLoginNotification(vendor);


        long end = System.currentTimeMillis();
        System.out.println("⏱️ Vendor login completed in " + (end - start) + " ms");

        return new LoginResponse(token, vendor.getName(), vendor.getRole().name(), vendor.getId());
    }



    @Override
    public List<VendorResponse> getApprovedVendors() {
        return vendorRepo.findByStatus(VendorStatus.APPROVED)
                .stream()
                .map(v -> new VendorResponse(
                        v.getName(),
                        v.getEmail(),
                        v.getCountryCode(),
                        v.getPhoneNumber(),
                        v.getFeatures(),
                        v.getId()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public String sendOtpForPasswordReset(OtpRequest request) {
        String fullPhone = request.getCountryCode() + request.getPhoneNumber();
        Vendor vendor = vendorRepo.findAll().stream()
                .filter(v -> (v.getCountryCode() + v.getPhoneNumber()).equals(fullPhone))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vendor not found with provided phone number"));

        otpService.sendOtp(fullPhone);
        return "OTP sent to " + fullPhone;
    }

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        String fullPhone = request.getCountryCode() + request.getPhoneNumber();

        Vendor vendor = vendorRepo.findAll().stream()
                .filter(v -> (v.getCountryCode() + v.getPhoneNumber()).equals(fullPhone))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vendor not found with provided phone number"));

        vendor.setPassword(encoder.encode(request.getNewPassword()));
        vendorRepo.save(vendor);

        if (vendor.getEmail() != null) {
            notificationRouter.dispatch(
                    NotificationType.EMAIL,
                    vendor.getEmail(),
                    "Password Reset Successful",
                    "Hi " + vendor.getName() + ",\n\nYour password has been successfully reset. If you did not initiate this request, please contact our support team immediately."
            );
        }

        return "Password reset successful";
    }
    @Override
    public boolean verifyOtp(String countryCode, String phoneNumber, String otp) {
        // ✅ Normalize inputs
        countryCode = countryCode == null ? "" : countryCode.trim();
        phoneNumber = phoneNumber == null ? "" : phoneNumber.trim();
        otp = otp == null ? "" : otp.trim();

        // ✅ Ensure + prefix
        if (!countryCode.startsWith("+")) {
            countryCode = "+" + countryCode;
        }

        String fullPhone = (countryCode + phoneNumber).replaceAll("\\s+", ""); // ✅ Remove ALL spaces
        String redisKey = "otp:value:" + fullPhone;

        System.out.println("🔍 Checking OTP for key: " + redisKey + ", OTP: " + otp);

        String storedOtp = redisTemplate.opsForValue().get(redisKey);
        System.out.println("🗝️ Found stored OTP: " + storedOtp);

        return storedOtp != null && storedOtp.equals(otp);
    }


    @Override
    public VendorResponse getVendorById(Long id) {
        Vendor v = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        return new VendorResponse(
                v.getName(),
                v.getEmail(),
                v.getCountryCode(),
                v.getPhoneNumber(),
                v.getFeatures(),
                v.getId()
        );
    }


    @Override
    public String savePendingVendor(RegisterRequest request) {
        PendingVendorRequest pending = new PendingVendorRequest();
        pending.setName(request.getName());
        pending.setEmail(request.getEmail());
        pending.setPassword(encoder.encode(request.getPassword()));
        pending.setPhoneNumber(request.getPhoneNumber());
        pending.setCountryCode(request.getCountryCode());
        pending.setRole(request.getVendorType());
        pending.setFeatures(request.getFeatures());

        pendingVendorRepo.save(pending);
        return "Pending vendor saved";
    }

    @Override
    @Transactional
    public String registerRequest(RegisterRequest request) {
        boolean existsInApproved = vendorRepo.findByEmail(request.getEmail()).isPresent();
        boolean existsInPending = pendingVendorRepo.findByEmail(request.getEmail()).isPresent();

        if (existsInApproved || existsInPending) {
            notificationRouter.dispatch(NotificationType.EMAIL, request.getEmail(), "Vendor Registration", "Vendor already exists with this email");
            return "Vendor already exists or registration request is already pending.";
        }

        PendingVendorRequest pending = new PendingVendorRequest();
        pending.setName(request.getName());
        pending.setEmail(request.getEmail());
        pending.setPassword(encoder.encode(request.getPassword()));
        pending.setPhoneNumber(request.getPhoneNumber());
        pending.setCountryCode(request.getCountryCode());
        VendorRole resolvedRole = mapVendorTypeToRole(request.getVendorType());
        pending.setRole(resolvedRole.name()); // store as string
        pending.setFeatures(request.getFeatures());

        pendingVendorRepo.save(pending);
        notificationRouter.dispatch(NotificationType.EMAIL, request.getEmail(), "Vendor Registration", "Vendor registration request submitted. Awaiting admin approval.");
        return "Vendor registration request submitted. Awaiting admin approval.";
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (vendorRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Vendor already exists with this email");
        }

        Vendor v = new Vendor();
        v.setName(request.getName());
        v.setEmail(request.getEmail());
        v.setPassword(encoder.encode(request.getPassword()));

        VendorRole resolvedRole = mapVendorTypeToRole(request.getVendorType());
        v.setRole(resolvedRole);

        v.setPhoneNumber(request.getPhoneNumber());
        v.setCountryCode(request.getCountryCode());
        v.setFeatures(request.getFeatures());
        v.setStatus(VendorStatus.APPROVED);

        vendorRepo.save(v);

        String token = jwtUtil.generateToken(v.getEmail(), v.getRole().name(), v.getId());

        return new LoginResponse(token, v.getName(), v.getRole().name(), v.getId()); // ✅ updated
    }


    @Override
    public List<VendorResponse> getAllVendors() {
        return vendorRepo.findAll().stream()
                .map(v -> new VendorResponse(
                        v.getName(),
                        v.getEmail(),
                        v.getCountryCode(),
                        v.getPhoneNumber(),
                        v.getFeatures(),
                        v.getId()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateVendor(Long id, RegisterRequest request) {
        Vendor v = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        v.setName(request.getName());
        v.setEmail(request.getEmail());
        v.setPhoneNumber(request.getPhoneNumber());
        VendorRole resolvedRole = mapVendorTypeToRole(request.getVendorType());
        v.setRole(resolvedRole); // set as enum



        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            v.setPassword(encoder.encode(request.getPassword()));
        }

        v.setFeatures(request.getFeatures());
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        if (!vendorRepo.existsById(id)) {
            throw new RuntimeException("Vendor not found");
        }
        vendorRepo.deleteById(id);
    }

    @Override
    public boolean verifyToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public VerifyResponse getVerifyInfo(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }
        String email = jwtUtil.extractUsername(token);
        Vendor v = vendorRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        return new VerifyResponse(
                v.getId(),
                "Login successful",
                v.getName(),
                v.getEmail(),
                v.getCountryCode(),
                v.getPhoneNumber(),
                v.getFeatures(),
                v.getRole()
        );
    }

    @Override
    @Transactional
    public void updateFeatures(Long vendorId, UpdateFeaturesRequest request) {
        Vendor v = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        v.setFeatures(request.getFeatures());
    }

    @Override
    public List<PendingVendorDTO> getPendingVendors() {
        return pendingVendorRepo.findAll()
                .stream()
                .map(p -> new PendingVendorDTO(
                        p.getId(),
                        p.getName(),
                        p.getEmail(),
                        p.getCountryCode(),
                        p.getPhoneNumber(),
                        p.getRole(),
                        p.getFeatures()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String approveVendor(Long id) {
        System.out.println("🔍 Approving vendor with ID: " + id);

        PendingVendorRequest pending = pendingVendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pending vendor not found"));

        Vendor vendor = new Vendor();
        vendor.setName(pending.getName());
        vendor.setEmail(pending.getEmail());
        vendor.setPassword(pending.getPassword());
        vendor.setPhoneNumber(pending.getPhoneNumber());
        vendor.setCountryCode(pending.getCountryCode());
        vendor.setRole(VendorRole.valueOf(pending.getRole())); // ✅ Convert string to enum
        vendor.setFeatures(pending.getFeatures());
        vendor.setStatus(VendorStatus.APPROVED);

        vendorRepo.save(vendor);
        pendingVendorRepo.deleteById(id);

        notificationRouter.dispatch(
                NotificationType.EMAIL,
                vendor.getEmail(),
                "Vendor Approved",
                "Hi " + vendor.getName() + ",\n\nYour vendor registration request has been approved! You can now log in to your vendor account and start managing your profile and services.\n\nThank you!"
        );

        return "Vendor approved successfully.";
    }

    @Override
    @Transactional
    public String rejectVendor(Long id) {
        PendingVendorRequest pending = pendingVendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pending vendor not found"));

        notificationRouter.dispatch(NotificationType.EMAIL, pending.getEmail(), "Vendor Rejected", "Your vendor registration request was rejected by admin.");

        pendingVendorRepo.deleteById(id);
        return "Vendor registration request rejected and removed";
    }


    private VendorRole mapVendorTypeToRole(String vendorType) {
        if (vendorType == null || vendorType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor type is required");
        }

        return switch (vendorType.trim().toLowerCase()) {
            case "hall", "venue", "banquet" -> VendorRole.HALL_VENDOR;
            case "photography", "photographer" -> VendorRole.PHOTOGRAPHER;
            case "catering", "caterer" -> VendorRole.CATERING;
            case "mehandi", "mehandi artist" -> VendorRole.MEHANDI_ARTIST;
            case "decor", "decorator", "decoration" -> VendorRole.DECORATOR;
            default -> throw new IllegalArgumentException("Unknown vendor type: " + vendorType);
        };
    }

}
