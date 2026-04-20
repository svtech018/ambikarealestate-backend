package com.realestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Real Estate Backend Application
 * 
 * Main Spring Boot application class for the Real Estate Management System.
 * This application provides REST APIs for property management, user management,
 * and inquiry handling with separate modules for admin and user operations.
 * 
 * Features:
 * - Property listing management for admins
 * - Property viewing for users
 * - Inquiry submission and management
 * - JWT-based authentication and authorization
 * - PostgreSQL database integration
 * - Comprehensive logging with Log4j2
 * - Security configuration with Spring Security
 * 
 * Architecture:
 * - Modular design with separate admin and user packages
 * - Layered architecture (Controller -> Service -> Repository -> Entity)
 * - Configuration-driven setup with externalized properties
 * 
 * @author Real Estate Development Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@SpringBootApplication(scanBasePackages = {
        "com.realestate"
})
@EnableJpaRepositories(basePackages = {
        "com.realestate.repository"
})
@EnableJpaAuditing
@EnableTransactionManagement
@EnableCaching
@EnableAsync
public class RealEstateBackendApplication {

    /**
     * Main method to start the Spring Boot application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set system properties for better performance
        System.setProperty("spring.jpa.open-in-view", "false");

        // Start the Spring Boot application
        SpringApplication.run(RealEstateBackendApplication.class, args);
    }
}