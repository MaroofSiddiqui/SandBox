package com.sandbox.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sandbox.entity.EmailVerificationToken;
import com.sandbox.entity.User;
import com.sandbox.exception.InvalidOtpException;
import com.sandbox.exception.ResourceNotFoundException;
import com.sandbox.mail.HtmlEmailBuilder;
import com.sandbox.repository.EmailVerificationTokenRepository;
import com.sandbox.repository.UserRepository;
import com.sandbox.service.EmailService;
import com.sandbox.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Value;
/*
 * EMAIL VERIFICATION SERVICE IMPLEMENTATION
 *
 * Handles:
 * - Token Generation
 * - Email Sending
 * - Token Verification
 * - Resend Verification Email
 */

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

	// Email verification token repository
	private final EmailVerificationTokenRepository tokenRepository;

	// User repository
	private final UserRepository userRepository;

	// Sends emails
	private final EmailService emailService;

	// Builds HTML emails
	private final HtmlEmailBuilder htmlEmailBuilder;

	/*
	 * Frontend Base URL
	 *
	 * Loaded from application.properties.
	 *
	 * Example: http://localhost:5173
	 */
	@Value("${app.frontend.url}")
	private String frontendUrl;

	/*
	 * Constructor Injection
	 *
	 * Spring injects all required dependencies.
	 */
	public EmailVerificationServiceImpl(

			EmailVerificationTokenRepository tokenRepository,

			UserRepository userRepository,

			EmailService emailService,

			HtmlEmailBuilder htmlEmailBuilder

	) {

		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.htmlEmailBuilder = htmlEmailBuilder;

	}

	@Override
	public void sendVerificationEmail(User user) {

		// Delete previous verification token if present
		tokenRepository.findTopByUserOrderByCreatedAtDesc(user).ifPresent(tokenRepository::delete);

		// Generate unique verification token
		String token = UUID.randomUUID().toString();

		// Create verification token entity
		EmailVerificationToken verificationToken = new EmailVerificationToken();

		verificationToken.setUser(user);
		verificationToken.setToken(token);
		verificationToken.setCreatedAt(LocalDateTime.now());
		verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
		verificationToken.setVerified(false);

		// Save token
		tokenRepository.save(verificationToken);

		/*
		 * Verification URL
		 *
		 * For now we're using localhost. Later we'll move this URL into
		 * application.properties.
		 */
		String verificationUrl = frontendUrl + "/verify-email?token=" + token;

		// Build HTML email
		String html = htmlEmailBuilder.buildEmail(

				com.sandbox.mail.EmailTemplateType.EMAIL_VERIFICATION,

				"Verify Your Email",

				"Click the button below to verify your email address.",

				"Verify Email",

				verificationUrl

		);

		// Send email
		emailService.sendEmail(

				user.getEmail(),

				"Verify your Sandbox account",

				html

		);

	}

	@Override
	public void verifyEmail(String token) {

		// Find verification token
		EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
				.orElseThrow(() -> new ResourceNotFoundException("Verification token not found."));

		/*
		 * Token already used.
		 *
		 * The user's email is already verified, therefore no additional action is
		 * required.
		 */
		if (verificationToken.isVerified()) {
			return;
		}

		// Token expired
		if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {

			throw new InvalidOtpException("Verification link has expired.");

		}

		// Get associated user
		User user = verificationToken.getUser();

		// Mark user verified
		user.setEmailVerified(true);

		userRepository.save(user);

		// Mark token verified
		verificationToken.setVerified(true);

		tokenRepository.save(verificationToken);

	}

	@Override
	public void resendVerificationEmail(String email) {

		// Find user by email
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));

		// Email already verified
		if (user.isEmailVerified()) {

			throw new InvalidOtpException("Email is already verified.");

		}

		// Reuse existing logic
		sendVerificationEmail(user);

	}

}