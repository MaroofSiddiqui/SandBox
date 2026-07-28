package com.sandbox.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.OrganizationRequest;
import com.sandbox.dto.OrganizationStatusRequest;
import com.sandbox.entity.Organization;
import com.sandbox.entity.User;
import com.sandbox.service.OrganizationService;

import jakarta.validation.Valid;

/*
 * ORGANIZATION CONTROLLER
 *
 * Purpose:
 * Handles all HTTP/API requests related to organization management.
 *
 * Current APIs:
 *
 * POST   /organizations
 *        -> Create an organization
 *
 * GET    /organizations
 *        -> Get all organizations
 *
 * GET    /organizations/{id}
 *        -> Get a specific organization
 *
 * PUT    /organizations/{id}
 *        -> Update an organization
 *
 * DELETE /organizations/{id}
 *        -> Delete an organization
 *
 * PATCH  /organizations/{id}/status
 *        -> Activate/deactivate an organization
 *
 * According to SecurityConfig, /organizations/** is accessible
 * only to users having the SUPER_ADMIN role.
 */
@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    /*
     * OrganizationService contains the actual business logic.
     *
     * The controller should mainly:
     * - Receive HTTP requests
     * - Validate request data
     * - Call the service layer
     * - Return HTTP responses
     */
    private final OrganizationService organizationService;


    /*
     * Constructor Dependency Injection.
     *
     * Spring automatically injects OrganizationService
     * when this controller is created.
     */
    public OrganizationController(
            OrganizationService organizationService) {

        this.organizationService = organizationService;
    }


    /*
     * CREATE ORGANIZATION
     *
     * URL:
     * POST /organizations
     *
     * Example request:
     *
     * {
     *     "name": "Acme Technologies",
     *     "domain": "acme.com"
     * }
     *
     * Only SUPER_ADMIN can access this API.
     */
    @PostMapping
    public ResponseEntity<Organization> createOrganization(

            /*
             * @Valid runs the validation rules defined
             * inside OrganizationRequest.
             *
             * @RequestBody converts incoming JSON
             * into an OrganizationRequest object.
             */
            @Valid @RequestBody OrganizationRequest request,

            /*
             * Authentication contains information about
             * the currently logged-in user.
             *
             * It was populated by JwtAuthenticationFilter.
             */
            Authentication authentication) {

        /*
         * Get the currently authenticated SUPER_ADMIN.
         *
         * We need this because the organization stores
         * createdBy, which should come from the authenticated
         * user instead of being supplied by the frontend.
         */
        User currentUser =
                (User) authentication.getPrincipal();


        /*
         * Delegate organization creation to the service layer.
         */
        Organization organization =
                organizationService.createOrganization(
                        request,
                        currentUser
                );


        /*
         * HTTP 201 Created is returned because
         * a new organization has been created.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(organization);
    }


    /*
     * GET ALL ORGANIZATIONS
     *
     * URL:
     * GET /organizations
     *
     * Returns all organizations available in the database.
     */
    @GetMapping
    public ResponseEntity<List<Organization>> getAllOrganizations() {

        /*
         * Service retrieves organizations from the repository/database.
         *
         * ResponseEntity.ok() returns HTTP 200 OK.
         */
        return ResponseEntity.ok(
                organizationService.getAllOrganizations()
        );
    }


    /*
     * GET ORGANIZATION BY ID
     *
     * Example:
     * GET /organizations/2
     *
     * {id} represents the organization's database ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(

            /*
             * @PathVariable extracts the value from the URL.
             *
             * Example:
             *
             * /organizations/2
             *                ↓
             *              id = 2
             */
            @PathVariable Long id) {

        /*
         * If the organization exists:
         *      -> 200 OK
         *
         * If it does not exist:
         *      OrganizationService throws ResourceNotFoundException
         *      -> GlobalExceptionHandler converts it to 404 Not Found.
         */
        return ResponseEntity.ok(
                organizationService.getOrganizationById(id)
        );
    }


    /*
     * UPDATE ORGANIZATION
     *
     * URL:
     * PUT /organizations/{id}
     *
     * Example:
     * PUT /organizations/2
     *
     * Request:
     *
     * {
     *     "name": "Acme Technologies Updated",
     *     "domain": "acme.com"
     * }
     *
     * PUT is used here to update the organization's
     * main information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(

            // ID of the organization that should be updated.
            @PathVariable Long id,

            /*
             * Validate the incoming organization data
             * before passing it to the service.
             */
            @Valid @RequestBody OrganizationRequest request) {

        /*
         * Service finds the organization, performs checks
         * such as duplicate-domain validation, updates it,
         * and saves the changes.
         */
        return ResponseEntity.ok(
                organizationService.updateOrganization(
                        id,
                        request
                )
        );
    }


    /*
     * DELETE ORGANIZATION
     *
     * URL:
     * DELETE /organizations/{id}
     *
     * Example:
     * DELETE /organizations/2
     *
     * Removes the organization from the database.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(
            @PathVariable Long id) {

        /*
         * Service first verifies that the organization exists,
         * then deletes it.
         *
         * If it doesn't exist, ResourceNotFoundException
         * results in HTTP 404.
         */
        organizationService.deleteOrganization(id);


        /*
         * Return a simple success message after deletion.
         */
        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        "Organization deleted successfully"
                )
        );
    }


    /*
     * UPDATE ORGANIZATION STATUS
     *
     * URL:
     * PATCH /organizations/{id}/status
     *
     * Example:
     * PATCH /organizations/2/status
     *
     * Request:
     *
     * {
     *     "status": "INACTIVE"
     * }
     *
     * PATCH is appropriate because we are modifying
     * only one specific property: status.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Organization> updateOrganizationStatus(

            // Organization whose status should be changed.
            @PathVariable Long id,

            /*
             * OrganizationStatusRequest validates the status.
             *
             * Currently valid values are:
             * ACTIVE
             * INACTIVE
             */
            @Valid @RequestBody OrganizationStatusRequest request) {

        /*
         * Pass the organization ID and requested status
         * to the service layer.
         */
        Organization organization =
                organizationService.updateOrganizationStatus(
                        id,
                        request.getStatus()
                );


        // Successful status update -> HTTP 200 OK.
        return ResponseEntity.ok(organization);
    }
}