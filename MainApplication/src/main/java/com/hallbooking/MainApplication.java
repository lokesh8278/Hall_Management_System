package com.hallbooking;

import com.hallbooking.config.TwilioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.hallbooking.config",
		"com.hallbooking.utilis",
		"com.hallbooking.userservice",
		"com.hallbooking.bookingService",
		"com.hallbooking.hall_service",
		"com.hallbooking.notification",
		"com.hallbooking.payment",          // ✅ Added
		"com.hallbooking.reviewrating",           // ✅ Added
		"com.hallbooking.ticketing"         // ✅ Added
})
@EntityScan(basePackages = {
		"com.hallbooking.userservice.entity",
		"com.hallbooking.bookingService.entity",
		"com.hallbooking.hall_service.entity",
		"com.hallbooking.notification.entity",
		"com.hallbooking.payment.entity",   // ✅ Added
		"com.hallbooking.reviewrating.entity",    // ✅ Added
		"com.hallbooking.ticketing.entity"  // ✅ Added
})
@EnableJpaRepositories(basePackages = {
		"com.hallbooking.userservice.repository",
		"com.hallbooking.bookingService.repository",
		"com.hallbooking.hall_service.repository",
		"com.hallbooking.notification.repository",
		"com.hallbooking.payment.repository",   // ✅ Added
		"com.hallbooking.reviewrating.repository",    // ✅ Added
		"com.hallbooking.ticketing.repository"  // ✅ Added
})
@EnableScheduling
@EnableConfigurationProperties(TwilioProperties.class)
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
}
