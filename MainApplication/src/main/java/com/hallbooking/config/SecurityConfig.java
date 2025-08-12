package com.hallbooking.config;

import com.hallbooking.utilis.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/auth/**",
                                "/api/user/register",
                                "/api/user/login",
                                "/api/notify/**",
                                "/notification/**",
                                "/notification/firebase-messaging-sw.js",
                                "/fcm-test.html",
                                "/static/**",
                                "/webjars/**"
                        ).permitAll()

                        // ✅ Public GET access
                        .requestMatchers(HttpMethod.GET, "/api/halls/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/halls/documents/**").permitAll()

                        // ✅ ✅ Explicitly allow public POST for checking availability
                        .requestMatchers(HttpMethod.POST, "/api/halls/*/availability").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/halls/*/available-dates").permitAll()

                        // ✅ Vendor-only access
                        .requestMatchers(HttpMethod.POST, "/api/halls/**").hasAuthority("ROLE_HALL_VENDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/halls/**").hasAuthority("ROLE_HALL_VENDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/halls/**").hasAuthority("ROLE_HALL_VENDOR")

                        // ✅ Booking APIs require login
                        .requestMatchers("/booking/**").authenticated()

                        // ✅ Admin-only access
                        .requestMatchers("/api/user/all").hasRole("ADMIN")

                        // ✅ Fallback
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
