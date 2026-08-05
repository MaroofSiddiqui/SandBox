import axiosInstance from "./axiosInstance";

/*
 * Get organization belonging to
 * the currently authenticated HR.
 *
 * Backend:
 * GET /hr/organization
 */
export const getCurrentHrOrganization = async () => {

    const response = await axiosInstance.get(
        "/hr/organization"
    );

    return response.data;
};