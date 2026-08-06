import { useState } from "react";

function AddSubscriptionModal({
    open,
    onClose,
    onCreate
}) {

    // Stores all form field values
    const [formData, setFormData] = useState({
        planName: "",
        description: "",
        durationMonths: "",
        price: "",
        maxCandidates: ""
    });

    // Updates the corresponding field when user types
    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };

    // Submit subscription data to parent component
    const handleSubmit = async (e) => {

        e.preventDefault();

        const data = {
            planName: formData.planName.trim(),
            description: formData.description.trim(),
            durationMonths: Number(formData.durationMonths),
            price: Number(formData.price),
            maxCandidates: Number(formData.maxCandidates)
        };

        await onCreate(data);

        // Reset form after successful creation
        setFormData({
            planName: "",
            description: "",
            durationMonths: "",
            price: "",
            maxCandidates: ""
        });
    };

    // Do not render modal when closed
    if (!open) return null;

    return (

        <div className="modal-overlay">

            <div className="modal">

                <h2>Add Subscription Plan</h2>

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
                            Add Plan
                        </button>

                    </div>

                </form>

            </div>

        </div>

    );
}

export default AddSubscriptionModal;