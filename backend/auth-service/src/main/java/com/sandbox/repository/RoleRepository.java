package com.sandbox.repository;

import com.sandbox.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * ROLE REPOSITORY
 *
 * Purpose:
 * This interface is the database-access layer for
 * the Role entity.
 *
 * It allows the service/configuration layers to interact
 * with the "roles" table without manually writing SQL.
 *
 * Examples of roles stored in the table:
 *
 * SUPER_ADMIN
 * HR
 * CANDIDATE
 *
 * Flow:
 *
 * Service / DataInitializer
 *          ↓
 * RoleRepository
 *          ↓
 * Spring Data JPA / Hibernate
 *          ↓
 * roles table
 */
public interface RoleRepository
        extends JpaRepository<Role, Long> {

    /*
     * JpaRepository<Role, Long>
     *
     * Role:
     * -> The entity managed by this repository.
     *
     * Long:
     * -> The datatype of the Role entity's primary key (id).
     *
     * By extending JpaRepository, we automatically receive
     * standard CRUD methods such as:
     *
     * save(role)
     * findById(id)
     * findAll()
     * delete(role)
     * deleteById(id)
     * existsById(id)
     * count()
     *
     * Spring automatically creates the implementation
     * of this repository at runtime.
     */


    /*
     * FIND ROLE BY NAME
     *
     * Searches the roles table using the role name.
     *
     * Example:
     *
     * roleRepository.findByName("HR");
     *
     * Spring Data JPA understands the method name
     * and automatically generates a query similar to:
     *
     * SELECT *
     * FROM roles
     * WHERE name = 'HR';
     *
     * Therefore, we don't need to manually write SQL
     * or JPQL for this operation.
     */
    Optional<Role> findByName(String name);

    /*
     * WHY Optional<Role>?
     *
     * The requested role may or may not exist.
     *
     * If found:
     * Optional contains the Role object.
     *
     * If not found:
     * Optional.empty()
     *
     * This allows code such as:
     *
     * roleRepository.findByName("CANDIDATE")
     *         .orElseThrow(() ->
     *             new IllegalArgumentException(
     *                 "CANDIDATE role not found"
     *             )
     *         );
     */
}