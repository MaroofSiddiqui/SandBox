package com.sandbox.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.CreateHrRequest;
import com.sandbox.dto.HrStatusRequest;
import com.sandbox.dto.UpdateHrRequest;
import com.sandbox.dto.UserResponse;
import com.sandbox.entity.User;
import com.sandbox.service.HrService;

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
     * HrService contains the actual business logic
     * required for creating an HR.
     *
     * For example:
     * - Checking duplicate email
     * - Finding the organization
     * - Finding the HR role
     * - Encoding the password
     * - Saving the HR into the database
     */
    private final HrService hrService;


    /*
     * Constructor Dependency Injection.
     *
     * Spring automatically provides the HrService
     * when this controller is created.
     */
    public HrController(HrService hrService) {
        this.hrService = hrService;
    }


    /*
     * CREATE HR ENDPOINT
     *
     * URL:
     * POST /hrs
     *
     * Example request:
     *
     * {
     *     "name": "Rahul Sharma",
     *     "email": "rahul.hr@acme.com",
     *     "password": "Hr@12345",
     *     "organizationId": 2
     * }
     *
     * Only SUPER_ADMIN can access this endpoint because
     * SecurityConfig contains:
     *
     * .requestMatchers("/organizations/**", "/hrs/**")
     * .hasRole("SUPER_ADMIN")
     */
    @PostMapping
    public ResponseEntity<UserResponse> createHr(

            /*
             * @RequestBody:
             * Converts incoming JSON into a CreateHrRequest object.
             *
             * @Valid:
             * Executes validation annotations defined inside
             * CreateHrRequest, such as:
             *
             * @NotBlank
             * @Email
             * @Size
             * @NotNull
             */
            @Valid @RequestBody CreateHrRequest request) {

        /*
         * Delegate the actual HR creation logic to HrService.
         *
         * HrService will:
         *
         * 1. Check whether email already exists
         * 2. Find the requested organization
         * 3. Find the HR role
         * 4. Hash the password
         * 5. Create the User
         * 6. Save it in the database
         *
         * The returned User represents the newly created HR.
         */
        User hr = hrService.createHr(request);


        /*
         * Convert the User entity into UserResponse DTO.
         *
         * We should NOT return the complete User entity
         * because User contains sensitive information such as:
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
                hr.getCreatedAt()
        );


        /*
         * HTTP 201 Created is appropriate because
         * a new HR resource was successfully created.
         *
         * Response body contains UserResponse,
         * not the User entity.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    /*
     * GET ALL HRS
     *
     * URL:
     * GET /hrs
     *
     * Accessible only by SUPER_ADMIN.
     *
     * Returns safe UserResponse DTOs instead of
     * exposing User entities.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllHrs() {

        List<User> hrs = hrService.getAllHrs();

        List<UserResponse> response = hrs.stream()
                .map(hr -> new UserResponse(
                        hr.getId(),
                        hr.getName(),
                        hr.getEmail(),
                        hr.getRole().getName(),
                        hr.getOrganization() != null
                                ? hr.getOrganization().getId()
                                : null,
                        hr.getStatus(),
                        hr.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
    
    /*
     * GET HR BY ID
     *
     * URL:
     * GET /hrs/{id}
     *
     * Example:
     * GET /hrs/2
     *
     * Accessible only by SUPER_ADMIN.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getHrById(
            @PathVariable Long id) {

        User hr = hrService.getHrById(id);

        UserResponse response = new UserResponse(
                hr.getId(),
                hr.getName(),
                hr.getEmail(),
                hr.getRole().getName(),
                hr.getOrganization() != null
                        ? hr.getOrganization().getId()
                        : null,
                hr.getStatus(),
                hr.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
    
    /*
     * UPDATE HR
     *
     * URL:
     * PUT /hrs/{id}
     *
     * Example:
     *
     * PUT /hrs/2
     *
     * {
     *     "name": "Rahul Sharma Updated",
     *     "email": "rahul.hr@acme.com",
     *     "organizationId": 2
     * }
     *
     * Only SUPER_ADMIN can update HR details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateHr(

            @PathVariable Long id,

            @Valid @RequestBody UpdateHrRequest request) {

        /*
         * Delegate update logic to the service.
         */
        User hr = hrService.updateHr(id, request);

        /*
         * Return only safe information.
         */
        UserResponse response = new UserResponse(

                hr.getId(),

                hr.getName(),

                hr.getEmail(),

                hr.getRole().getName(),

                hr.getOrganization() != null
                        ? hr.getOrganization().getId()
                        : null,

                hr.getStatus(),

                hr.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
    
    /*
     * UPDATE HR STATUS
     *
     * URL:
     * PATCH /hrs/{id}/status
     *
     * Example:
     *
     * PATCH /hrs/2/status
     *
     * Request:
     *
     * {
     *     "status": "INACTIVE"
     * }
     *
     * Only SUPER_ADMIN can access this endpoint.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateHrStatus(

            // HR ID whose status should be updated.
            @PathVariable Long id,

            /*
             * Validate the incoming status.
             *
             * Allowed values:
             * ACTIVE
             * INACTIVE
             */
            @Valid @RequestBody HrStatusRequest request) {

        /*
         * Delegate status update to the service layer.
         */
        User hr = hrService.updateHrStatus(
                id,
                request.getStatus()
        );

        /*
         * Convert the updated User entity into UserResponse.
         */
        UserResponse response = new UserResponse(
                hr.getId(),
                hr.getName(),
                hr.getEmail(),
                hr.getRole().getName(),
                hr.getOrganization().getId(),
                hr.getStatus(),
                hr.getCreatedAt()
        );

        /*
         * Return HTTP 200 OK with the updated HR details.
         */
        return ResponseEntity.ok(response);
    }
}