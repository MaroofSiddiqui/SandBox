import axiosInstance from "./axiosInstance";

export const getOrganizations = () =>
    axiosInstance.get("/organizations");

export const createOrganization = (data) =>
    axiosInstance.post("/organizations", data);

export const updateOrganization = (id, data) =>
    axiosInstance.put(`/organizations/${id}`, data);

export const updateOrganizationStatus = (id, status) =>
    axiosInstance.patch(
        `/organizations/${id}/status`,
        { status }
    );