export function calculateExamResult(questions, answers, assessment) {
  let totalMarks = 0;
  let obtainedMarks = 0;
  let correctCount = 0;
  let wrongCount = 0;
  let unansweredCount = 0;

  const questionResults = questions.map((q, index) => {
    totalMarks += q.marks || 0;
    const answer = answers[q.id];
    let isCorrect = false;
    let status = "UNANSWERED";
    let marksAwarded = 0;

    if (q.questionType === "MCQ") {
      if (!answer?.selectedOptionId) {
        unansweredCount++;
      } else {
        const selected = q.mcqOptions?.find((o) => o.id === answer.selectedOptionId);
        isCorrect = selected?.isCorrect === true;
        if (isCorrect) {
          correctCount++;
          marksAwarded = q.marks || 0;
          status = "CORRECT";
        } else {
          wrongCount++;
          marksAwarded = -(assessment?.negativeMarks || 0);
          status = "WRONG";
        }
      }
    } else if (q.questionType === "CODING") {
      if (!answer?.code?.trim()) {
        unansweredCount++;
      } else {
        status = "SUBMITTED";
        marksAwarded = dividedCodingMarks(q.marks, answer.code);
        if (marksAwarded > 0) correctCount++;
      }
    }

    obtainedMarks += marksAwarded;

    return {
      questionId: q.id,
      index: index + 1,
      title: q.title,
      questionType: q.questionType,
      marks: q.marks,
      marksAwarded,
      status,
      isCorrect,
      answer,
    };
  });

  const percentage = totalMarks > 0 ? (obtainedMarks / totalMarks) * 100 : 0;
  const passed = obtainedMarks >= (assessment?.passingMarks || 0);

  return {
    totalMarks,
    obtainedMarks: Math.max(0, obtainedMarks),
    percentage: Math.round(percentage * 100) / 100,
    passed,
    correctCount,
    wrongCount,
    unansweredCount,
    questionResults,
    evaluatedAt: new Date().toISOString(),
  };
}

function dividedCodingMarks(totalMarks, code) {
  if (!code?.trim()) return 0;
  const lines = code.split("\n").filter((l) => l.trim()).length;
  if (lines > 5) return totalMarks * 0.5;
  return totalMarks * 0.25;
}
