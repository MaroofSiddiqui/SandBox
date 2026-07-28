package com.sandbox.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandbox.dto.CandidateRequest;
import com.sandbox.dto.CandidateResponse;
import com.sandbox.entity.Role;
import com.sandbox.entity.User;
import com.sandbox.exception.ResourceNotFoundException;
import com.sandbox.repository.RoleRepository;
import com.sandbox.repository.UserRepository;

/*
 * CANDIDATE SERVICE
 *
 * Purpose:
 * Contains the business logic related to candidates.
 *
 * An HR can:
 *
 * 1. Create a candidate
 * 2. Get all candidates belonging to their organization
 * 3. Get a particular candidate by ID
 *
 * Important:
 * Candidates are automatically associated with the
 * organization of the currently logged-in HR.
 *
 * Flow:
 *
 * CandidateController
 *        ↓
 * CandidateService
 *        ↓
 * UserRepository + RoleRepository + PasswordEncoder
 *        ↓
 * users table
 */
@Service
public class CandidateService {

    /*
     * USER REPOSITORY
     *
     * Used for:
     *
     * - Checking duplicate emails
     * - Saving candidates
     * - Finding candidates
     */
    private final UserRepository userRepository;


    /*
     * ROLE REPOSITORY
     *
     * Used to retrieve the CANDIDATE role
     * from the roles table.
     */
    private final RoleRepository roleRepository;


    /*
     * PASSWORD ENCODER
     *
     * Used to hash the candidate's password
     * before storing it in the database.
     */
    private final PasswordEncoder passwordEncoder;


    /*
     * CONSTRUCTOR DEPENDENCY INJECTION
     *
     * Spring automatically injects these dependencies.
     */
    public CandidateService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /*
     * CREATE CANDIDATE
     *
     * Called when an HR sends:
     *
     * POST /candidates
     *
     * The candidate automatically belongs to
     * the logged-in HR's organization.
     */
    public CandidateResponse createEmployee(
            CandidateRequest request,
            User currentHr) {


        /*
         * STEP 1: CHECK HR ORGANIZATION
         *
         * An HR must belong to an organization before
         * they can create candidates.
         */
        if (currentHr.getOrganization() == null) {

            throw new IllegalArgumentException(
                    "HR is not assigned to an organization"
            );
        }


        /*
         * STEP 2: NORMALIZE EMAIL
         *
         * Example:
         *
         * " Aman.Verma@Acme.com "
         *
         * becomes:
         *
         * "aman.verma@acme.com"
         */
        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        /*
         * STEP 3: CHECK DUPLICATE EMAIL
         *
         * Every user must have a unique email.
         */
        if (userRepository.existsByEmail(email)) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }


        /*
         * STEP 4: GET CANDIDATE ROLE
         *
         * The role is assigned by the backend.
         *
         * The client does NOT get to decide which
         * role the new user receives.
         */
        Role candidateRole =
                roleRepository
                        .findByName("CANDIDATE")
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "CANDIDATE role not found"
                                )
                        );


        /*
         * STEP 5: BUILD CANDIDATE USER
         */
        User candidate = User.builder()

                // Candidate's cleaned name
                .name(request.getName().trim())

                // Normalized email
                .email(email)

                /*
                 * Hash the plain-text password using BCrypt
                 * before storing it.
                 */
                .passwordHash(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )

                /*
                 * Assign CANDIDATE role.
                 */
                .role(candidateRole)

                /*
                 * IMPORTANT:
                 *
                 * Candidate automatically gets the same
                 * organization as the logged-in HR.
                 *
                 * We do NOT accept organizationId from
                 * CandidateRequest.
                 */
                .organization(
                        currentHr.getOrganization()
                )

                // New candidate starts as ACTIVE
                .status("ACTIVE")

                .build();


        /*
         * STEP 6: SAVE CANDIDATE
         *
         * Hibernate inserts the candidate into
         * the users table.
         */
        candidate = userRepository.save(candidate);


        /*
         * STEP 7: CONVERT ENTITY TO RESPONSE DTO
         *
         * We don't return the complete User entity because
         * it contains sensitive/internal information such
         * as passwordHash.
         */
        return toResponse(candidate);
    }


    /*
     * GET ALL CANDIDATES
     *
     * Returns candidates belonging ONLY to the
     * logged-in HR's organization.
     *
     * GET /candidates
     */
    public List<CandidateResponse> getCandidates(
            User currentHr) {


        /*
         * HR must belong to an organization.
         */
        if (currentHr.getOrganization() == null) {

            throw new IllegalArgumentException(
                    "HR is not assigned to an organization"
            );
        }


        /*
         * Query users where:
         *
         * organization_id = HR's organization
         *
         * AND
         *
         * role.name = CANDIDATE
         *
         * This provides organization-level isolation.
         */
        return userRepository
                .findByOrganizationIdAndRoleName(
                        currentHr.getOrganization().getId(),
                        "CANDIDATE"
                )

                /*
                 * Convert List<User>
                 *
                 * into
                 *
                 * List<CandidateResponse>
                 */
                .stream()
                .map(this::toResponse)
                .toList();
    }


    /*
     * GET CANDIDATE BY ID
     *
     * GET /candidates/{id}
     *
     * Finds a candidate only if:
     *
     * 1. Candidate ID matches
     * 2. Candidate belongs to HR's organization
     * 3. User has CANDIDATE role
     */
    public CandidateResponse getCandidateById(
            Long id,
            User currentHr) {


        /*
         * HR must have an organization.
         */
        if (currentHr.getOrganization() == null) {

            throw new IllegalArgumentException(
                    "HR is not assigned to an organization"
            );
        }


        /*
         * ORGANIZATION-LEVEL SECURITY
         *
         * Instead of:
         *
         * findById(id)
         *
         * we search using:
         *
         * ID
         * + organization ID
         * + CANDIDATE role
         *
         * This prevents an HR from Organization A
         * accessing a candidate from Organization B
         * simply by changing the ID in the URL.
         */
        User candidate =
                userRepository
                        .findByIdAndOrganizationIdAndRoleName(
                                id,
                                currentHr.getOrganization().getId(),
                                "CANDIDATE"
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Candidate not found"
                                )
                        );


        /*
         * Convert User entity into safe CandidateResponse.
         */
        return toResponse(candidate);
    }


    /*
     * ENTITY → DTO CONVERSION
     *
     * Converts:
     *
     * User
     *
     * into:
     *
     * CandidateResponse
     *
     * This prevents sensitive fields such as passwordHash
     * from being returned through the API.
     */
    private CandidateResponse toResponse(User user) {

        return new CandidateResponse(

                user.getId(),

                user.getName(),

                user.getEmail(),

                // Example: "CANDIDATE"
                user.getRole().getName(),

                // Organization candidate belongs to
                user.getOrganization().getId(),

                // ACTIVE / INACTIVE
                user.getStatus(),

                user.getCreatedAt()
        );
    }
}