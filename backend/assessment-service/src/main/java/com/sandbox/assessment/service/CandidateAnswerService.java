package com.sandbox.assessment.service;

import org.springframework.stereotype.Service;

import com.sandbox.assessment.entity.AssessmentSubmission;
import com.sandbox.assessment.entity.CandidateAnswer;
import com.sandbox.assessment.entity.McqOption;
import com.sandbox.assessment.entity.Question;
import com.sandbox.assessment.repository.AssessmentQuestionRepository;
import com.sandbox.assessment.repository.AssessmentSubmissionRepository;
import com.sandbox.assessment.repository.CandidateAnswerRepository;
import com.sandbox.assessment.repository.McqOptionRepository;
import com.sandbox.assessment.repository.QuestionRepository;

@Service
public class CandidateAnswerService {

    private final CandidateAnswerRepository candidateAnswerRepository;
    private final AssessmentSubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final McqOptionRepository mcqOptionRepository;

    public CandidateAnswerService(
            CandidateAnswerRepository candidateAnswerRepository,
            AssessmentSubmissionRepository submissionRepository,
            QuestionRepository questionRepository,
            AssessmentQuestionRepository assessmentQuestionRepository,
            McqOptionRepository mcqOptionRepository) {

        this.candidateAnswerRepository = candidateAnswerRepository;
        this.submissionRepository = submissionRepository;
        this.questionRepository = questionRepository;
        this.assessmentQuestionRepository = assessmentQuestionRepository;
        this.mcqOptionRepository = mcqOptionRepository;
    }

    /*
     * ============================================================
     * CODING ANSWER
     * ============================================================
     */
    public CandidateAnswer saveCodingEvaluation(
            Long submissionId,
            Long questionId,
            String codingEvaluationId,
            Long candidateId) {

        AssessmentSubmission submission =
                getValidSubmission(submissionId, candidateId);

        validateQuestionBelongsToAssessment(
                submission,
                questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Question not found with ID: " + questionId));

        if (question.getQuestionType() != Question.QuestionType.CODING) {
            throw new RuntimeException(
                    "Question is not a coding question");
        }

        CandidateAnswer answer =
                candidateAnswerRepository
                        .findBySubmissionIdAndQuestionId(
                                submissionId,
                                questionId)
                        .orElseGet(() ->
                                CandidateAnswer.builder()
                                        .submission(submission)
                                        .question(question)
                                        .build());

        answer.setCodingEvaluationId(codingEvaluationId);

        return candidateAnswerRepository.save(answer);
    }

    /*
     * ============================================================
     * MCQ ANSWER
     * ============================================================
     */
    public CandidateAnswer saveMcqAnswer(
            Long submissionId,
            Long questionId,
            Long selectedOptionId,
            Long candidateId) {

        AssessmentSubmission submission =
                getValidSubmission(submissionId, candidateId);

        /*
         * Prevent answering a question that is not part
         * of the candidate's current assessment.
         */
        validateQuestionBelongsToAssessment(
                submission,
                questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Question not found with ID: " + questionId));

        if (question.getQuestionType() != Question.QuestionType.MCQ) {
            throw new RuntimeException(
                    "Question is not an MCQ question");
        }

        McqOption selectedOption =
                mcqOptionRepository.findById(selectedOptionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "MCQ option not found with ID: "
                                                + selectedOptionId));

        /*
         * Important security/integrity check:
         * candidate cannot submit an option belonging
         * to another MCQ question.
         */
        if (!selectedOption.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException(
                    "Selected option does not belong to this question");
        }

        boolean correct =
                Boolean.TRUE.equals(selectedOption.getIsCorrect());

        /*
         * Correct MCQ:
         * award full question marks.
         *
         * Incorrect MCQ:
         * currently award 0.
         *
         * Negative marking can be incorporated later using
         * Assessment.negativeMarks when final scoring rules
         * are confirmed.
         */
        double awardedMarks =
                correct ? question.getMarks() : 0.0;

        CandidateAnswer answer =
                candidateAnswerRepository
                        .findBySubmissionIdAndQuestionId(
                                submissionId,
                                questionId)
                        .orElseGet(() ->
                                CandidateAnswer.builder()
                                        .submission(submission)
                                        .question(question)
                                        .build());

        answer.setSelectedOption(selectedOption);
        answer.setCorrect(correct);
        answer.setAwardedMarks(awardedMarks);

        /*
         * MCQ answers should not contain a coding
         * evaluation reference.
         */
        answer.setCodingEvaluationId(null);

        return candidateAnswerRepository.save(answer);
    }

    /*
     * ============================================================
     * COMMON VALIDATION
     * ============================================================
     */

    private AssessmentSubmission getValidSubmission(
            Long submissionId,
            Long candidateId) {

        AssessmentSubmission submission =
                submissionRepository
                        .findByIdAndCandidateId(
                                submissionId,
                                candidateId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment submission not found"));

        if (submission.getStatus()
                != AssessmentSubmission.SubmissionStatus.IN_PROGRESS) {

            throw new RuntimeException(
                    "Assessment submission is not in progress");
        }

        return submission;
    }

    private void validateQuestionBelongsToAssessment(
            AssessmentSubmission submission,
            Long questionId) {

        Long assessmentId =
                submission.getAssessment().getId();

        boolean questionBelongsToAssessment =
                assessmentQuestionRepository
                        .existsByAssessmentIdAndQuestionId(
                                assessmentId,
                                questionId);

        if (!questionBelongsToAssessment) {
            throw new RuntimeException(
                    "Question does not belong to this assessment");
        }
    }
}