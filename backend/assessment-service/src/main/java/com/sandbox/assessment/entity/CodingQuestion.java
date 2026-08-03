package com.sandbox.assessment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coding_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String problemStatement;

    @Column(columnDefinition = "TEXT")
    private String driverCode;

    @Column(columnDefinition = "TEXT")
    private String sampleTestCasesJson;

    @Column(columnDefinition = "TEXT")
    private String hiddenTestCasesJson;

    private Integer timeLimitInSeconds;
    private Integer memoryLimitInMb;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}