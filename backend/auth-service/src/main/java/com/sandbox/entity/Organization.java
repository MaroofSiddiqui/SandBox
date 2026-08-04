package com.sandbox.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/*
 * ORGANIZATION ENTITY
 *
 * Purpose:
 * This class represents an organization in the application
 * and maps directly to the "organizations" table in the database.
 *
 * Examples of organizations could be:
 * - Acme Technologies
 * - ABC Pvt Ltd
 * - XYZ Corporation
 *
 * An Entity represents database data, unlike a DTO which is
 * mainly used for transferring data through API requests/responses.
 */

/*
 * @Entity
 *
 * Marks this class as a JPA entity.
 *
 * This tells Hibernate/JPA that objects of this class
 * should be stored in a database table.
 */
@Entity

/*
 * @Table
 *
 * Specifies the database table associated with this entity.
 *
 * Organization class ↓ organizations table
 */
@Table(name = "organizations")

/*
 * LOMBOK ANNOTATIONS
 *
 * @Getter -> Automatically generates getters for all fields.
 *
 * @Setter -> Automatically generates setters for all fields.
 *
 * @NoArgsConstructor -> Generates a constructor with no parameters. JPA
 * requires a no-argument constructor to create entities.
 *
 * @AllArgsConstructor -> Generates a constructor containing all fields.
 *
 * @Builder -> Enables the Builder Pattern.
 *
 * Example:
 *
 * Organization organization = Organization.builder() .name("Acme Technologies")
 * .domain("acme.com") .status("ACTIVE") .build();
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

	/*
	 * PRIMARY KEY
	 *
	 * @Id marks this field as the primary key of the organizations table.
	 */
	@Id

	/*
	 * @GeneratedValue with IDENTITY means the database automatically generates the
	 * ID when a new row is inserted.
	 *
	 * Example:
	 *
	 * First organization -> id = 1 Second organization -> id = 2 Third organization
	 * -> id = 3
	 *
	 * Typically backed by AUTO_INCREMENT in MySQL.
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/*
	 * ORGANIZATION NAME
	 *
	 * @Column configures the corresponding database column.
	 *
	 * nullable = false -> Database does not allow NULL.
	 *
	 * length = 150 -> Maximum column length is 150 characters.
	 */
	@Column(nullable = false, length = 150)
	private String name;

	/*
	 * ORGANIZATION DOMAIN
	 *
	 * Example: acme.com
	 *
	 * unique = true -> Two organizations cannot have the same domain.
	 *
	 * This gives us database-level protection against duplicate organization
	 * domains.
	 *
	 * OrganizationService also checks for duplicate domains before attempting to
	 * save the organization.
	 */
	@Column(unique = true, length = 150)
	private String domain;

	/*
	 * ORGANIZATION STATUS
	 *
	 * Represents whether the organization is currently active.
	 *
	 * Expected values:
	 *
	 * ACTIVE INACTIVE
	 *
	 * nullable = false means every organization must have a status.
	 */
	@Column(nullable = false, length = 20)
	private String status;

	/*
	 * CREATED BY
	 *
	 * Stores the ID of the user who created this organization.
	 *
	 * In our current flow, organizations are created by SUPER_ADMIN.
	 *
	 * Example:
	 *
	 * SUPER_ADMIN id = 1
	 *
	 * created_by = 1
	 *
	 * Notice that this is currently stored as a simple Long, not as a @ManyToOne
	 * User relationship.
	 */
	@Column(name = "created_by")
	private Long createdBy;

	/*
	 * CREATED AT
	 *
	 * Stores the date and time when the organization was first created.
	 *
	 * name = "created_at" -> Maps Java field createdAt to DB column created_at.
	 *
	 * nullable = false -> Every organization must have a creation timestamp.
	 *
	 * updatable = false -> Hibernate should not modify this column during updates.
	 *
	 * Creation time should remain the same throughout the organization's lifetime.
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/*
	 * @PrePersist
	 *
	 * This is a JPA lifecycle callback.
	 *
	 * It automatically runs BEFORE a new Organization entity is inserted into the
	 * database.
	 *
	 * Flow:
	 *
	 * organizationRepository.save(organization) ↓
	 * 
	 * @PrePersist ↓ onCreate() ↓ Database INSERT
	 */

	/*
	 * CURRENT SUBSCRIPTION
	 *
	 * Represents the subscription plan currently assigned to this organization.
	 *
	 * Example:
	 *
	 * Acme Technologies ↓ Basic Plan
	 *
	 * Many organizations may use the same subscription plan, therefore this is a
	 * Many-To-One relationship.
	 *
	 * Database column:
	 *
	 * subscription_id
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_id")
	private Subscription subscription;

	/*
	 * SUBSCRIPTION START DATE
	 *
	 * Stores when the organization's current subscription became active.
	 *
	 * Example:
	 *
	 * 2026-08-05 04:28:17
	 */
	@Column(name = "subscription_start_at")
	private LocalDateTime subscriptionStartAt;

	/*
	 * SUBSCRIPTION EXPIRY DATE
	 *
	 * Stores when the organization's current subscription expires.
	 *
	 * Example:
	 *
	 * Basic plan duration = 3 months
	 *
	 * Start: 2026-08-05
	 *
	 * Expiry: 2026-11-05
	 */
	@Column(name = "subscription_expires_at")
	private LocalDateTime subscriptionExpiresAt;

	@PrePersist
	protected void onCreate() {

		/*
		 * If no status was manually provided, make the organization ACTIVE by default.
		 *
		 * This prevents a newly created organization from having a null status.
		 */
		if (status == null) {
			status = "ACTIVE";
		}

		/*
		 * Automatically record the current date/time when the organization is first
		 * saved.
		 */
		createdAt = LocalDateTime.now();
	}
}