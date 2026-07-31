package com.sandbox.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandbox.dto.ForgotPasswordRequest;
import com.sandbox.dto.ResetPasswordRequest;
import com.sandbox.dto.VerifyOtpRequest;
import com.sandbox.entity.PasswordResetOtp;
import com.sandbox.entity.User;
import com.sandbox.mail.HtmlEmailBuilder;
import com.sandbox.repository.PasswordResetOtpRepository;
import com.sandbox.repository.UserRepository;
import com.sandbox.service.EmailService;
import com.sandbox.service.PasswordResetService;
import com.sandbox.util.OtpGenerator;

/*
 * PASSWORD RESET SERVICE IMPLEMENTATION
 *
 * Handles the complete Forgot Password workflow.
 *
 * Responsibilities:
 * - Generate OTP
 * - Send OTP Email
 * - Verify OTP
 * - Reset Password
 */

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

	// Handles OTP database operations
	private final PasswordResetOtpRepository otpRepository;

	// Accesses user information
	private final UserRepository userRepository;

	// Sends emails
	private final EmailService emailService;

	// Builds HTML email templates
	private final HtmlEmailBuilder htmlEmailBuilder;

	// Encrypts passwords before storing them
	private final PasswordEncoder passwordEncoder;

	/*
	 * Constructor Injection
	 *
	 * Spring automatically injects all required dependencies.
	 */
	public PasswordResetServiceImpl(PasswordResetOtpRepository otpRepository, UserRepository userRepository,
			EmailService emailService, HtmlEmailBuilder htmlEmailBuilder, PasswordEncoder passwordEncoder) {

		this.otpRepository = otpRepository;
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.htmlEmailBuilder = htmlEmailBuilder;
		this.passwordEncoder = passwordEncoder;
	}

	/*
	 * SEND OTP
	 *
	 * Generates and emails a password reset OTP.
	 */
	@Override
	public void sendOtp(ForgotPasswordRequest request) {

		// Find user by email
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("No account found with this email."));

		// Delete previous OTP if present
		otpRepository.findTopByUserOrderByCreatedAtDesc(user).ifPresent(otpRepository::delete);

		// Generate secure 6-digit OTP
		String otp = OtpGenerator.generate(6);

		// Create new OTP entity
		PasswordResetOtp passwordOtp = new PasswordResetOtp();

		passwordOtp.setUser(user);
		passwordOtp.setOtp(otp);
		passwordOtp.setCreatedAt(LocalDateTime.now());
		passwordOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
		passwordOtp.setVerified(false);
		passwordOtp.setUsed(false);
		passwordOtp.setAttempts(0);

		otpRepository.save(passwordOtp);

		// Build email HTML
		String html = htmlEmailBuilder.buildOtpTemplate(

				user.getName(),

				otp,

				10

		);

		// Send email
		emailService.sendEmail(

				user.getEmail(),

				"Sandbox Password Reset OTP",

				html

		);
	}

	
	/*
	 * VERIFY OTP
	 *
	 * Validates the OTP entered by the user.
	 */
	@Override
	public void verifyOtp(VerifyOtpRequest request) {

		// Find user by email
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("No account found with this email."));

		// Fetch latest OTP generated for the user
		PasswordResetOtp passwordOtp = otpRepository.findTopByUserOrderByCreatedAtDesc(user)
				.orElseThrow(() -> new RuntimeException("No OTP request found."));

		// Check if OTP has already been used
		if (passwordOtp.isUsed()) {
			throw new RuntimeException("OTP has already been used.");
		}

		// Check if OTP has expired
		if (passwordOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP has expired.");
		}

		// Increment failed attempts for incorrect OTP
		if (!passwordOtp.getOtp().equals(request.getOtp())) {

			passwordOtp.setAttempts(passwordOtp.getAttempts() + 1);

			// Lock OTP after 3 incorrect attempts
			if (passwordOtp.getAttempts() >= 3) {

				passwordOtp.setUsed(true);

			}

			otpRepository.save(passwordOtp);

			throw new RuntimeException("Invalid OTP.");
		}

		// Mark OTP as verified
		passwordOtp.setVerified(true);

		otpRepository.save(passwordOtp);

	}

	/*
	 * RESET PASSWORD
	 *
	 * Updates the user's password after successful OTP verification.
	 */
	@Override
	public void resetPassword(ResetPasswordRequest request) {

		// Find user by email
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() ->
						new RuntimeException("No account found with this email."));

		// Fetch latest OTP generated for the user
		PasswordResetOtp passwordOtp = otpRepository
				.findTopByUserOrderByCreatedAtDesc(user)
				.orElseThrow(() ->
						new RuntimeException("No OTP request found."));

		// OTP must be verified before password reset
		if (!passwordOtp.isVerified()) {
			throw new RuntimeException("OTP verification required.");
		}

		// OTP must not be expired
		if (passwordOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP has expired.");
		}

		// OTP must not be reused
		if (passwordOtp.isUsed()) {
			throw new RuntimeException("OTP has already been used.");
		}

		// Update password
		user.setPasswordHash(
				passwordEncoder.encode(request.getNewPassword())
		);

		userRepository.save(user);

		// Mark OTP as used
		passwordOtp.setUsed(true);

		otpRepository.save(passwordOtp);

	}

}