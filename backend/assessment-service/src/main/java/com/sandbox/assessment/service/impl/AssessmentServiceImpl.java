package com.sandbox.assessment.service.impl;

import com.sandbox.assessment.dto.AssessmentDto;
import com.sandbox.assessment.entity.Assessment;
import com.sandbox.assessment.entity.AssessmentQuestion;
import com.sandbox.assessment.entity.ExamAssignment;
import com.sandbox.assessment.entity.Question;
import com.sandbox.assessment.repository.AssessmentRepository;
import com.sandbox.assessment.repository.ExamAssignmentRepository;
import com.sandbox.assessment.repository.QuestionRepository;
import com.sandbox.assessment.service.AssessmentService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final ExamAssignmentRepository examAssignmentRepository;

    public AssessmentServiceImpl(
            AssessmentRepository assessmentRepository,
            QuestionRepository questionRepository,
            ExamAssignmentRepository examAssignmentRepository
    ) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.examAssignmentRepository = examAssignmentRepository;
    }

    // ============================================================
    // CREATE ASSESSMENT
    // ============================================================

    @Override
    public AssessmentDto createAssessment(AssessmentDto dto) {

        Assessment assessment = Assessment.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationInMinutes(dto.getDurationInMinutes())
                .passingMarks(dto.getPassingMarks())
                .negativeMarks(dto.getNegativeMarks())
                .isPublished(false)
                .build();

        if (dto.getQuestionIds() != null &&
                !dto.getQuestionIds().isEmpty()) {

            List<Question> questions =
                    questionRepository.findAllById(dto.getQuestionIds());

            List<AssessmentQuestion> assessmentQuestions =
                    questions.stream()
                            .map(q -> AssessmentQuestion.builder()
                                    .assessment(assessment)
                                    .question(q)
                                    .build())
                            .collect(Collectors.toList());

            assessment.setAssessmentQuestions(
                    assessmentQuestions
            );
        }

        Assessment saved =
                assessmentRepository.save(assessment);

        dto.setId(saved.getId());
        dto.setIsPublished(
                saved.getIsPublished()
        );

        return dto;
    }

    // ============================================================
    // PUBLISH ASSESSMENT
    // ============================================================

    @Override
    public AssessmentDto publishAssessment(
            Long assessmentId
    ) {

        Assessment assessment =
                assessmentRepository.findById(assessmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found with ID: "
                                                + assessmentId
                                )
                        );

        assessment.setIsPublished(true);

        Assessment updated =
                assessmentRepository.save(assessment);

        return mapToDto(updated);
    }

    // ============================================================
    // GET ALL ASSESSMENTS
    // ============================================================

    @Override
    public List<AssessmentDto> getAllAssessments() {

        return assessmentRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ============================================================
    // GET ASSESSMENT BY ID
    // ============================================================

    @Override
    public AssessmentDto getAssessmentById(
            Long id
    ) {

        Assessment assessment =
                assessmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found with ID: "
                                                + id
                                )
                        );

        return mapToDto(assessment);
    }

    // ============================================================
    // ASSIGN CANDIDATES TO ASSESSMENT
    // ============================================================

    @Override
    public void assignCandidates(
            Long assessmentId,
            List<Long> candidateIds
    ) {

        // Verify assessment exists
        assessmentRepository.findById(assessmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assessment not found with ID: "
                                        + assessmentId
                        )
                );

        if (candidateIds == null ||
                candidateIds.isEmpty()) {

            throw new RuntimeException(
                    "No candidates selected."
            );
        }

        for (Long candidateId : candidateIds) {

            // Prevent duplicate assignment
            boolean alreadyAssigned =
                    examAssignmentRepository
                            .findByAssessmentIdAndCandidateId(
                                    assessmentId,
                                    candidateId
                            )
                            .isPresent();

            if (alreadyAssigned) {
                continue;
            }

            ExamAssignment assignment =
                    ExamAssignment.builder()
                            .assessmentId(assessmentId)
                            .candidateId(candidateId)
                            .status("ASSIGNED")
                            .build();

            examAssignmentRepository.save(
                    assignment
            );
        }
    }

    // ============================================================
    // GET CANDIDATE ASSIGNMENTS
    // ============================================================

    @Override
    public List<ExamAssignment> getCandidateAssignments(
            Long candidateId
    ) {

        return examAssignmentRepository
                .findByCandidateId(candidateId);
    }

    // ============================================================
    // DTO MAPPING
    // ============================================================

    private AssessmentDto mapToDto(
            Assessment assessment
    ) {

        List<Long> qIds =
                assessment.getAssessmentQuestions()
                        .stream()
                        .map(aq ->
                                aq.getQuestion().getId()
                        )
                        .collect(Collectors.toList());

        return AssessmentDto.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(
                        assessment.getDescription()
                )
                .durationInMinutes(
                        assessment.getDurationInMinutes()
                )
                .passingMarks(
                        assessment.getPassingMarks()
                )
                .negativeMarks(
                        assessment.getNegativeMarks()
                )
                .isPublished(
                        assessment.getIsPublished()
                )
                .questionIds(qIds)
                .build();
    }
}