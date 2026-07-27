package com.sandbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * AUTH RESPONSE DTO
 *
 * Purpose:
 * This DTO defines the data that is returned to the client
 * after a user successfully logs in.
 *
 * DTO = Data Transfer Object
 *
 * Instead of returning the complete User entity, we return
 * only the information required by the frontend/client.
 *
 * This is especially important because the User entity contains
 * sensitive information such as passwordHash, which should
 * NEVER be included in the login response.
 */


/*
 * @Getter is provided by Lombok.
 *
 * Lombok automatically generates getter methods for every field.
 *
 * For example:
 *
 * getToken()
 * getTokenType()
 * getUserId()
 * getName()
 * getEmail()
 * getRole()
 * getOrganizationId()
 *
 * Therefore, we don't need to manually write those methods.
 */
@Getter

/*
 * @AllArgsConstructor is also provided by Lombok.
 *
 * It automatically generates a constructor containing
 * all fields of this class.
 *
 * This allows AuthService to easily create the complete
 * login response in one constructor call.
 */
@AllArgsConstructor
public class AuthResponse {

    /*
     * JWT ACCESS TOKEN
     *
     * Contains the JWT generated after successful authentication.
     *
     * The client stores this token and sends it with future
     * protected API requests.
     *
     * Example:
     *
     * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     */
    private String token;


    /*
     * TOKEN TYPE
     *
     * Normally this will contain:
     *
     * "Bearer"
     *
     * It tells the client how the JWT should be sent
     * in the Authorization header.
     */
    private String tokenType;


    /*
     * ID of the successfully authenticated user.
     *
     * Example:
     * Super Admin -> 1
     * HR          -> 2
     * Candidate   -> 3
     */
    private Long userId;


    /*
     * Name of the logged-in user.
     */
    private String name;


    /*
     * Email address of the logged-in user.
     *
     * This is also currently used as the login identifier.
     */
    private String email;


    /*
     * Role of the logged-in user.
     *
     * Examples:
     *
     * SUPER_ADMIN
     * HR
     * CANDIDATE
     *
     * The frontend can use this information to decide
     * which dashboard/features should be displayed.
     *
     * Actual backend authorization is still enforced by
     * Spring Security and must not rely only on the frontend.
     */
    private String role;


    /*
     * Organization ID associated with the logged-in user.
     *
     * Example:
     *
     * HR:
     * organizationId = 2
     *
     * Candidate:
     * organizationId = 2
     *
     * SUPER_ADMIN:
     * organizationId = null
     *
     * because SUPER_ADMIN is not tied to one organization.
     */
    private Long organizationId;
}