export const API_CONFIG = {
  AUTH_SERVICE:
    import.meta.env.VITE_AUTH_SERVICE_URL ||
    "http://localhost:8081",

  ASSESSMENT_SERVICE:
    import.meta.env.VITE_ASSESSMENT_SERVICE_URL ||
    "http://localhost:8082",

  AI_PROCTORING_SERVICE:
    import.meta.env.VITE_AI_PROCTORING_SERVICE_URL ||
    "http://localhost:8083",
};