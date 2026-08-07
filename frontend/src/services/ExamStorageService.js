const STORAGE_PREFIX = "sandbox_exam_";

export const saveExamState = (assignmentId, state) => {
  localStorage.setItem(
    `${STORAGE_PREFIX}${assignmentId}`,
    JSON.stringify({ ...state, lastSaved: Date.now() })
  );
};

export const loadExamState = (assignmentId) => {
  const raw = localStorage.getItem(`${STORAGE_PREFIX}${assignmentId}`);
  return raw ? JSON.parse(raw) : null;
};

export const clearExamState = (assignmentId) => {
  localStorage.removeItem(`${STORAGE_PREFIX}${assignmentId}`);
};

export const saveResult = (assignmentId, result) => {
  localStorage.setItem(
    `${STORAGE_PREFIX}result_${assignmentId}`,
    JSON.stringify(result)
  );
};

export const loadResult = (assignmentId) => {
  const raw = localStorage.getItem(`${STORAGE_PREFIX}result_${assignmentId}`);
  return raw ? JSON.parse(raw) : null;
};
