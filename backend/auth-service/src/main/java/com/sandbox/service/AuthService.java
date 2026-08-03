package com.sandbox.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandbox.dto.AuthResponse;
import com.sandbox.dto.ChangePasswordRequest;
import com.sandbox.dto.LoginRequest;
import com.sandbox.dto.RegisterRequest;
import com.sandbox.dto.UpdateProfileRequest;
import com.sandbox.entity.Role;
import com.sandbox.entity.User;
import com.sandbox.exception.AccountLockedException;
import com.sandbox.repository.RoleRepository;
import com.sandbox.repository.UserRepository;
import com.sandbox.security.JwtService;
import com.sandbox.exception.InvalidCredentialsException;
import com.sandbox.exception.ResourceNotFoundException;
import com.sandbox.exception.AccountInactiveException;

/*
 * AUTH SERVICE
 *
 * Purpose:
 * This service contains the business logic for user login.
 *
 * It is responsible for:
 *
 * 1. Finding the user using their email
 * 2. Verifying the entered password
 * 3. Checking whether the user account is ACTIVE
 * 4. Generating a JWT token
 * 5. Creating and returning AuthResponse
 *
 * Flow:
 *
 * AuthController
 *      ↓
 * AuthService
 *      ↓
 * UserRepository + PasswordEncoder + JwtService
 *      ↓
 * AuthResponse
 */
@Service
public class AuthService {

	/*
	 * USER REPOSITORY
	 *
	 * Used to find the user from the database using the email supplied during
	 * login.
	 */
	private final UserRepository userRepository;

	/*
	 * PASSWORD ENCODER
	 *
	 * Used to compare the plain-text password entered during login with the BCrypt
	 * hash stored in the database.
	 *
	 * This bean was created in SecurityBeansConfig.
	 */
	private final PasswordEncoder passwordEncoder;

	/*
	 * JWT SERVICE
	 *
	 * Used to generate a JWT token after successful authentication.
	 */
	private final JwtService jwtService;

	// Accesses application roles
	private final RoleRepository roleRepository;

	// Sends verification email after registration
	private final EmailVerificationService emailVerificationService;

