import React, { useEffect, useState } from "react";
import { Clock } from "lucide-react";

/**
 * Live Countdown Timer with Auto-Submit listener
 * Member 4 Exam Engine
 */
function Timer({ durationMinutes = 30, onTimeUp, initialRemainingSeconds }) {
  const [secondsLeft, setSecondsLeft] = useState(() => {
    if (initialRemainingSeconds !== undefined && initialRemainingSeconds !== null) {
      return initialRemainingSeconds;
    }
    return durationMinutes * 60;
  });

  useEffect(() => {
    if (secondsLeft <= 0) {
      if (onTimeUp) onTimeUp();
      return;
    }

    const intervalId = setInterval(() => {
      setSecondsLeft((prev) => {
        if (prev <= 1) {
          clearInterval(intervalId);
          if (onTimeUp) onTimeUp();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(intervalId);
  }, [secondsLeft, onTimeUp]);

  const formatTime = (totalSeconds) => {
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };

  const isLowTime = secondsLeft < 300; // less than 5 mins

  return (
    <div className={`timer-box ${isLowTime ? "low-time" : "normal"}`}>
      <Clock size={18} />
      <span>{formatTime(secondsLeft)}</span>
    </div>
  );
}

export default Timer;
