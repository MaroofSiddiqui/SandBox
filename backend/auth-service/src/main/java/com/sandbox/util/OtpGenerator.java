package com.sandbox.util;

import java.security.SecureRandom;

/*
 * OTP GENERATOR
 *
 * Responsible for generating secure numeric OTPs.
 *
 * This utility can be reused for:
 * - Forgot Password
 * - Email Verification
 * - Login OTP (Future)
 */
public final class OtpGenerator {

    // Cryptographically secure random generator
    private static final SecureRandom RANDOM = new SecureRandom();

    // Utility class should never be instantiated
    private OtpGenerator() {
    }

    /*
     * Generates a numeric OTP.
     *
     * Example:
     * 483921
     */
    public static String generate(int length) {

        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {

            otp.append(RANDOM.nextInt(10));

        }

        return otp.toString();
    }

}