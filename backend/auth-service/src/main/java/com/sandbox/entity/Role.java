package com.sandbox.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * ROLE ENTITY
 *
 * Purpose:
 * This class represents a user role in the application
 * and maps directly to the "roles" table in the database.
 *
 * Current roles in our system:
 *
 * SUPER_ADMIN
 * HR
 * CANDIDATE
 *
 * Roles are used for Role-Based Access Control (RBAC).
 *
 * Example:
 *
 * SUPER_ADMIN -> manages organizations and HRs
 * HR          -> manages candidates
 * CANDIDATE   -> candidate-level functionality
 */


/*
 * @Entity
 *
 * Marks this class as a JPA entity.
 *
 * Hibernate/JPA will treat objects of this class
 * as records stored in the database.
 */
@Entity


/*
 * @Table(name = "roles")
 *
 * Maps this Java class to the "roles" table.
 *
 * Role.java
 *     ↓
 * roles table
 */
@Table(name = "roles")


/*
 * LOMBOK ANNOTATIONS
 *
 * @Getter
 * -> Generates getter methods automatically.
 *
 * @Setter
 * -> Generates setter methods automatically.
 *
 * @NoArgsConstructor
 * -> Generates an empty constructor.
 *    JPA requires a no-argument constructor.
 *
 * @AllArgsConstructor
 * -> Generates a constructor containing all fields.
 *
 * @Builder
 * -> Enables the Builder Pattern.
 *
 * Example:
 *
 * Role role = Role.builder()
 *         .name("HR")
 *         .build();
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    /*
     * ROLE ID
     *
     * @Id marks this field as the primary key
     * of the roles table.
     */
    @Id

    /*
     * @GeneratedValue with IDENTITY means the database
     * automatically generates the role ID.
     *
     * Example:
     *
     * 1 -> SUPER_ADMIN
     * 2 -> HR
     * 3 -> CANDIDATE
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * ROLE NAME
     *
     * Stores the name of the role.
     *
     * nullable = false
     * -> Every role must have a name.
     *
     * unique = true
     * -> Duplicate role names are not allowed.
     *
     * Therefore, we cannot have:
     *
     * 1 -> HR
     * 2 -> HR
     *
     * length = 50
     * -> Maximum database column length is 50 characters.
     */
    @Column(
        nullable = false,
        unique = true,
        length = 50
    )
    private String name;
}