import React from "react";
import { ChevronLeft, ChevronRight, Bookmark, CheckCircle, RotateCcw } from "lucide-react";

/**
 * Exam Navigation & Submission Buttons
 * Member 4 Exam Engine
 */
function NavigationButtons({
  hasPrevious,
  hasNext,
  onPrevious,
  onNext,
  onClear,
  onToggleReview,
  isReview,
  onSubmit,
}) {
  return (
    <div className="exam-footer">
      <div style={{ display: "flex", gap: "0.75rem" }}>
        <button
          className="btn-secondary"
          onClick={onPrevious}
          disabled={!hasPrevious}
          style={{ opacity: hasPrevious ? 1 : 0.4 }}
        >
          <ChevronLeft size={16} inline /> Previous
        </button>

        <button className="btn-secondary" onClick={onClear}>
          <RotateCcw size={16} inline /> Clear Response
        </button>

        <button
          className={isReview ? "btn-warning active" : "btn-warning"}
          onClick={onToggleReview}
        >
          <Bookmark size={16} inline /> {isReview ? "Marked for Review" : "Mark for Review"}
        </button>
      </div>

      <div style={{ display: "flex", gap: "0.75rem" }}>
        {hasNext ? (
          <button className="btn-secondary" onClick={onNext}>
            Next <ChevronRight size={16} inline />
          </button>
        ) : (
          <button className="btn-submit" onClick={onSubmit}>
            <CheckCircle size={16} inline /> Submit Exam
          </button>
        )}
      </div>
    </div>
  );
}

export default NavigationButtons;
