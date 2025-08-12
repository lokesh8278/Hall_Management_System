package com.hallbooking.service;

import com.hallbooking.admin.model.Admin;
import com.hallbooking.admin.repository.AdminRepository;
import com.hallbooking.vendor.model.Vendor;
import com.hallbooking.vendor.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CompositeUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private VendorRepository vendorRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByEmail(email).orElse(null);
        if (admin != null) return admin;

        Vendor vendor = vendorRepo.findByEmail(email).orElse(null);
        if (vendor != null) return vendor;

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}