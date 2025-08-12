package com.hallbooking.userservice.service.serviceimpl;

import com.hallbooking.userservice.entity.User;
import com.hallbooking.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String mobileWithCode) throws UsernameNotFoundException {
        String[] parts = mobileWithCode.split("\\|", 2);
        if (parts.length != 2) {
            throw new UsernameNotFoundException("Invalid mobile format (expected countryCode-mobile)");
        }

        String countryCode = parts[0];
        String mobile = parts[1];

        User user = userRepository.findByMobileAndCountryCode(mobile, countryCode)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile: " + mobileWithCode));

        return new org.springframework.security.core.userdetails.User(
                countryCode + "|" + user.getMobile(), // ✅ username = subject
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
