import axiosInstance from "./axiosInstance";

export const getHrs = () =>
    axiosInstance.get("/hrs");

export const createHr = (data) =>
    axiosInstance.post("/hrs", data);

export const updateHr = (id, data) =>
    axiosInstance.put(`/hrs/${id}`, data);

export const updateHrStatus = (id, status) =>
    axiosInstance.patch(`/hrs/${id}/status`, { status });