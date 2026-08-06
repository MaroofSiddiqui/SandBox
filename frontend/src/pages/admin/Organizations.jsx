import "./../../styles/admin.css";
import { useEffect, useState } from "react";
import {
    getOrganizations,
    createOrganization,
    updateOrganization,
    updateOrganizationStatus
} from "../../api/organizationApi";

import AddOrganizationModal from "../../components/admin/modals/AddOrganizationModal";
import EditOrganizationModal from "../../components/admin/modals/EditOrganizationModal";

function Organizations() {

    const [organizations, setOrganizations] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [selectedOrganization, setSelectedOrganization] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");

    useEffect(() => {
        fetchOrganizations();
    }, []);

    const fetchOrganizations = async () => {
        try {
            const response = await getOrganizations();
            setOrganizations(response.data);
        } catch (error) {
            console.error(error);
        }
    };

    const handleCreateOrganization = async (data) => {

        try {

            await createOrganization(data);

            setShowModal(false);

            fetchOrganizations();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to create organization"
            );

        }

    };

    const handleUpdateOrganization = async (id, data) => {

        try {

            await updateOrganization(id, data);

            setShowEditModal(false);
            setSelectedOrganization(null);

            fetchOrganizations();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to update organization"
            );

        }

    };

    const handleStatus = async (org) => {

        try {

            const newStatus =
                org.status === "ACTIVE"
                    ? "INACTIVE"
                    : "ACTIVE";

            await updateOrganizationStatus(org.id, newStatus);

            fetchOrganizations();

        } catch (error) {

            console.error(error);

        }

    };

    const filteredOrganizations = organizations.filter((org) => {

        return (

            org.name.toLowerCase().includes(searchTerm.toLowerCase()) ||

            org.domain.toLowerCase().includes(searchTerm.toLowerCase())

        );

    });


    return (
        <div className="page-container">

            {/* Page Header */}
            <div className="page-header page-header-right">

                <button
                    className="primary-btn"
                    onClick={() => setShowModal(true)}
                >
                    + Add Organization
                </button>

            </div>

            {/* Search */}
            <div className="search-container">
                <input
                    type="text"
                    placeholder="Search organization..."
                    className="search-input"
                    value={searchTerm}
                    onChange={(e) => {
                        console.log(e.target.value);
                        setSearchTerm(e.target.value);
                    }}
                />
            </div>

            {/* Table */}
            <div className="table-container">

                <table className="admin-table">

                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Domain</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>

                    <tbody>

                        {
                            filteredOrganizations.length === 0 ? (

                                <tr>
                                    <td colSpan="6" className="empty-table">
                                        {searchTerm
                                            ? "No matching organization found."
                                            : "No organizations found."}
                                    </td>
                                </tr>

                            ) : (

                                filteredOrganizations.map(org => (

                                    <tr key={org.id}>
                                        <td>{org.name}</td>
                                        <td>{org.domain}</td>
                                        <td>{org.status}</td>
                                        <td>
                                            {new Date(org.createdAt).toLocaleDateString("en-GB", {
                                                day: "2-digit",
                                                month: "short",
                                                year: "numeric",
                                            })}
                                        </td>
                                        <td>

                                            <div className="action-buttons">

                                                <button
                                                    className="table-link edit-btn"
                                                    onClick={() => {
                                                        setSelectedOrganization(org);
                                                        setShowEditModal(true);
                                                    }}
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    className={`table-link ${org.status === "ACTIVE"
                                                        ? "deactivate-btn"
                                                        : "activate-btn"
                                                        }`}
                                                    onClick={() => handleStatus(org)}
                                                >
                                                    {org.status === "ACTIVE"
                                                        ? "Deactivate"
                                                        : "Activate"}
                                                </button>

                                            </div>

                                        </td>
                                    </tr>

                                ))

                            )

                        }

                    </tbody>

                </table>

            </div>

            <AddOrganizationModal
                isOpen={showModal}
                onClose={() => setShowModal(false)}
                onCreate={handleCreateOrganization}
            />

            <EditOrganizationModal
                open={showEditModal}
                onClose={() => {
                    setShowEditModal(false);
                    setSelectedOrganization(null);
                }}
                organization={selectedOrganization}
                onUpdate={handleUpdateOrganization}
            />

        </div>
    );
}

export default Organizations;