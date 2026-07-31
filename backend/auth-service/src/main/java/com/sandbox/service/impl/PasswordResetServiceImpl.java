package com.sandbox.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandbox.dto.ForgotPasswordRequest;
import com.sandbox.dto.ResetPasswordRequest;
import com.sandbox.dto.VerifyOtpRequest;
import com.sandbox.entity.PasswordResetOtp;
import com.sandbox.entity.User;
import com.sandbox.exception.InvalidOtpException;
import com.sandbox.exception.UserNotFoundException;
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
				.orElseThrow(() -> new UserNotFoundException("No account found with this email."));

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
	            .orElseThrow(() -> new UserNotFoundException("User not found."));

	    // Find OTP record
	    PasswordResetOtp otpEntity = otpRepository.findTopByUserOrderByCreatedAtDesc(user)
	            .orElseThrow(() -> new InvalidOtpException("OTP not found."));

	    // OTP already used
	    if (otpEntity.isUsed()) {
	        throw new InvalidOtpException("OTP has already been used.");
	    }

	    // OTP expired
	    if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
	        throw new InvalidOtpException("OTP has expired.");
	    }

	    // Maximum attempts reached
	    if (otpEntity.getAttempts() >= 5) {
	        throw new InvalidOtpException("Maximum OTP attempts exceeded.");
	    }

	    // Incorrect OTP
	    if (!otpEntity.getOtp().equals(request.getOtp())) {

	        otpEntity.setAttempts(otpEntity.getAttempts() + 1);

	        otpRepository.save(otpEntity);

	        throw new InvalidOtpException("Invalid OTP.");
	    }

	    // OTP verified successfully
	    otpEntity.setVerified(true);

	    otpRepository.save(otpEntity);
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
	            .orElseThrow(() -> new UserNotFoundException("User not found."));

	    // Find OTP record
	    PasswordResetOtp otpEntity = otpRepository.findTopByUserOrderByCreatedAtDesc(user)
	            .orElseThrow(() -> new InvalidOtpException("OTP not found."));

	    // OTP must be verified first
	    if (!otpEntity.isVerified()) {
	        throw new InvalidOtpException("Please verify your OTP first.");
	    }

	    // OTP already used
	    if (otpEntity.isUsed()) {
	        throw new InvalidOtpException("OTP has already been used.");
	    }

	    // Update password
	    user.setPasswordHash(
	            passwordEncoder.encode(request.getNewPassword())
	    );

	    userRepository.save(user);

	    // Mark OTP as used
	    otpEntity.setUsed(true);

	    otpRepository.save(otpEntity);

	    // Build confirmation email
	    String html = htmlEmailBuilder.buildEmail(
	            com.sandbox.mail.EmailTemplateType.PASSWORD_CHANGED,
	            "Password Changed Successfully",
	            "Your Sandbox account password has been changed successfully.",
	            null,
	            null
	    );

	    // Send confirmation email
	    emailService.sendEmail(
	            user.getEmail(),
	            "Sandbox Password Changed",
	            html
	    );
	}

}