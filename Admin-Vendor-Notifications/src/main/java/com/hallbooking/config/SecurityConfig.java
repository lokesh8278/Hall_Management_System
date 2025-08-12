package com.hallbooking.config;

import com.hallbooking.filter.JwtAuthFilter;
import com.hallbooking.service.CompositeUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CompositeUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CompositeUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // ✅ Public endpoints
                        .requestMatchers(
                                "/admin/login",
                                "/admin/register",
                                "/admin/verify",
                                "/admin/forgot-password",
                                "/admin/reset-password",
                                "/admin/verify-otp",
                                "/vendor/login",
                                "/vendor/register",
                                "/vendor/verify",
                                "/vendor/verify-otp",
                                "/vendor/register-request",
                                "/vendor/forgot-password",
                                "/vendor/reset-password",
                                "/mail/test",
                                "/notification/**",
                                "/api/notify/**",
                                "/notification/firebase-messaging-sw.js",
                                "/fcm-test.html",
                                "/static/**",
                                "/webjars/**",
                                "/api/auth/**"
                        ).permitAll()

                        // ✅ Admin-only
                        .requestMatchers("/admin/**", "/vendor/admin/**").hasRole("ADMIN")

                        // ✅ Vendor roles
                        .requestMatchers("/vendor/halls/**").hasRole("HALL_VENDOR")
                        .requestMatchers("/vendor/photos/**").hasRole("PHOTOGRAPHER")
                        .requestMatchers("/vendor/catering/**").hasRole("CATERING")
                        .requestMatchers("/vendor/decor/**").hasRole("DECORATOR")
                        .requestMatchers("/vendor/mehandi/**").hasRole("MEHANDI_ARTIST")

                        // ✅ General fallback
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // ✅ CORS configuration allowing frontend origin
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow specific origin where frontend runs
        config.setAllowedOriginPatterns(List.of("http://localhost:5173")); // ✅ Vite default
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // ✅ Must come after allowedOriginPatterns

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }




    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // 🔐 BCrypt for password hashing
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
