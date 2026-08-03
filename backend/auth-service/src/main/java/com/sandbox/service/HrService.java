package com.sandbox.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandbox.dto.CreateHrRequest;
import com.sandbox.dto.UpdateHrRequest;
import com.sandbox.entity.Organization;
import com.sandbox.entity.Role;
import com.sandbox.entity.User;
import com.sandbox.exception.ResourceNotFoundException;
import com.sandbox.repository.OrganizationRepository;
import com.sandbox.repository.RoleRepository;
import com.sandbox.repository.UserRepository;
import java.util.List;

/*
 * HR SERVICE
 *
 * Purpose:
 * This service contains the business logic for creating
 * a new HR user.
 *
 * In the current security configuration, /hrs/** can only
 * be accessed by SUPER_ADMIN.
 *
 * Main responsibilities:
 *
 * 1. Normalize the HR email
 * 2. Check whether the email already exists
 * 3. Find the organization where HR will work
 * 4. Find the HR role
 * 5. Encrypt/hash the HR password using BCrypt
 * 6. Build the User entity
 * 7. Save the new HR into the database
 *
 * Flow:
 *
 * HrController
 *      ↓
 * HrService
 *      ↓
 * Repositories + PasswordEncoder
 *      ↓
 * users table
 */
@Service
public class HrService {

    /*
     * USER REPOSITORY
     *
     * Used for:
     *
     * - Checking whether an email already exists
     * - Saving the newly created HR
     */
    private final UserRepository userRepository;


    /*
     * ROLE REPOSITORY
     *
     * Used to find the "HR" role from the roles table.
     *
     * We need the actual Role entity because User contains:
     *
     * private Role role;
     */
    private final RoleRepository roleRepository;


    /*
     * ORGANIZATION REPOSITORY
     *
     * Used to find the organization specified by
     * organizationId in CreateHrRequest.
     *
     * Every HR must be associated with an organization.
     */
    private final OrganizationRepository organizationRepository;


    /*
     * PASSWORD ENCODER
     *
     * Used to convert the plain-text password into
     * a BCrypt hash before storing it.
     *
     * Plain password:
     *
     * Hr@12345
     *
     *      ↓ BCrypt
     *
     * $2a$10$....
     */
    private final PasswordEncoder passwordEncoder;


