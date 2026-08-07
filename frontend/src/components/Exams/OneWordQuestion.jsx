import React from "react";

/**
 * Text / Coding Answer Component
 * Member 4 Exam Engine
 */
function OneWordQuestion({ question, value = "", onChange }) {
  const isCoding = question?.questionType === "CODING";

  return (
    <div className="open-question-container">
      {isCoding && question?.problemStatement && (
        <div style={{ marginBottom: "1rem", color: "#94a3b8", fontSize: "0.95rem" }}>
          <strong>Problem Statement:</strong>
          <p>{question.problemStatement}</p>
        </div>
      )}
      <textarea
        className="text-answer-box"
        placeholder={isCoding ? "// Write your code solution here..." : "Type your answer here..."}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        rows={isCoding ? 12 : 5}
      />
    </div>
  );
}

export default OneWordQuestion;
