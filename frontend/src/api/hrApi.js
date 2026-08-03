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

export const getHrs = () =>
    API.get("/hrs");

export const createHr = (data) =>
    API.post("/hrs", data);

export const updateHr = (id, data) =>
    API.put(`/hrs/${id}`, data);

export const updateHrStatus = (id, status) =>
    API.patch(`/hrs/${id}/status`, { status });