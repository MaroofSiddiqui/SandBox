import React from "react";

/**
 * MCQ Question View Component
 * Member 4 Exam Engine
 */
function McqQuestion({ question, selectedAnswer, onAnswerChange }) {
  if (!question) return null;

  const options = question.mcqOptions || [
    { id: 1, optionText: "Option A" },
    { id: 2, optionText: "Option B" },
    { id: 3, optionText: "Option C" },
    { id: 4, optionText: "Option D" },
  ];

  return (
    <div className="mcq-container">
      <div className="mcq-options">
        {options.map((opt, idx) => {
          const letter = String.fromCharCode(65 + idx);
          const isSelected = selectedAnswer === opt.id || selectedAnswer === opt.optionText || selectedAnswer === letter;

          return (
            <div
              key={opt.id || idx}
              className={`option-item ${isSelected ? "selected" : ""}`}
              onClick={() => onAnswerChange(opt.id || opt.optionText)}
            >
              <div className="option-key">{letter}</div>
              <div className="option-text">{opt.optionText}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default McqQuestion;
