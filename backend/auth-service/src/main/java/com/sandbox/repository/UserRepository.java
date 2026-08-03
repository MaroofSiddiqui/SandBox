package com.sandbox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sandbox.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Used by CandidateService to get candidates of an organization
    List<User> findByOrganizationIdAndRoleName(
            Long organizationId,
            String roleName
    );

    // Used to get all users belonging to an organization
    List<User> findByOrganizationId(Long organizationId);

    /*
     * Used by HrService to retrieve all HR users
     * across all organizations.
     */
    List<User> findByRoleName(String roleName);
    
    // Used to get a specific candidate from the HR's organization
    Optional<User> findByIdAndOrganizationIdAndRoleName(
            Long id,
            Long organizationId,
            String roleName
    );

    // Loads role + organization for JWT authentication
    @Query("""
           SELECT u
           FROM User u
           LEFT JOIN FETCH u.role
           LEFT JOIN FETCH u.organization
           WHERE u.email = :email
           """)
    Optional<User> findForAuthentication(@Param("email") String email);
}