	/*
	 * CONSTRUCTOR DEPENDENCY INJECTION
	 *
	 * Spring automatically injects:
	 *
	 * - UserRepository - PasswordEncoder - JwtService
	 *
	 * when AuthService is created.
	 */
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			RoleRepository roleRepository, EmailVerificationService emailVerificationService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.roleRepository = roleRepository;
		this.emailVerificationService = emailVerificationService;
	}

	/*
	 * REGISTER
	 *
	 * Creates a new public CANDIDATE account.
	 *
	 * Flow: - Check duplicate email - Load CANDIDATE role - Hash password - Create
	 * unverified user - Save user - Send verification email
	 */
	public void register(RegisterRequest request) {

		// Prevent duplicate accounts
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("An account with this email already exists.");
		}

		// Public registration always creates a CANDIDATE
		Role candidateRole = roleRepository.findByName("CANDIDATE")
				.orElseThrow(() -> new ResourceNotFoundException("CANDIDATE role not found."));

		// Create new user
		User user = new User();

		user.setName(request.getName());
		user.setEmail(request.getEmail());

		// Never store plain-text passwords
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

		// Role is controlled by backend
		user.setRole(candidateRole);

		// Public candidate is not assigned to an organization yet
		user.setOrganization(null);

		// Account exists but email must still be verified
		user.setEmailVerified(false);

		user.setStatus("ACTIVE");

		// Save user first so it receives a database ID
		User savedUser = userRepository.save(user);

		// Generate token and send verification email
		emailVerificationService.sendVerificationEmail(savedUser);
	}

	/*
	 * CHANGE PASSWORD
	 *
	 * Allows an authenticated user to change their password.
	 *
	 * Flow: - Identify logged-in user - Verify current password - Check new
	 * password confirmation - Prevent reuse of current password - BCrypt encode new
	 * password - Save updated password
	 */
	public void changePassword(String email, ChangePasswordRequest request) {

		/*
		 * STEP 1: FIND AUTHENTICATED USER
		 */
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));

		/*
		 * STEP 2: VERIFY CURRENT PASSWORD
		 */
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {

			throw new InvalidCredentialsException("Current password is incorrect.");
		}

		/*
		 * STEP 3: CHECK PASSWORD CONFIRMATION
		 */
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {

			throw new IllegalArgumentException("New password and confirm password do not match.");
		}

		/*
		 * STEP 4: PREVENT REUSING CURRENT PASSWORD
		 */
		if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {

			throw new IllegalArgumentException("New password must be different from current password.");
		}

		/*
		 * STEP 5: ENCODE NEW PASSWORD
		 */
		String encodedPassword = passwordEncoder.encode(request.getNewPassword());

		/*
		 * STEP 6: UPDATE USER
		 */
		user.setPasswordHash(encodedPassword);

		userRepository.save(user);
	}

	public void updateProfile(User user, UpdateProfileRequest request) {

		/*
		 * Only fields that the user is allowed to modify through self-service profile
		 * editing are updated.
		 *
		 * Security-sensitive fields such as:
		 *
		 * - role - organization - status - emailVerified
		 *
		 * cannot be changed through this endpoint.
		 */

		user.setName(request.getName().trim());

		userRepository.save(user);
	}

	/*
	 * LOGIN METHOD
	 *
	 * Performs the complete authentication process.
	 *
	 * LoginRequest contains:
	 *
	 * - email - password
	 *
	 * If authentication succeeds: -> AuthResponse containing JWT is returned.
	 *
	 * If authentication fails: -> An exception is thrown.
	 */
	public AuthResponse login(LoginRequest request) {

		/*
		 * STEP 1: FIND USER
		 *
		 * Do not reveal whether the email exists.
		 */
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		/*
		 * STEP 2: CHECK TEMPORARY ACCOUNT LOCK
		 *
		 * If lockedUntil exists and is still in the future, the user is not allowed to
		 * attempt authentication.
		 */
		if (user.getLockedUntil() != null) {

			if (user.getLockedUntil().isAfter(LocalDateTime.now())) {

				throw new AccountLockedException(
						"Too many failed login attempts. Account is temporarily locked. Please try again later.");
			}

			/*
			 * Lock period has expired.
			 *
			 * Automatically unlock the account and reset the failed-attempt counter.
			 */
			user.setLockedUntil(null);
			user.setFailedLoginAttempts(0);

			userRepository.save(user);
		}

		/*
		 * STEP 3: VERIFY PASSWORD
		 */
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {

			int failedAttempts = user.getFailedLoginAttempts() + 1;

			user.setFailedLoginAttempts(failedAttempts);

			/*
			 * LOCK ACCOUNT AFTER 5 FAILED ATTEMPTS
			 */
			if (failedAttempts >= 5) {

				user.setLockedUntil(LocalDateTime.now().plusMinutes(30));

				userRepository.save(user);

				throw new AccountLockedException(
						"Too many failed login attempts. Account has been locked for 30 minutes.");
			}

			/*
			 * Save failed attempt count.
			 */
			userRepository.save(user);

			throw new InvalidCredentialsException("Invalid email or password");
		}

		/*
		 * STEP 4: PASSWORD IS CORRECT
		 *
		 * Reset previous failed attempts.
		 */
		if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {

			user.setFailedLoginAttempts(0);
			user.setLockedUntil(null);

			userRepository.save(user);
		}

		/*
		 * STEP 5: CHECK USER STATUS
		 */
		if (!"ACTIVE".equals(user.getStatus())) {

			throw new AccountInactiveException("User account is inactive");
		}

		/*
		 * STEP 6: CHECK ORGANIZATION STATUS
		 */
		if (user.getOrganization() != null && !"ACTIVE".equals(user.getOrganization().getStatus())) {

			throw new AccountInactiveException("Organization account is inactive");
		}

		/*
		 * STEP 7: CHECK EMAIL VERIFICATION
		 */
		if (!user.isEmailVerified()) {

			throw new AccountInactiveException("Please verify your email before signing in.");
		}

		/*
		 * STEP 8: GENERATE JWT
		 */
		String token = jwtService.generateToken(user);

		/*
		 * STEP 9: GET ORGANIZATION ID
		 */
		Long organizationId = user.getOrganization() != null ? user.getOrganization().getId() : null;

		/*
		 * STEP 10: RETURN LOGIN RESPONSE
		 */

		return new AuthResponse(

				token,

				"Bearer",

				user.getId(),

				user.getName(),

				user.getEmail(),

				user.getRole().getName(),

				organizationId);
	}

	public RoleRepository getRoleRepository() {
		return roleRepository;
	}

	public EmailVerificationService getEmailVerificationService() {
		return emailVerificationService;
	}
}