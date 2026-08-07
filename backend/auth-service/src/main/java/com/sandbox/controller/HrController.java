package com.sandbox.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.CreateHrRequest;
import com.sandbox.dto.UserResponse;
import com.sandbox.entity.User;
import com.sandbox.service.HrService;
import java.util.List;
import jakarta.validation.Valid;

/*
 * HR CONTROLLER
 *
 * Purpose:
 * This controller handles HTTP requests related to HR management.
 *
 * Currently it provides:
 *
 * POST /hrs
 * -> Creates a new HR user.
 *
 * According to SecurityConfig, /hrs/** can only be accessed
 * by a user having the SUPER_ADMIN role.
 *
 * Flow:
 * SUPER_ADMIN
 *      ↓
 * POST /hrs
 *      ↓
 * HrController
 *      ↓
 * HrService
 *      ↓
 * User saved with HR role + assigned organization
 */
@RestController
@RequestMapping("/hrs")
public class HrController {

	/*
	 * HrService contains the actual business logic required for creating an HR.
	 *
	 * For example: - Checking duplicate email - Finding the organization - Finding
	 * the HR role - Encoding the password - Saving the HR into the database
	 */
	private final HrService hrService;

	/*
	 * Constructor Dependency Injection.
	 *
	 * Spring automatically provides the HrService when this controller is created.
	 */
	public HrController(HrService hrService) {
		this.hrService = hrService;
	}

	/*
	 * CREATE HR ENDPOINT
	 *
	 * URL: POST /hrs
	 *
	 * Example request:
	 *
	 * { "name": "Rahul Sharma", "email": "rahul.hr@acme.com", "password":
	 * "Hr@12345", "organizationId": 2 }
	 *
	 * Only SUPER_ADMIN can access this endpoint because SecurityConfig contains:
	 *
	 * .requestMatchers("/organizations/**", "/hrs/**") .hasRole("SUPER_ADMIN")
	 */
	@PostMapping
	public ResponseEntity<UserResponse> createHr(

			/*
			 * @RequestBody: Converts incoming JSON into a CreateHrRequest object.
			 *
			 * @Valid: Executes validation annotations defined inside CreateHrRequest, such
			 * as:
			 *
			 * @NotBlank
			 * 
			 * @Email
			 * 
			 * @Size
			 * 
			 * @NotNull
			 */
			@Valid @RequestBody CreateHrRequest request) {

		/*
		 * Delegate the actual HR creation logic to HrService.
		 *
		 * HrService will:
		 *
		 * 1. Check whether email already exists 2. Find the requested organization 3.
		 * Find the HR role 4. Hash the password 5. Create the User 6. Save it in the
		 * database
		 *
		 * The returned User represents the newly created HR.
		 */
		User hr = hrService.createHr(request);

		/*
		 * Convert the User entity into UserResponse DTO.
		 *
		 * We should NOT return the complete User entity because User contains sensitive
		 * information such as:
		 *
		 * passwordHash
		 *
		 * UserResponse exposes only safe information.
		 */
		UserResponse response = new UserResponse(

				// Database-generated user ID.
				hr.getId(),

				// HR's name.
				hr.getName(),

				// HR's email.
				hr.getEmail(),

				// Convert Role entity into simple role name: "HR".
				hr.getRole().getName(),

				// Return only the organization's ID.
				hr.getOrganization().getId(),

				// Example: ACTIVE.
				hr.getStatus(),

				// Date/time when the HR account was created.
				hr.getCreatedAt());

		/*
		 * HTTP 201 Created is appropriate because a new HR resource was successfully
		 * created.
		 *
		 * Response body contains UserResponse, not the User entity.
		 */
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllHrs() {

		return ResponseEntity.ok(hrService.getAllHrs());
	}
}