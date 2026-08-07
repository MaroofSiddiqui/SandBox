import React from "react";

/**
 * Question Palette Component
 * Member 4 Exam Engine
 */
function QuestionPalette({
  questions = [],
  currentIndex = 0,
  answers = {},
  reviewFlags = {},
  onSelectQuestion,
}) {
  const getStatusClass = (index) => {
    const isCurrent = currentIndex === index;
    const isAnswered = answers[index] !== undefined && answers[index] !== "";
    const isReview = reviewFlags[index] === true;

    let status = "unattempted";
    if (isReview) status = "review";
    else if (isAnswered) status = "answered";

    if (isCurrent) status += " current";
    return status;
  };

  return (
    <div className="palette-sidebar">
      <h4>Question Palette</h4>
      <div className="palette-grid">
        {questions.map((q, idx) => (
          <button
            key={q.id || idx}
            className={`palette-btn ${getStatusClass(idx)}`}
            onClick={() => onSelectQuestion(idx)}
          >
            {idx + 1}
          </button>
        ))}
      </div>

      <div className="palette-legend">
        <div className="legend-item">
          <span className="dot answered"></span> Answered
        </div>
        <div className="legend-item">
          <span className="dot review"></span> Marked for Review
        </div>
        <div className="legend-item">
          <span className="dot unattempted"></span> Unattempted
        </div>
      </div>
    </div>
  );
}

export default QuestionPalette;
