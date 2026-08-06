import { useEffect, useState } from "react";
import "../../styles/admin.css";

import {
    getSubscriptions,
    createSubscription,
    updateSubscription,
    updateSubscriptionStatus
} from "../../api/subscriptionApi";

import AddSubscriptionModal
    from "../../components/admin/modals/AddSubscriptionModal";

import EditSubscriptionModal
    from "../../components/admin/modals/EditSubscriptionModal";

function SubscriptionManagement() {

    // Stores all subscription plans
    const [subscriptions, setSubscriptions] = useState([]);

    // Modal states
    const [showAddModal, setShowAddModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);

    // Currently selected subscription for editing
    const [selectedSubscription, setSelectedSubscription] = useState(null);

    // Search field
    const [searchTerm, setSearchTerm] = useState("");

    // Fetch plans when page loads
    useEffect(() => {
        fetchSubscriptions();
    }, []);

    // Load all subscription plans from backend
    const fetchSubscriptions = async () => {

        try {

            const response = await getSubscriptions();

            setSubscriptions(response.data);

        } catch (error) {

            console.error("Unable to fetch subscriptions:", error);

        }

    };

    // Create a new subscription
    const handleCreateSubscription = async (data) => {

        try {

            await createSubscription(data);

            setShowAddModal(false);

            await fetchSubscriptions();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to create subscription plan"
            );

            // Re-throw so modal doesn't reset on failure
            throw error;
        }

    };

    // Update existing subscription
    const handleUpdateSubscription = async (id, data) => {

        try {

            await updateSubscription(id, data);

            setShowEditModal(false);
            setSelectedSubscription(null);

            await fetchSubscriptions();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to update subscription plan"
            );

            throw error;
        }

    };

    // Activate / deactivate subscription
    const handleStatus = async (subscription) => {

        try {

            const newStatus =
                subscription.status === "ACTIVE"
                    ? "INACTIVE"
                    : "ACTIVE";

            await updateSubscriptionStatus(
                subscription.id,
                newStatus
            );

            await fetchSubscriptions();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to update subscription status"
            );

        }

    };

    // Search subscriptions by useful visible fields
    const filteredSubscriptions = subscriptions.filter((subscription) => {

        const keyword = searchTerm.trim().toLowerCase();

        if (!keyword) {
            return true;
        }

        return (
            subscription.planName
                ?.toLowerCase()
                .includes(keyword) ||

            subscription.description
                ?.toLowerCase()
                .includes(keyword) ||

            subscription.status
                ?.toLowerCase()
                .includes(keyword) ||

            String(subscription.durationMonths)
                .includes(keyword) ||

            String(subscription.price)
                .includes(keyword) ||

            String(subscription.maxCandidates)
                .includes(keyword)
        );

    });

    return (

        <div className="page-container">

            {/* Header */}
            <div className="page-header">

                <div></div>

                <button
                    className="primary-btn"
                    onClick={() => setShowAddModal(true)}
                >
                    + Add Subscription
                </button>

            </div>

            {/* Search */}
            <div className="search-container">

                <input
                    type="text"
                    className="search-input"
                    placeholder="Search subscription..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />

            </div>

            {/* Subscription Table */}
            <div className="table-container">

                <table className="admin-table">

                    <thead>

                        <tr>
                            <th>Plan</th>
                            <th>Description</th>
                            <th>Duration</th>
                            <th>Price</th>
                            <th>Max Candidates</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>

                    </thead>

                    <tbody>

                        {
                            filteredSubscriptions.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan="7"
                                        className="empty-table"
                                    >
                                        {
                                            searchTerm
                                                ? "No matching subscription found."
                                                : "No subscription plans found."
                                        }
                                    </td>

                                </tr>

                            ) : (

                                filteredSubscriptions.map((subscription) => (

                                    <tr key={subscription.id}>

                                        <td>
                                            {subscription.planName}
                                        </td>

                                        <td>
                                            {subscription.description}
                                        </td>

                                        <td>
                                            {subscription.durationMonths} month
                                            {subscription.durationMonths !== 1
                                                ? "s"
                                                : ""}
                                        </td>

                                        <td>
                                            ₹{Number(subscription.price)
                                                .toLocaleString("en-IN")}
                                        </td>

                                        <td>
                                            {subscription.maxCandidates}
                                        </td>

                                        <td>
                                            {subscription.status}
                                        </td>

                                        <td>

                                            <div className="action-buttons">

                                                <button
                                                    className="table-link edit-btn"
                                                    onClick={() => {
                                                        setSelectedSubscription(
                                                            subscription
                                                        );
                                                        setShowEditModal(true);
                                                    }}
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    className={`table-link ${
                                                        subscription.status === "ACTIVE"
                                                            ? "deactivate-btn"
                                                            : "activate-btn"
                                                    }`}
                                                    onClick={() =>
                                                        handleStatus(subscription)
                                                    }
                                                >
                                                    {
                                                        subscription.status === "ACTIVE"
                                                            ? "Deactivate"
                                                            : "Activate"
                                                    }
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

            {/* Add Subscription Modal */}
            <AddSubscriptionModal
                open={showAddModal}
                onClose={() => setShowAddModal(false)}
                onCreate={handleCreateSubscription}
            />

            {/* Edit Subscription Modal */}
            <EditSubscriptionModal
                open={showEditModal}
                onClose={() => {
                    setShowEditModal(false);
                    setSelectedSubscription(null);
                }}
                subscription={selectedSubscription}
                onUpdate={handleUpdateSubscription}
            />

        </div>

    );
}

export default SubscriptionManagement;