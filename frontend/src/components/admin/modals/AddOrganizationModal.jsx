import { useState } from "react";

function AddOrganizationModal({
    isOpen,
    onClose,
    onCreate
}) {

    const [name, setName] = useState("");
    const [domain, setDomain] = useState("");

    if (!isOpen) return null;

    const handleSubmit = (e) => {
        e.preventDefault();

        onCreate({
            name,
            domain
        });

        setName("");
        setDomain("");
    };

    return (
        <div className="modal-overlay">

            <div className="modal">

                <h2>Add Organization</h2>

                <form onSubmit={handleSubmit}>

                    <input
                        type="text"
                        placeholder="Organization Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />

                    <input
                        type="text"
                        placeholder="Domain"
                        value={domain}
                        onChange={(e) => setDomain(e.target.value)}
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
                            Create
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}

export default AddOrganizationModal;