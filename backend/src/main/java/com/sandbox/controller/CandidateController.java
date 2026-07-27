package com.sandbox.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.CandidateRequest;
import com.sandbox.dto.CandidateResponse;
import com.sandbox.entity.User;
import com.sandbox.service.CandidateService;

import jakarta.validation.Valid;

/*
 * CANDIDATE CONTROLLER
 *
 * Purpose:
 * This controller handles candidate-related HTTP requests.
 *
 * Currently it provides:
 *
 * 1. POST /candidates
 *    -> HR creates a new candidate.
 *
 * 2. GET /candidates
 *    -> HR gets all candidates belonging to their organization.
 *
 * 3. GET /candidates/{id}
 *    -> HR gets a particular candidate by ID.
 *
 * SecurityConfig allows /candidates/** only for users
 * having the HR role.
 *
 * This controller handles HTTP requests/responses,
 * while the actual business logic is delegated to CandidateService.
 */
@RestController
@RequestMapping("/candidates")
public class CandidateController {

    /*
     * CandidateService contains the business logic for candidates.
     *
     * Examples:
     * - Creating candidates
     * - Assigning candidate role
     * - Assigning HR's organization
     * - Checking duplicate email
     * - Fetching candidates
     * - Enforcing organization-level isolation
     */
    private final CandidateService candidateService;


    /*
     * Constructor Dependency Injection.
     *
     * Spring automatically injects CandidateService
     * when it creates this controller.
     */
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }


    /*
     * CREATE CANDIDATE
     *
     * URL:
     * POST /candidates
     *
     * Accessible only by HR because SecurityConfig contains:
     *
     * .requestMatchers("/candidates/**")
     * .hasRole("HR")
     *
     * Example request:
     *
     * {
     *     "name": "Aman Verma",
     *     "email": "aman.verma@acme.com",
     *     "password": "Candidate@123"
     * }
     *
     * Notice that organizationId is NOT supplied by the client.
     * The candidate's organization is taken from the logged-in HR.
     */
    @PostMapping
    public ResponseEntity<CandidateResponse> createEmployee(

            /*
             * @Valid:
             * Runs validation rules defined in CandidateRequest.
             *
             * @RequestBody:
             * Converts incoming JSON into CandidateRequest.
             */
            @Valid @RequestBody CandidateRequest request,

            /*
             * Authentication contains information about
             * the currently logged-in HR.
             *
             * It was populated earlier by JwtAuthenticationFilter.
             */
            Authentication authentication) {

        /*
         * getPrincipal() gives us the currently authenticated user.
         *
         * Since only HR can access this endpoint,
         * currentHr represents the HR creating the candidate.
         */
        User currentHr =
                (User) authentication.getPrincipal();


        /*
         * Pass both:
         *
         * 1. Candidate request data
         * 2. Logged-in HR
         *
         * to the service layer.
         *
         * The service uses currentHr to automatically determine
         * which organization the candidate should belong to.
         */
        CandidateResponse employee =
                candidateService.createEmployee(
                        request,
                        currentHr
                );


        /*
         * HTTP 201 Created is returned because
         * a new candidate resource was successfully created.
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employee);
    }


    /*
     * GET CANDIDATE BY ID
     *
     * URL example:
     * GET /candidates/3
     *
     * {id} is a path variable representing
     * the candidate's database ID.
     *
     * Important security rule:
     * HR should only be able to retrieve candidates
     * belonging to their own organization.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> getCandidateById(

            /*
             * @PathVariable extracts the ID from the URL.
             *
             * Example:
             *
             * /candidates/3
             *             ↓
             *           id = 3
             */
            @PathVariable Long id,

            Authentication authentication) {

        // Get the currently authenticated HR.
        User currentHr =
                (User) authentication.getPrincipal();


        /*
         * CandidateService checks:
         *
         * - Candidate ID matches
         * - Candidate role is CANDIDATE
         * - Candidate belongs to current HR's organization
         *
         * If no matching candidate exists,
         * ResourceNotFoundException is thrown.
         */
        CandidateResponse candidate =
                candidateService.getCandidateById(
                        id,
                        currentHr
                );


        // Successfully found candidate -> HTTP 200 OK.
        return ResponseEntity.ok(candidate);
    }


    /*
     * GET ALL CANDIDATES
     *
     * URL:
     * GET /candidates
     *
     * Returns candidates belonging ONLY to
     * the currently logged-in HR's organization.
     *
     * It does NOT return candidates from other organizations.
     */
    @GetMapping
    public ResponseEntity<List<CandidateResponse>> getCandidates(
            Authentication authentication) {

        // Identify the HR making the request.
        User currentHr =
                (User) authentication.getPrincipal();


        /*
         * CandidateService uses the HR's organization ID
         * to retrieve only users having:
         *
         * organization = currentHr's organization
         * role         = CANDIDATE
         */
        List<CandidateResponse> candidates =
                candidateService.getCandidates(currentHr);


        /*
         * ResponseEntity.ok() returns:
         *
         * HTTP 200 OK
         *
         * along with the list of candidates.
         */
        return ResponseEntity.ok(candidates);
    }
}