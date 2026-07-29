package com.sandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * SECURITY BEANS CONFIGURATION
 *
 * Purpose:
 * This class defines security-related objects (beans)
 * that Spring should create and manage.
 *
 * Currently, it provides the PasswordEncoder used
 * throughout the application for password hashing.
 */
@Configuration
public class SecurityBeansConfig {

    /*
     * @Bean tells Spring:
     * "Create this object and keep it inside the Spring container."
     *
     * After this, PasswordEncoder can be injected into other
     * classes such as AuthService, CandidateService,
     * DataInitializer, etc.
     */
    @Bean
    PasswordEncoder passwordEncoder() {

        /*
         * BCryptPasswordEncoder securely hashes passwords.
         *
         * Example:
         * "Admin@123"
         *      ↓
         * "$2a$10$......"
         *
         * The hashed password is stored in the database
         * instead of the original plain-text password.
         *
         * BCrypt also automatically handles salt,
         * making identical passwords produce different hashes.
         */
        return new BCryptPasswordEncoder();
    }
}