package com.sandbox.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandbox.dto.AuthResponse;
import com.sandbox.dto.LoginRequest;
import com.sandbox.entity.User;
import com.sandbox.repository.UserRepository;
import com.sandbox.security.JwtService;
import com.sandbox.exception.InvalidCredentialsException;

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
     * Used to find the user from the database
     * using the email supplied during login.
     */
    private final UserRepository userRepository;


    /*
     * PASSWORD ENCODER
     *
     * Used to compare the plain-text password entered
     * during login with the BCrypt hash stored in the database.
     *
     * This bean was created in SecurityBeansConfig.
     */
    private final PasswordEncoder passwordEncoder;


    /*
     * JWT SERVICE
     *
     * Used to generate a JWT token after
     * successful authentication.
     */
    private final JwtService jwtService;


    /*
     * CONSTRUCTOR DEPENDENCY INJECTION
     *
     * Spring automatically injects:
     *
     * - UserRepository
     * - PasswordEncoder
     * - JwtService
     *
     * when AuthService is created.
     */
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    /*
     * LOGIN METHOD
     *
     * Performs the complete authentication process.
     *
     * LoginRequest contains:
     *
     * - email
     * - password
     *
     * If authentication succeeds:
     * -> AuthResponse containing JWT is returned.
     *
     * If authentication fails:
     * -> An exception is thrown.
     */
    public AuthResponse login(LoginRequest request) {

        /*
         * STEP 1: FIND USER BY EMAIL
         *
         * Search the users table using the email
         * received in LoginRequest.
         *
         * findByEmail() returns Optional<User>.
         */
        User user = userRepository
                .findByEmail(request.getEmail())

                /*
                 * If no user exists with this email,
                 * throw InvalidCredentialsException.
                 *
                 * We deliberately use the generic message
                 * "Invalid email or password" instead of
                 * saying "Email does not exist".
                 *
                 * This avoids revealing whether a particular
                 * email account exists in the system.
                 */
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );


        /*
         * STEP 2: VERIFY PASSWORD
         *
         * request.getPassword()
         * -> Plain-text password entered during login.
         *
         * user.getPasswordHash()
         * -> BCrypt hash stored in the database.
         *
         * PasswordEncoder.matches() safely checks whether
         * the entered password corresponds to the stored hash.
         *
         * We do NOT decrypt the stored password.
         */
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            /*
             * Password does not match.
             *
             * InvalidCredentialsException is handled by
             * GlobalExceptionHandler and becomes:
             *
             * HTTP 401 Unauthorized.
             */
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }


        /*
         * STEP 3: CHECK ACCOUNT STATUS
         *
         * Even if email and password are correct,
         * an INACTIVE user should not be allowed to log in.
         */
        if (!"ACTIVE".equals(user.getStatus())) {

            /*
             * Currently a generic RuntimeException is thrown.
             *
             * This indicates that the account exists
             * but has been disabled/inactivated.
             */
            throw new RuntimeException(
                    "User account is inactive"
            );
        }


        /*
         * STEP 4: GENERATE JWT
         *
         * At this point:
         *
         * ✓ User exists
         * ✓ Password is correct
         * ✓ Account is ACTIVE
         *
         * So authentication has succeeded.
         *
         * JwtService generates a signed token containing
         * information such as:
         *
         * - email
         * - userId
         * - role
         * - organizationId
         * - issued time
         * - expiration time
         */
        String token = jwtService.generateToken(user);


        /*
         * STEP 5: GET ORGANIZATION ID
         *
         * HR and CANDIDATE normally belong to an organization.
         *
         * SUPER_ADMIN has organization = null.
         *
         * Ternary operator:
         *
         * organization exists
         *       ↓
         * return its ID
         *
         * organization is null
         *       ↓
         * return null
         */
        Long organizationId =
                user.getOrganization() != null
                        ? user.getOrganization().getId()
                        : null;


        /*
         * STEP 6: CREATE LOGIN RESPONSE
         *
         * Return the JWT together with safe user information.
         *
         * Notice that passwordHash is NOT included.
         */
        return new AuthResponse(
                token,

                // Tells the client to use:
                // Authorization: Bearer <token>
                "Bearer",

                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName(),
                organizationId
        );
    }
}