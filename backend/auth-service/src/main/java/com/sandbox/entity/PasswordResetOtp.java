package com.sandbox.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/*
 * PASSWORD RESET OTP ENTITY
 *
 * Stores OTPs generated for password reset requests.
 *
 * One user can have one active OTP at a time.
 */

@Entity
@Table(name = "password_reset_otps")
public class PasswordResetOtp {

	// Primary key
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// User requesting password reset
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// Generated 6-digit OTP
	@Column(nullable = false, length = 6)
	private String otp;

	// OTP generation time
	@Column(nullable = false)
	private LocalDateTime createdAt;

	// OTP expiry time
	@Column(nullable = false)
	private LocalDateTime expiresAt;

	// Indicates whether OTP has been successfully verified
	@Column(nullable = false)
	private boolean verified = false;

	// Prevents OTP reuse
	@Column(nullable = false)
	private boolean used = false;

	// Number of incorrect verification attempts
	@Column(nullable = false)
	private int attempts = 0;

	// Default constructor required by JPA
	public PasswordResetOtp() {
	}

	// Getters & Setters

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
}