    /*
     * CONSTRUCTOR DEPENDENCY INJECTION
     *
     * Spring automatically provides all four dependencies
     * when HrService is created.
     */
    public HrService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /*
     * CREATE HR
     *
     * Receives the validated CreateHrRequest from HrController
     * and performs the complete HR creation process.
     *
     * CreateHrRequest contains:
     *
     * - name
     * - email
     * - password
     * - organizationId
     *
     * Returns:
     *
     * The newly created and saved User entity.
     */
    public User createHr(CreateHrRequest request) {


        /*
         * STEP 1: NORMALIZE EMAIL
         *
         * trim()
         * -> Removes spaces from the beginning/end.
         *
         * toLowerCase()
         * -> Converts email to lowercase.
         *
         * Example:
         *
         * "  Rahul.HR@Acme.com  "
         *
         * becomes:
         *
         * "rahul.hr@acme.com"
         */
        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        /*
         * STEP 2: CHECK FOR DUPLICATE EMAIL
         *
         * Since User.email is unique, two accounts should
         * not have the same email.
         */
        if (userRepository.existsByEmail(email)) {

            /*
             * GlobalExceptionHandler currently handles
             * IllegalArgumentException as:
             *
             * HTTP 409 Conflict.
             */
            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }


        /*
         * STEP 3: FIND ORGANIZATION
         *
         * The SUPER_ADMIN supplies organizationId
         * when creating the HR.
         *
         * Example request:
         *
         * {
         *     "name": "Rahul",
         *     "email": "rahul@acme.com",
         *     "password": "Hr@12345",
         *     "organizationId": 2
         * }
         *
         * We now retrieve organization ID 2.
         */
        Organization organization =
                organizationRepository
                        .findById(
                                request.getOrganizationId()
                        )

                        /*
                         * If the organization does not exist,
                         * throw ResourceNotFoundException.
                         *
                         * GlobalExceptionHandler converts this to:
                         *
                         * HTTP 404 Not Found.
                         */
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found"
                                )
                        );


        /*
         * STEP 4: FIND HR ROLE
         *
         * Retrieve the Role entity whose name is "HR".
         *
         * We don't trust the client to send the role.
         *
         * The backend itself decides:
         *
         * New user created through this service
         *          ↓
         * role = HR
         *
         * This prevents someone from sending something like:
         *
         * "role": "SUPER_ADMIN"
         *
         * and creating a privileged account.
         */
        Role hrRole =
                roleRepository
                        .findByName("HR")

                        /*
                         * If the HR role is unexpectedly missing
                         * from the roles table, throw an exception.
                         */
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "HR role not found"
                                )
                        );


        /*
         * STEP 5: BUILD HR USER
         *
         * HR is stored in the same "users" table as
         * SUPER_ADMIN and CANDIDATE.
         *
         * What makes this user an HR is:
         *
         * role = hrRole
         */
        User hr = User.builder()

                /*
                 * Remove unnecessary spaces around the name.
                 */
                .name(
                        request.getName().trim()
                )


                /*
                 * Store normalized email.
                 */
                .email(email)


                /*
                 * HASH PASSWORD BEFORE STORAGE
                 *
                 * request.getPassword()
                 *      ↓
                 * Plain password
                 *      ↓
                 * passwordEncoder.encode()
                 *      ↓
                 * BCrypt hash
                 *      ↓
                 * password_hash column
                 *
                 * The plain-text password is NEVER stored
                 * in the database.
                 */
                .passwordHash(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )


                /*
                 * Assign the HR role retrieved from
                 * the roles table.
                 *
                 * User.role_id will reference hrRole.id.
                 */
                .role(hrRole)


                /*
                 * Associate this HR with the selected organization.
                 *
                 * User.organization_id will reference
                 * organization.id.
                 */
                .organization(organization)


                /*
                 * New HR accounts are ACTIVE by default.
                 */
                .status("ACTIVE")


                /*
                 * Build the User object.
                 *
                 * At this point it exists in Java memory,
                 * but has not yet been inserted into the DB.
                 */
                .build();


        /*
         * STEP 6: SAVE HR
         *
         * save(hr) causes Hibernate/JPA to insert
         * the new user into the users table.
         *
         * Before INSERT, User's @PrePersist method runs:
         *
         * createdAt = now
         * updatedAt = now
         *
         * Database then generates the user's ID.
         *
         * The saved User entity is returned.
         */
        return userRepository.save(hr);
    }
    
    /*
     * GET ALL HRS
     *
     * Returns all users having the HR role.
     *
     * SUPER_ADMIN uses this to view HR accounts
     * across all organizations.
     */
    public List<User> getAllHrs() {

        /*
         * We already have:
         *
         * findByOrganizationIdAndRoleName(...)
         *
         * but that requires an organization ID.
         *
         * Since SUPER_ADMIN needs HRs across ALL organizations,
         * we need a repository method that filters only by role.
         */
        return userRepository.findByRoleName("HR");
    }
    
    /*
     * GET HR BY ID
     *
     * Finds a specific user by ID and ensures
     * that the requested user actually has the HR role.
     *
     * Example:
     * GET /hrs/2
     */
    public User getHrById(Long id) {

        User hr = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "HR not found"
                        )
                );

        /*
         * An existing user is not necessarily an HR.
         *
         * For example, ID 4 might belong to a CANDIDATE.
         * We should not return that account through /hrs/{id}.
         */
        if (!"HR".equals(hr.getRole().getName())) {
            throw new ResourceNotFoundException(
                    "HR not found"
            );
        }

        return hr;
    }
    
    /*
     * UPDATE HR
     *
     * Updates an existing HR account.
     *
     * SUPER_ADMIN can modify:
     * - Name
     * - Email
     * - Organization
     *
     * Password is NOT updated here.
     */
    public User updateHr(
            Long id,
            UpdateHrRequest request) {

        /*
         * STEP 1:
         * Find the HR.
         */
        User hr = getHrById(id);

        /*
         * STEP 2:
         * Normalize the email.
         */
        String email = request.getEmail()
                .trim()
                .toLowerCase();

        /*
         * STEP 3:
         * Check duplicate email.
         *
         * Allow the HR to keep their own email.
         */
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Email already exists"
                    );
                });

        /*
         * STEP 4:
         * Find the selected organization.
         */
        Organization organization =
                organizationRepository.findById(
                        request.getOrganizationId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"
                        )
                );

        /*
         * STEP 5:
         * Update fields.
         */
        hr.setName(request.getName().trim());
        hr.setEmail(email);
        hr.setOrganization(organization);

        /*
         * STEP 6:
         * Save changes.
         */
        return userRepository.save(hr);
    }
    
    /*
     * UPDATE HR STATUS
     *
     * Activates or deactivates an HR account.
     *
     * Valid status values:
     * - ACTIVE
     * - INACTIVE
     */
    public User updateHrStatus(Long id, String status) {

        /*
         * Find the HR by ID.
         *
         * Also ensure that the user actually has the HR role.
         */
        User hr = userRepository
                .findById(id)
                .filter(user -> "HR".equals(user.getRole().getName()))
                .orElseThrow(() ->
                        new ResourceNotFoundException("HR not found")
                );

        /*
         * Update the status.
         */
        hr.setStatus(status);

        /*
         * Save the updated HR.
         */
        return userRepository.save(hr);
    }
    
}