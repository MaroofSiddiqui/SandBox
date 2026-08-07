package com.sandbox.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

import com.sandbox.entity.Role;
import com.sandbox.entity.Organization;

/*
 * USER ENTITY
 *
 * Purpose:
 * This class represents a user in the application and maps
 * directly to the "users" table in the database.
 *
 * All types of users are stored in this same table:
 *
 * SUPER_ADMIN
 * HR
 * CANDIDATE
 *
 * Their permissions and responsibilities are differentiated
 * using the Role assigned to each user.
 */
@Entity

/*
 * Maps this entity to the "users" database table.
 */
@Table(name = "users")

/*
 * LOMBOK ANNOTATIONS
 *
 * @Getter -> Automatically generates getters for all fields.
 *
 * @Setter -> Automatically generates setters for all fields.
 *
 * @NoArgsConstructor -> Generates an empty constructor. JPA requires a
 * no-argument constructor.
 *
 * @AllArgsConstructor -> Generates a constructor containing all fields.
 *
 * @Builder -> Allows User objects to be created using Builder Pattern.
 *
 * Example:
 *
 * User user = User.builder() .name("Rahul Sharma") .email("rahul@acme.com")
 * .role(role) .build();
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	/*
	 * USER ID / PRIMARY KEY
	 *
	 * @Id marks this as the primary key.
	 *
	 * GenerationType.IDENTITY means the database automatically generates the ID,
	 * usually using AUTO_INCREMENT in MySQL.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/*
	 * USER NAME
	 *
	 * Cannot be NULL and can contain at most 100 characters.
	 */
	@Column(nullable = false, length = 100)
	private String name;

	/*
	 * USER EMAIL
	 *
	 * Used as the unique login identifier.
	 *
	 * nullable = false -> Every user must have an email.
	 *
	 * unique = true -> Two users cannot register with the same email.
	 */
	@Column(nullable = false, unique = true, length = 150)
	private String email;

	/*
	 * PASSWORD HASH
	 *
	 * Maps to the database column:
	 *
	 * password_hash
	 *
	 * We NEVER store the user's plain-text password.
	 *
	 * Example:
	 *
	 * "Hr@12345" ↓ BCryptPasswordEncoder ↓ "$2a$10$..." ↓ Stored in password_hash
	 */
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	/*
	 * USER -> ROLE RELATIONSHIP
	 *
	 * Many users can have the same role.
	 *
	 * Example:
	 *
	 * Rahul ─────┐ Aman HR ───┼──> HR role Sara HR ───┘
	 *
	 * Therefore this is a Many-to-One relationship.
	 *
	 * users.role_id is a foreign key referring to roles.id.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	/*
	 * FetchType.EAGER means the Role is loaded immediately whenever the User is
	 * loaded.
	 *
	 * This is useful because authentication/security frequently needs the user's
	 * role immediately.
	 *
	 * Example:
	 *
	 * User loaded ↓ Role also loaded ↓ user.getRole().getName()
	 */

	/*
	 * USER -> ORGANIZATION RELATIONSHIP
	 *
	 * Many users can belong to the same organization.
	 *
	 * Example:
	 *
	 * Organization: Acme
	 *
	 * ↑ ┌────┼──────┐ HR Candidate Candidate
	 *
	 * Therefore this is also Many-to-One.
	 *
	 * users.organization_id is a foreign key referring to organizations.id.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	/*
	 * FetchType.LAZY means Organization is not necessarily loaded immediately with
	 * the User.
	 *
	 * Hibernate loads it when the organization is actually needed.
	 *
	 * This can avoid unnecessary database work.
	 *
	 * Notice organization_id is allowed to be NULL.
	 *
	 * This is intentional because:
	 *
	 * SUPER_ADMIN -> organization = null
	 *
	 * HR -> belongs to an organization CANDIDATE -> belongs to an organization
	 */

	/*
	 * USER ACCOUNT STATUS
	 *
	 * Example values:
	 *
	 * ACTIVE INACTIVE
	 *
	 * This can later be used to disable a user's account without deleting the user
	 * from the database.
	 */
	@Column(nullable = false, length = 20)
	private String status;

	/*
	 * CREATED AT
	 *
	 * Stores when the user account was originally created.
	 *
	 * updatable = false means Hibernate should not change this value when the user
	 * is later updated.
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/*
	 * UPDATED AT
	 *
	 * Stores when the user was most recently updated.
	 *
	 * Unlike createdAt, this value changes whenever the User entity is updated.
	 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/*
	 * @PrePersist
	 *
	 * JPA lifecycle callback that runs automatically BEFORE a new User is inserted
	 * into the database.
	 *
	 * Example:
	 *
	 * userRepository.save(user) ↓
	 * 
	 * @PrePersist ↓ onCreate() ↓ INSERT INTO users
	 */
	@PrePersist
	protected void onCreate() {

		/*
		 * If no status was explicitly provided, make the new account ACTIVE by default.
		 */
		if (status == null) {
			status = "ACTIVE";
		}

		/*
		 * When the user is first created:
		 *
		 * createdAt = current time updatedAt = current time
		 */
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@Column(name = "failed_login_attempts", nullable = false)
	@Builder.Default
	private Integer failedLoginAttempts = 0;

	/*
	 * EMAIL VERIFICATION STATUS
	 *
	 * Indicates whether the user has successfully verified their email address.
	 *
	 * false -> email not verified true -> email verified
	 */
	@Column(name = "email_verified", nullable = false)
	@Builder.Default
	private boolean emailVerified = false;

	/*
	 * @PreUpdate
	 *
	 * JPA lifecycle callback that runs automatically BEFORE an existing User is
	 * updated in the database.
	 *
	 * It keeps updatedAt synchronized with the most recent modification time.
	 *
	 * Example:
	 *
	 * Change status ↓ repository.save(user) ↓
	 * 
	 * @PreUpdate ↓ updatedAt = current time ↓ UPDATE users ...
	 */
	@PreUpdate
	protected void onUpdate() {

		updatedAt = LocalDateTime.now();
	}
}