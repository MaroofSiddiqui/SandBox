import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8081"
});

API.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export const getOrganizations = () =>
    API.get("/organizations");

export const createOrganization = (data) =>
    API.post("/organizations", data);

export const updateOrganization = (id, data) =>
    API.put(`/organizations/${id}`, data);

export const updateOrganizationStatus = (id, status) =>
    API.patch(`/organizations/${id}/status`, { status });