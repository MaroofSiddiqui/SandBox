package com.sandbox.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sandbox.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/*
 * JWT SERVICE
 *
 * Purpose:
 * This service contains the core JWT-related logic of the application.
 *
 * It is responsible for:
 *
 * 1. Generating JWT tokens after successful login
 * 2. Signing tokens using a secret key
 * 3. Extracting information (claims) from JWT tokens
 * 4. Checking token expiration
 * 5. Validating whether a token belongs to a valid ACTIVE user
 *
 * It is mainly used by:
 *
 * AuthService
 * -> Generates JWT after successful login
 *
 * JwtAuthenticationFilter
 * -> Reads and validates JWT on later requests
 */
@Service
public class JwtService {

    /*
     * JWT SECRET KEY
     *
     * @Value reads the value from application.properties
     * or application.yml.
     *
     * Example:
     *
     * app.jwt.secret=BASE64_ENCODED_SECRET_KEY
     *
     * This secret is used to cryptographically sign JWTs
     * and later verify that they were not modified.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;


    /*
     * JWT EXPIRATION TIME
     *
     * Also loaded from application configuration.
     *
     * Example:
     *
     * app.jwt.expiration=3600000
     *
     * This value is normally expressed in milliseconds.
     *
     * 3,600,000 ms = 1 hour.
     */
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;


    /*
     * GET SIGNING KEY
     *
     * Converts the configured Base64 secret string
     * into a SecretKey that the JWT library can use
     * for signing and verification.
     */
    private SecretKey getSigningKey() {

        /*
         * Decode the Base64 secret into raw bytes.
         */
        byte[] keyBytes =
                Decoders.BASE64.decode(jwtSecret);

        /*
         * Convert those bytes into an HMAC-compatible
         * cryptographic signing key.
         */
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /*
     * GENERATE JWT TOKEN
     *
     * Called after a user successfully logs in.
     *
     * The generated token contains:
     *
     * subject        -> user's email
     * userId         -> user's database ID
     * role           -> SUPER_ADMIN / HR / CANDIDATE
     * organizationId -> user's organization
     * issuedAt       -> token creation time
     * expiration     -> token expiry time
     *
     * The token is finally signed using our secret key.
     */
    public String generateToken(User user) {

        /*
         * Current date/time.
         *
         * This becomes the JWT's issued-at time.
         */
        Date now = new Date();


        /*
         * Calculate when the JWT should expire.
         *
         * expiry =
         * current time + configured expiration duration
         */
        Date expiryDate =
                new Date(now.getTime() + jwtExpiration);


        /*
         * Build the JWT.
         */
        return Jwts.builder()

                /*
                 * SUBJECT (sub)
                 *
                 * The subject identifies who this token belongs to.
                 *
                 * We use the user's email because email is unique
                 * in our users table.
                 */
                .subject(user.getEmail())


                /*
                 * CUSTOM CLAIM: userId
                 *
                 * Stores the database ID of the logged-in user.
                 */
                .claim(
                        "userId",
                        user.getId()
                )


                /*
                 * CUSTOM CLAIM: role
                 *
                 * Stores the user's role.
                 *
                 * Examples:
                 *
                 * SUPER_ADMIN
                 * HR
                 * CANDIDATE
                 */
                .claim(
                        "role",
                        user.getRole().getName()
                )


                /*
                 * CUSTOM CLAIM: organizationId
                 *
                 * HR and CANDIDATE normally belong to
                 * an organization.
                 *
                 * SUPER_ADMIN has organization = null.
                 *
                 * Therefore we use a ternary condition:
                 *
                 * organization exists
                 *      ↓
                 * store organization ID
                 *
                 * organization is null
                 *      ↓
                 * store null
                 */
                .claim(
                        "organizationId",
                        user.getOrganization() != null
                                ? user.getOrganization().getId()
                                : null
                )


                /*
                 * STANDARD JWT CLAIM: issuedAt (iat)
                 *
                 * Records when the token was created.
                 */
                .issuedAt(now)


                /*
                 * STANDARD JWT CLAIM: expiration (exp)
                 *
                 * Records when the token becomes invalid.
                 */
                .expiration(expiryDate)


                /*
                 * Cryptographically sign the JWT using
                 * our secret key.
                 *
                 * This allows the backend to detect if
                 * someone modifies/tampers with the token.
                 */
                .signWith(getSigningKey())


                /*
                 * compact() builds the final JWT string.
                 *
                 * A JWT normally looks like:
                 *
                 * xxxxx.yyyyy.zzzzz
                 *
                 * HEADER.PAYLOAD.SIGNATURE
                 */
                .compact();
    }


    /*
     * EXTRACT ALL CLAIMS
     *
     * Parses an existing JWT and returns its payload/claims.
     *
     * This method is private because other methods such as
     * extractEmail() and extractExpiration() use it internally.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()

                /*
                 * Verify the JWT signature using the SAME
                 * secret key that was used to sign it.
                 *
                 * If someone changes the JWT contents,
                 * signature verification will fail.
                 */
                .verifyWith(getSigningKey())

                // Build the JWT parser.
                .build()

                /*
                 * Parse and verify the signed JWT.
                 *
                 * Invalid, malformed, tampered, or expired
                 * tokens may cause JWT-related exceptions.
                 */
                .parseSignedClaims(token)

                /*
                 * Get the JWT payload containing claims such as:
                 *
                 * sub
                 * userId
                 * role
                 * organizationId
                 * iat
                 * exp
                 */
                .getPayload();
    }


    /*
     * EXTRACT EMAIL
     *
     * Retrieves the JWT subject.
     *
     * Since generateToken() stored email as:
     *
     * .subject(user.getEmail())
     *
     * getSubject() returns that email.
     */
    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }


    /*
     * EXTRACT EXPIRATION DATE
     *
     * Reads the standard "exp" claim from the JWT.
     */
    public Date extractExpiration(String token) {

        return extractAllClaims(token)
                .getExpiration();
    }


    /*
     * CHECK TOKEN EXPIRATION
     *
     * Compares the token's expiration date
     * with the current date/time.
     *
     * Example:
     *
     * expiration = 10:00 PM
     * current     = 11:00 PM
     *
     * expiration.before(current)
     *              ↓
     * true
     *
     * Therefore token is expired.
     */
    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }


    /*
     * VALIDATE JWT
     *
     * Performs application-level validation of the token.
     *
     * The token is considered valid when:
     *
     * 1. Token email matches the database user's email
     * 2. Token has not expired
     * 3. User account is still ACTIVE
     */
    public boolean isTokenValid(
            String token,
            User user) {

        // Extract email from JWT.
        String email = extractEmail(token);


        /*
         * All THREE conditions must be true.
         */
        return email.equals(user.getEmail())

                // Token must still be within its valid lifetime.
                && !isTokenExpired(token)

                /*
                 * User must still have ACTIVE status.
                 *
                 * This is important because even if someone has
                 * an otherwise valid JWT, making their account
                 * INACTIVE prevents successful authentication.
                 */
                && "ACTIVE".equals(user.getStatus());
    }
}