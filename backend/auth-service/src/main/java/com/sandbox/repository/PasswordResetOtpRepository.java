package com.sandbox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandbox.entity.PasswordResetOtp;
import com.sandbox.entity.User;

/*
 * PASSWORD RESET OTP REPOSITORY
 *
 * Handles database operations related to OTPs.
 */
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

	// Returns the latest OTP for a user
	Optional<PasswordResetOtp> findTopByUserOrderByCreatedAtDesc(User user);

	// Returns OTP by value
	Optional<PasswordResetOtp> findByUserAndOtp(
	        User user,
	        String otp
	);

}