package com.hallbooking.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN;
    @Override
    public String getAuthority() {
        return "ROLE_" + name(); // ✅ Spring expects roles like ROLE_ADMIN
    }

}
