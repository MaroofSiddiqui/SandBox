import React, { useState, useEffect } from "react";

import EditHrModal from "../../components/admin/modals/EditHrModal";
import CreateHrModal from "../../components/admin/modals/CreateHrModal";

import {
    getHrs,
    createHr,
    updateHr,
    updateHrStatus
} from "../../api/hrApi";

function HrManagement() {

    const [hrs, setHrs] = useState([]);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [selectedHr, setSelectedHr] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");

    useEffect(() => {
        fetchHrs();
    }, []);

    const fetchHrs = async () => {

        try {

            const response = await getHrs();

            setHrs(response.data);

        } catch (error) {

            console.error(error);

        }

    };

    const handleCreateHr = async (data) => {
        try {

            await createHr(data);

            setShowCreateModal(false);

            fetchHrs();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to create HR"
            );

        }
    };

    const handleUpdateHr = async (id, data) => {

        try {

            await updateHr(id, data);

            setShowEditModal(false);

            setSelectedHr(null);

            fetchHrs();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to update HR"
            );

        }

    };

    const handleStatus = async (hr) => {

        try {

            const newStatus =
                hr.status === "ACTIVE"
                    ? "INACTIVE"
                    : "ACTIVE";

            await updateHrStatus(hr.id, newStatus);

            fetchHrs();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to update status"
            );

        }

    };

    const filteredHrs = hrs.filter((hr) => {

        const keyword = searchTerm.toLowerCase();

        return (

            hr.name.toLowerCase().includes(keyword) ||

            hr.email.toLowerCase().includes(keyword) ||

            String(hr.organizationId).includes(keyword) ||

            hr.status.toLowerCase().includes(keyword)

        );

    });

    return (

        <div className="page-container">

            <div className="page-header page-header-right">

                <button
                    className="primary-btn"
                    onClick={() => setShowCreateModal(true)}
                >
                    + Create HR
                </button>

            </div>

            <div className="search-container">

                <input
                    className="search-input"
                    placeholder="Search HR..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />

            </div>

            <div className="table-container">

                <table className="admin-table">

                    <thead>

                        <tr>

                            <th>Name</th>
                            <th>Email</th>
                            <th>Organization</th>
                            <th>Status</th>
                            <th>Actions</th>

                        </tr>

                    </thead>

                    <tbody>

                        {
                            filteredHrs.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan="6"
                                        className="empty-table"
                                    >
                                        {searchTerm
                                            ? "No matching HR found."
                                            : "No HR found."
                                        }
                                    </td>

                                </tr>

                            ) : (

                                filteredHrs.map((hr) => (

                                    <tr key={hr.id}>

                                        <td>{hr.name}</td>

                                        <td>{hr.email}</td>

                                        <td>{hr.organizationId}</td>

                                        <td>{hr.status}</td>

                                        <td>

                                            <div className="action-buttons">

                                                <button
                                                    className="table-link edit-btn"
                                                    onClick={() => {
                                                        setSelectedHr(hr);
                                                        setShowEditModal(true);
                                                    }}
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    className={`table-link ${hr.status === "ACTIVE"
                                                        ? "deactivate-btn"
                                                        : "activate-btn"
                                                        }`}
                                                    onClick={() => handleStatus(hr)}
                                                >
                                                    {hr.status === "ACTIVE"
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

            <CreateHrModal
                open={showCreateModal}
                onClose={() => setShowCreateModal(false)}
                onSuccess={handleCreateHr}
            />

            <EditHrModal
                open={showEditModal}
                onClose={() => {
                    setShowEditModal(false);
                    setSelectedHr(null);
                }}
                hr={selectedHr}
                onUpdate={handleUpdateHr}
            />

        </div>



    );

}

export default HrManagement;