package com.sandbox.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/*
 * EMAIL VERIFICATION TOKEN ENTITY
 *
 * Purpose:
 * Stores email verification tokens generated during user registration.
 *
 * Flow:
 *
 * User Registers
 *        ↓
 * Generate Unique Token
 *        ↓
 * Save Token in Database
 *        ↓
 * Send Verification Email
 *        ↓
 * User Clicks Verification Link
 *        ↓
 * Token Verified
 *        ↓
 * User.emailVerified = true
 *
 * Notes:
 * - One user may receive multiple verification emails.
 * - Only the latest valid token should be accepted.
 * - Tokens expire after a configured duration.
 */

@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    /*
     * PRIMARY KEY
     *
     * Auto-generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * VERIFICATION TOKEN
     *
     * A long random string (UUID) sent to the user's email.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String token;

    /*
     * TOKEN CREATION TIME
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /*
     * TOKEN EXPIRATION TIME
     *
     * Example:
     * Created : 10:00 AM
     * Expires : 10:30 AM
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /*
     * TOKEN STATUS
     *
     * false -> Not verified
     * true  -> Email verified successfully
     */
    @Column(nullable = false)
    private boolean verified;

    /*
     * ASSOCIATED USER
     *
     * Many verification tokens may belong to one user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /*
     * DEFAULT CONSTRUCTOR
     *
     * Required by JPA.
     */
    public EmailVerificationToken() {
    }

    /*
     * GETTERS & SETTERS
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}