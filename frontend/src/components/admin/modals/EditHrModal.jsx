import { useEffect, useState } from "react";
import { getOrganizations } from "../../../api/organizationApi";

function EditHrModal({
    open,
    onClose,
    hr,
    onUpdate
}) {

    const [organizations, setOrganizations] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        organizationId: ""
    });

    useEffect(() => {

        if (open) {
            fetchOrganizations();
        }

    }, [open]);

    useEffect(() => {

        if (hr) {

            setFormData({
                name: hr.name,
                email: hr.email,
                organizationId: hr.organizationId
            });

        }

    }, [hr]);

    const fetchOrganizations = async () => {

        try {

            const response = await getOrganizations();

            setOrganizations(response.data);

        } catch (error) {

            console.error(error);

        }

    };

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        await onUpdate(hr.id, {
            ...formData,
            organizationId: Number(formData.organizationId)
        });

    };

    if (!open) return null;

    return (

        <div className="modal-overlay">

            <div className="modal">

                <h2>Edit HR</h2>

                <form onSubmit={handleSubmit}>

                    <input
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />

                    <input
                        name="email"
                        type="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />

                    <select
                        name="organizationId"
                        value={formData.organizationId}
                        onChange={handleChange}
                        required
                    >

                        {organizations.map(org => (

                            <option
                                key={org.id}
                                value={org.id}
                            >
                                {org.name}
                            </option>

                        ))}

                    </select>

                    <div className="modal-buttons">

                        <button
                            type="button"
                            className="cancel-btn"
                            onClick={onClose}
                        >
                            Cancel
                        </button>

                        <button
                            className="primary-btn"
                            type="submit"
                        >
                            Update HR
                        </button>

                    </div>

                </form>

            </div>

        </div>

    );

}

export default EditHrModal;