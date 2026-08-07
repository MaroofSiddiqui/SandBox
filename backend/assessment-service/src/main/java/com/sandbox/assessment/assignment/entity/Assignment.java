	package com.sandbox.assessment.assignment.entity;
	
	import com.sandbox.assessment.assignment.enums.AssignmentStatus;

	import com.sandbox.assessment.assignment.enums.AssignmentStatus;
	import jakarta.persistence.*;
	import lombok.*;
	
	import java.time.LocalDateTime;
	
	/**
	 * Stores assessment assignments for candidates.
	 *
	 * Member 4 Module
	 *
	 * This entity is completely independent from
	 * Member 3 implementation.
	 *
	 * It stores which candidate is assigned
	 * to which assessment along with exam status.
	 */
	@Entity
	@Table(name = "assignments")
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor	
	@Builder
	public class Assignment {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	
	    /**
	     * Assessment Id received from
	     * Assessment Module.
	     *
	     * We are intentionally storing only ID
	     * to avoid coupling with Member 3 code.
	     */
	    @Column(nullable = false)
	    private Long assessmentId;
	
	    /**
	     * Candidate Id received from
	     * Candidate Service.
	     */
	    @Column(nullable = false)
	    private Long candidateId;
	
	    /**
	     * Assignment Status
	     */
	    @Enumerated(EnumType.STRING)
	    @Builder.Default
	    private AssignmentStatus status = AssignmentStatus.ASSIGNED;
	
	    /**
	     * Assignment timestamp.
	     */
	    private LocalDateTime assignedAt;
	
	    /**
	     * Exam start time.
	     */
	    private LocalDateTime startedAt;
	
	    /**
	     * Exam submission time.
	     */
	    private LocalDateTime submittedAt;
	
	    /**
	     * Record creation timestamp.
	     */
	    private LocalDateTime createdAt;
	
	    @PrePersist
	    public void onCreate() {
	
	        createdAt = LocalDateTime.now();
	
	        assignedAt = LocalDateTime.now();
	
	    }
	
	}