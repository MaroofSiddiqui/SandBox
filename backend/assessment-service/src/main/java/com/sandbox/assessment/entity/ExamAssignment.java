package com.sandbox.assessment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long assessmentId;

    @Column(nullable = false)
    private Long candidateId;

    private String status; // e.g., "ASSIGNED", "COMPLETED"
}