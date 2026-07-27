package com.sandbox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandbox.entity.User;

/*
 * USER REPOSITORY
 *
 * Purpose:
 * This interface is the database-access layer for the User entity.
 *
 * Since SUPER_ADMIN, HR, and CANDIDATE are all stored in the
 * "users" table, this repository is used for database operations
 * involving all types of users.
 *
 * It is used for:
 *
 * - Finding users during login
 * - Checking duplicate emails
 * - Finding users of an organization
 * - Finding candidates belonging to an HR's organization
 * - Finding a particular candidate securely
 *
 * Flow:
 *
 * Service
 *    ↓
 * UserRepository
 *    ↓
 * Spring Data JPA / Hibernate
 *    ↓
 * users table
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * JpaRepository<User, Long>
     *
     * User:
     * -> Entity managed by this repository.
     *
     * Long:
     * -> Datatype of User's primary key (id).
     *
     * JpaRepository automatically provides methods such as:
     *
     * save(user)
     * findById(id)
     * findAll()
     * delete(user)
     * deleteById(id)
     * existsById(id)
     * count()
     *
     * Therefore we don't need to implement basic CRUD operations.
     */


    /*
     * FIND USER BY EMAIL
     *
     * Searches for a user using their email address.
     *
     * Mainly useful during LOGIN.
     *
     * Example:
     *
     * userRepository.findByEmail("admin@sandbox.com");
     *
     * Spring Data JPA automatically derives a query similar to:
     *
     * SELECT *
     * FROM users
     * WHERE email = ?;
     *
     * Optional<User> is returned because the email
     * may or may not exist.
     */
    Optional<User> findByEmail(String email);


    /*
     * CHECK WHETHER EMAIL ALREADY EXISTS
     *
     * Returns:
     *
     * true  -> Email already exists
     * false -> Email is available
     *
     * This is useful before creating HRs or candidates.
     *
     * Example:
     *
     * if (userRepository.existsByEmail(email)) {
     *     throw new IllegalArgumentException(
     *         "Email already exists"
     *     );
     * }
     *
     * This prevents duplicate user accounts.
     */
    boolean existsByEmail(String email);


    /*
     * FIND ALL USERS OF AN ORGANIZATION
     *
     * Returns every user whose organization.id matches
     * the supplied organizationId.
     *
     * Example:
     *
     * findByOrganizationId(2L)
     *
     * roughly means:
     *
     * SELECT *
     * FROM users
     * WHERE organization_id = 2;
     *
     * IMPORTANT:
     * This can return different roles belonging to that organization,
     * such as HR and CANDIDATE.
     *
     * It does NOT filter by role.
     */
    List<User> findByOrganizationId(Long organizationId);


    /*
     * FIND USERS BY ORGANIZATION + ROLE
     *
     * This method filters users using TWO conditions:
     *
     * 1. organization.id
     * 2. role.name
     *
     * Example:
     *
     * findByOrganizationIdAndRoleName(
     *     2L,
     *     "CANDIDATE"
     * );
     *
     * This means:
     *
     * "Give me all CANDIDATE users belonging
     *  to organization 2."
     *
     * Spring Data JPA understands nested entity properties.
     *
     * OrganizationId
     *       ↓
     * user.organization.id
     *
     * RoleName
     *       ↓
     * user.role.name
     *
     * Query is conceptually similar to:
     *
     * SELECT u.*
     * FROM users u
     * JOIN roles r ON u.role_id = r.id
     * WHERE u.organization_id = ?
     * AND r.name = ?;
     *
     * This is used when an HR requests the list
     * of candidates belonging to their organization.
     */
    List<User> findByOrganizationIdAndRoleName(
            Long organizationId,
            String roleName
    );


    /*
     * FIND ONE USER BY:
     *
     * 1. User ID
     * 2. Organization ID
     * 3. Role name
     *
     * All THREE conditions must match.
     *
     * Example:
     *
     * findByIdAndOrganizationIdAndRoleName(
     *     5L,
     *     2L,
     *     "CANDIDATE"
     * );
     *
     * Means:
     *
     * Find user ID 5
     * AND organization ID must be 2
     * AND role must be CANDIDATE.
     *
     * If all conditions match:
     * -> Optional contains User
     *
     * Otherwise:
     * -> Optional.empty()
     *
     * This is especially important for organization isolation.
     */
    Optional<User> findByIdAndOrganizationIdAndRoleName(
            Long id,
            Long organizationId,
            String roleName
    );
}