import { useEffect, useState } from "react";

function EditOrganizationModal({
    open,
    onClose,
    organization,
    onUpdate
}) {

    const [formData, setFormData] = useState({
        name: "",
        domain: ""
    });

    useEffect(() => {

        if (organization) {

            setFormData({
                name: organization.name,
                domain: organization.domain
            });

        }

    }, [organization]);

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        await onUpdate(
            organization.id,
            formData
        );

    };

    if (!open) return null;

    return (

        <div className="modal-overlay">

            <div className="modal">

                <h2>Edit Organization</h2>

                <form onSubmit={handleSubmit}>

                    <input
                        name="name"
                        placeholder="Organization Name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />

                    <input
                        name="domain"
                        placeholder="Domain"
                        value={formData.domain}
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
                            Update
                        </button>

                    </div>

                </form>

            </div>

        </div>

    );

}

export default EditOrganizationModal;