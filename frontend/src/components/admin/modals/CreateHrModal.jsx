import { useEffect, useState } from "react";
import { getOrganizations } from "../../../api/organizationApi";

function CreateHrModal({
    open,
    onClose,
    onSuccess
}) {

    const [organizations, setOrganizations] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        organizationId: ""
    });

    useEffect(() => {

        if (open) {
            fetchOrganizations();
        }

    }, [open]);

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

        console.log("STEP 1");
        console.log(formData);

        try {
            console.log("STEP 2");

            await onSuccess({
                ...formData,
                organizationId: Number(formData.organizationId)
            });

            console.log("STEP 3");

            setFormData({
                name: "",
                email: "",
                password: "",
                organizationId: ""
            });

        } catch (err) {
            console.error("Modal Error:", err);
        }
    };

    if (!open) return null;

    return (

        <div className="modal-overlay">

            <div className="modal">

                <h2>Create HR</h2>

                <form onSubmit={handleSubmit}>

                    <input
                        name="name"
                        placeholder="Full Name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />

                    <input
                        name="email"
                        type="email"
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />

                    <input
                        name="password"
                        type="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />

                    <select
                        name="organizationId"
                        value={formData.organizationId}
                        onChange={handleChange}
                        required
                    >

                        <option value="">
                            Select Organization
                        </option>

                        {

                            organizations.map(org => (

                                <option
                                    key={org.id}
                                    value={org.id}
                                >

                                    {org.name}

                                </option>

                            ))

                        }

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
                            Create HR
                        </button>

                    </div>

                </form>

            </div>

        </div>

    );

}

export default CreateHrModal;