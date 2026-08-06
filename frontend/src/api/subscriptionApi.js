import axios from "axios";

// Base Axios instance for Subscription Management APIs
const API = axios.create({
    baseURL: "http://localhost:8081"
});

// Attach JWT token automatically to every request
API.interceptors.request.use((config) => {

    const token = localStorage.getItem("token");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

// Get all subscription plans
export const getSubscriptions = () =>
    API.get("/admin/subscriptions");

// Get a single subscription plan by ID
export const getSubscriptionById = (id) =>
    API.get(`/admin/subscriptions/${id}`);

// Create a new subscription plan
export const createSubscription = (data) =>
    API.post("/admin/subscriptions", data);

// Update an existing subscription plan
export const updateSubscription = (id, data) =>
    API.put(`/admin/subscriptions/${id}`, data);

// Activate or deactivate a subscription plan
export const updateSubscriptionStatus = (id, status) =>
    API.patch(`/admin/subscriptions/${id}/status`, {
        status
    });