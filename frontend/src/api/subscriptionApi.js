import axiosInstance from "./axiosInstance";

// Get all subscription plans
export const getSubscriptions = () =>
    axiosInstance.get("/admin/subscriptions");

// Get subscription plan by ID
export const getSubscriptionById = (id) =>
    axiosInstance.get(`/admin/subscriptions/${id}`);

// Create subscription plan
export const createSubscription = (data) =>
    axiosInstance.post("/admin/subscriptions", data);

// Update subscription plan
export const updateSubscription = (id, data) =>
    axiosInstance.put(
        `/admin/subscriptions/${id}`,
        data
    );

// Activate/deactivate subscription
export const updateSubscriptionStatus = (id, status) =>
    axiosInstance.patch(
        `/admin/subscriptions/${id}/status`,
        { status }
    );