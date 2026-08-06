import { useEffect, useState } from "react";

function EditSubscriptionModal({
    open,
    onClose,
    subscription,
    onUpdate
}) {

    // Stores editable subscription data
    const [formData, setFormData] = useState({
        planName: "",
        description: "",
        durationMonths: "",
        price: "",
        maxCandidates: ""
    });

    // Populate the form whenever a subscription is selected
    useEffect(() => {

        if (subscription) {

            setFormData({
                planName: subscription.planName || "",
                description: subscription.description || "",
                durationMonths: subscription.durationMonths || "",
                price: subscription.price || "",
                maxCandidates: subscription.maxCandidates || ""
            });

        }

    }, [subscription]);

    // Handle input changes
    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };

    // Send updated data to parent component
    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!subscription) return;

        const data = {
            planName: formData.planName.trim(),
            description: formData.description.trim(),
            durationMonths: Number(formData.durationMonths),
            price: Number(formData.price),
            maxCandidates: Number(formData.maxCandidates)
        };

        await onUpdate(subscription.id, data);
    };

    // Don't render modal when closed
    if (!open || !subscription) return null;

    return (

        <div className="modal-overlay">

            <div className="modal">

                <h2>Edit Subscription Plan</h2>

                <form onSubmit={handleSubmit}>

                    <input
                        type="text"
                        name="planName"
                        placeholder="Plan Name"
                        value={formData.planName}
                        onChange={handleChange}
                        required
                    />

                    <textarea
                        name="description"
                        placeholder="Description"
                        value={formData.description}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="number"
                        name="durationMonths"
                        placeholder="Duration (Months)"
                        min="1"
                        value={formData.durationMonths}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="number"
                        name="price"
                        placeholder="Price"
                        min="0"
                        step="0.01"
                        value={formData.price}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="number"
                        name="maxCandidates"
                        placeholder="Maximum Candidates"
                        min="1"
                        value={formData.maxCandidates}
                        onChange={handleChange}
                        required
                    />

                    <div className="modal-buttons">

                        <button
                            type="button"
                            className="cancel-btn"
                            onClick={onClose}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="primary-btn"
                        >
                            Update Plan
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}

export default EditSubscriptionModal;