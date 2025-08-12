package com.hallbooking.admin.service.impl;

import com.hallbooking.admin.dto.LoginRequest;
import com.hallbooking.admin.dto.LoginResponse;
import com.hallbooking.admin.dto.RegisterRequest;
import com.hallbooking.admin.model.Admin;
import com.hallbooking.admin.repository.AdminRepository;
import com.hallbooking.admin.service.AdminService;
import com.hallbooking.enums.NotificationType;
import com.hallbooking.notification.service.NotificationRouter;
import com.hallbooking.notification.service.OtpService;
import com.hallbooking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private NotificationRouter notificationRouter;

    @Autowired
    private OtpService otpService;

    // ✅ Admin Login
    @Override
    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!encoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Send login notification
        notificationRouter.dispatch(
                NotificationType.EMAIL,
                admin.getEmail(),
                "Admin Login Successful",
                "You have successfully logged in as an admin."
        );

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole(), admin.getId());


        // ✅ Include admin name in response
        return new LoginResponse(token, admin.getName());
    }

    // ✅ Admin Register
    @Override
    public LoginResponse register(RegisterRequest request) {
        if (adminRepo.findByEmail(request.getEmail()).isPresent()) {
            return new LoginResponse("Admin already exists with this email");
        }

        Admin admin = new Admin();
        admin.setName(request.getName()); // ✅ name field
        admin.setEmail(request.getEmail());
        admin.setCountryCode(request.getCountryCode());
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setPassword(encoder.encode(request.getPassword()));
        admin.setRole("ADMIN");

        adminRepo.save(admin);

        // No OTP - just welcome email
        notificationRouter.dispatch(
                NotificationType.EMAIL,
                admin.getEmail(),
                "Welcome to Hall Booking!",
                "You have successfully registered as an admin."
        );

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole(), admin.getId());
        return new LoginResponse(token, admin.getName());
    }

    // ✅ Find by Full Phone
    @Override
    public Admin findByPhoneNumber(String fullPhoneNumber) {
        if (fullPhoneNumber == null || !fullPhoneNumber.startsWith("+")) return null;

        return adminRepo.findAll().stream()
                .filter(admin -> (admin.getCountryCode() + admin.getPhoneNumber()).equals(fullPhoneNumber))
                .findFirst()
                .orElse(null);
    }

    // ✅ Update Password
    @Override
    public boolean updatePassword(String fullPhoneNumber, String newPassword) {
        Admin admin = findByPhoneNumber(fullPhoneNumber);
        if (admin == null) return false;

        admin.setPassword(encoder.encode(newPassword));
        adminRepo.save(admin);
        return true;
    }

    // ✅ Token Verification
    @Override
    public boolean verifyToken(String token) {
        return jwtUtil.validateToken(token);
    }

    // Optional: if you plan to reuse OTP generation separately
    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
