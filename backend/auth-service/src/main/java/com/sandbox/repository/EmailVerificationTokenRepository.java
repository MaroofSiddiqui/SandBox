package com.sandbox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandbox.entity.EmailVerificationToken;
import com.sandbox.entity.User;

/*
 * EMAIL VERIFICATION TOKEN REPOSITORY
 *
 * Purpose:
 * Performs database operations for email verification tokens.
 *
 * Spring Data JPA automatically generates the SQL queries
 * based on the method names.
 */

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    /*
     * FIND TOKEN BY TOKEN STRING
     *
     * Used when the user clicks the verification link.
     *
     * Example:
     * http://localhost:5173/verify-email?token=abc123...
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /*
     * FIND LATEST TOKEN FOR A USER
     *
     * Used while resending the verification email.
     *
     * If multiple tokens exist, only the newest one
     * should be considered valid.
     */
    Optional<EmailVerificationToken> findTopByUserOrderByCreatedAtDesc(User user);

}