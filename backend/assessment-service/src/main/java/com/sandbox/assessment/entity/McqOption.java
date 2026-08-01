package com.sandbox.assessment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mcq_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McqOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String optionText;

    @Column(nullable = false)
    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}