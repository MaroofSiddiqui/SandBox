import api from "../api/axios";

/*
|--------------------------------------------------------------------------
| Get all organizations
|--------------------------------------------------------------------------
*/
export const getOrganizations = async () => {
  const response = await api.get("/organizations");
  return response.data;
};

/*
|--------------------------------------------------------------------------
| Get organization by id
|--------------------------------------------------------------------------
*/
export const getOrganizationById = async (id) => {
  const response = await api.get(`/organizations/${id}`);
  return response.data;
};

/*
|--------------------------------------------------------------------------
| Create organization
|--------------------------------------------------------------------------
*/
export const createOrganization = async (organization) => {
  const response = await api.post("/organizations", organization);
  return response.data;
};

/*
|--------------------------------------------------------------------------
| Update organization
|--------------------------------------------------------------------------
*/
export const updateOrganization = async (id, organization) => {
  const response = await api.put(`/organizations/${id}`, organization);
  return response.data;
};

/*
|--------------------------------------------------------------------------
| Change organization status
|--------------------------------------------------------------------------
*/
export const updateOrganizationStatus = async (id, status) => {
  const response = await api.patch(`/organizations/${id}/status`, {
    status,
  });
  return response.data;
};

/*
|--------------------------------------------------------------------------
| Delete organization
|--------------------------------------------------------------------------
*/
export const deleteOrganization = async (id) => {
  const response = await api.delete(`/organizations/${id}`);
  return response.data;
};