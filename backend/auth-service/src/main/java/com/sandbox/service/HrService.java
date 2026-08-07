package com.sandbox.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandbox.dto.CreateHrRequest;
import com.sandbox.entity.Organization;
import com.sandbox.entity.Role;
import com.sandbox.entity.User;
import com.sandbox.exception.ResourceNotFoundException;
import com.sandbox.repository.OrganizationRepository;
import com.sandbox.repository.RoleRepository;
import com.sandbox.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.sandbox.dto.UserResponse;

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
	 * - Checking whether an email already exists - Saving the newly created HR
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
	 * Used to find the organization specified by organizationId in CreateHrRequest.
	 *
	 * Every HR must be associated with an organization.
	 */
	private final OrganizationRepository organizationRepository;

	/*
	 * PASSWORD ENCODER
	 *
	 * Used to convert the plain-text password into a BCrypt hash before storing it.
	 *
	 * Plain password:
	 *
	 * Hr@12345
	 *
	 * ↓ BCrypt
	 *
	 * $2a$10$....
	 */
	private final PasswordEncoder passwordEncoder;

	/*
	 * CONSTRUCTOR DEPENDENCY INJECTION
	 *
	 * Spring automatically provides all four dependencies when HrService is
	 * created.
	 */
	public HrService(UserRepository userRepository, RoleRepository roleRepository,
			OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder) {

		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.organizationRepository = organizationRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/*
	 * CREATE HR
	 *
	 * Receives the validated CreateHrRequest from HrController and performs the
	 * complete HR creation process.
	 *
	 * CreateHrRequest contains:
	 *
	 * - name - email - password - organizationId
	 *
	 * Returns:
	 *
	 * The newly created and saved User entity.
	 */
	public User createHr(CreateHrRequest request) {

		/*
		 * STEP 1: NORMALIZE EMAIL
		 *
		 * trim() -> Removes spaces from the beginning/end.
		 *
		 * toLowerCase() -> Converts email to lowercase.
		 *
		 * Example:
		 *
		 * "  Rahul.HR@Acme.com  "
		 *
		 * becomes:
		 *
		 * "rahul.hr@acme.com"
		 */
		String email = request.getEmail().trim().toLowerCase();

		/*
		 * STEP 2: CHECK FOR DUPLICATE EMAIL
		 *
		 * Since User.email is unique, two accounts should not have the same email.
		 */
		if (userRepository.existsByEmail(email)) {

			/*
			 * GlobalExceptionHandler currently handles IllegalArgumentException as:
			 *
			 * HTTP 409 Conflict.
			 */
			throw new IllegalArgumentException("Email already exists");
		}

		/*
		 * STEP 3: FIND ORGANIZATION
		 *
		 * The SUPER_ADMIN supplies organizationId when creating the HR.
		 *
		 * Example request:
		 *
		 * { "name": "Rahul", "email": "rahul@acme.com", "password": "Hr@12345",
		 * "organizationId": 2 }
		 *
		 * We now retrieve organization ID 2.
		 */
		Organization organization = organizationRepository.findById(request.getOrganizationId())

				/*
				 * If the organization does not exist, throw ResourceNotFoundException.
				 *
				 * GlobalExceptionHandler converts this to:
				 *
				 * HTTP 404 Not Found.
				 */
				.orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

		/*
		 * STEP 4: FIND HR ROLE
		 *
		 * Retrieve the Role entity whose name is "HR".
		 *
		 * We don't trust the client to send the role.
		 *
		 * The backend itself decides:
		 *
		 * New user created through this service ↓ role = HR
		 *
		 * This prevents someone from sending something like:
		 *
		 * "role": "SUPER_ADMIN"
		 *
		 * and creating a privileged account.
		 */
		Role hrRole = roleRepository.findByName("HR")

				/*
				 * If the HR role is unexpectedly missing from the roles table, throw an
				 * exception.
				 */
				.orElseThrow(() -> new ResourceNotFoundException("HR role not found"));

		/*
		 * STEP 5: BUILD HR USER
		 *
		 * HR is stored in the same "users" table as SUPER_ADMIN and CANDIDATE.
		 *
		 * What makes this user an HR is:
		 *
		 * role = hrRole
		 */
		User hr = User.builder()

				/*
				 * Remove unnecessary spaces around the name.
				 */
				.name(request.getName().trim())

				/*
				 * Store normalized email.
				 */
				.email(email)

				/*
				 * HASH PASSWORD BEFORE STORAGE
				 *
				 * request.getPassword() ↓ Plain password ↓ passwordEncoder.encode() ↓ BCrypt
				 * hash ↓ password_hash column
				 *
				 * The plain-text password is NEVER stored in the database.
				 */
				.passwordHash(passwordEncoder.encode(request.getPassword()))

				/*
				 * Assign the HR role retrieved from the roles table.
				 *
				 * User.role_id will reference hrRole.id.
				 */
				.role(hrRole)

				/*
				 * Associate this HR with the selected organization.
				 *
				 * User.organization_id will reference organization.id.
				 */
				.organization(organization)

				/*
				 * New HR accounts are ACTIVE by default.
				 */
				.status("ACTIVE")

				/*
				 * Build the User object.
				 *
				 * At this point it exists in Java memory, but has not yet been inserted into
				 * the DB.
				 */
				.build();

		/*
		 * STEP 6: SAVE HR
		 *
		 * save(hr) causes Hibernate/JPA to insert the new user into the users table.
		 *
		 * Before INSERT, User's @PrePersist method runs:
		 *
		 * createdAt = now updatedAt = now
		 *
		 * Database then generates the user's ID.
		 *
		 * The saved User entity is returned.
		 */
		return userRepository.save(hr);
	}

	public List<UserResponse> getAllHrs() {

		return userRepository.findAll().stream()
				.filter(user -> user.getRole() != null && "HR".equalsIgnoreCase(user.getRole().getName()))
				.map(hr -> new UserResponse(hr.getId(), hr.getName(), hr.getEmail(), hr.getRole().getName(),
						hr.getOrganization() != null ? hr.getOrganization().getId() : null, hr.getStatus(),
						hr.getCreatedAt()))
				.collect(Collectors.toList());
	}
}