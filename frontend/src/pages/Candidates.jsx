import { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance";

function Candidates() {

    const [candidates, setCandidates] = useState([]);
    const [loading, setLoading] = useState(true);
    const [creating, setCreating] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const [form, setForm] = useState({
        name: "",
        email: "",
        password: ""
    });


    /*
     * LOAD CANDIDATES
     *
     * Backend:
     * GET /candidates
     */
    const loadCandidates = async () => {

        try {

            setError("");

            const response =
                await axiosInstance.get("/candidates");

            setCandidates(response.data);

        } catch (err) {

            console.error(
                "Unable to load candidates:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to load candidates."
            );

        } finally {

            setLoading(false);
        }
    };


    /*
     * Load candidates when page opens.
     */
    useEffect(() => {

        loadCandidates();

    }, []);


    /*
     * Handle form fields.
     */
    const handleChange = (event) => {

        const { name, value } = event.target;

        setForm((previous) => ({
            ...previous,
            [name]: value
        }));
    };


    /*
     * CREATE CANDIDATE
     *
     * Backend:
     * POST /candidates
     */
    const handleSubmit = async (event) => {

        event.preventDefault();

        try {

            setCreating(true);
            setError("");
            setSuccess("");


            await axiosInstance.post(
                "/candidates",
                {
                    name: form.name,
                    email: form.email,
                    password: form.password
                }
            );


            setSuccess(
                "Candidate created successfully."
            );


            /*
             * Clear form.
             */
            setForm({
                name: "",
                email: "",
                password: ""
            });


            /*
             * Reload candidate list.
             */
            await loadCandidates();


        } catch (err) {

            console.error(
                "Unable to create candidate:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to create candidate."
            );

        } finally {

            setCreating(false);
        }
    };


    return (

        <div style={{ padding: "30px" }}>

            <h1>Candidate Management</h1>

            <p>
                Create and manage candidates for your organization.
            </p>


            {/* =========================
                CREATE CANDIDATE
               ========================= */}

            <div
                style={{
                    marginTop: "30px",
                    maxWidth: "500px",
                    padding: "25px",
                    border: "1px solid #ddd",
                    borderRadius: "12px",
                    background: "white"
                }}
            >

                <h2>Add Candidate</h2>


                <form onSubmit={handleSubmit}>

                    <div style={{ marginBottom: "15px" }}>

                        <label>
                            Name
                        </label>

                        <br />

                        <input
                            type="text"
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                            required
                            style={{
                                width: "100%",
                                padding: "10px",
                                marginTop: "5px"
                            }}
                        />

                    </div>


                    <div style={{ marginBottom: "15px" }}>

                        <label>
                            Email
                        </label>

                        <br />

                        <input
                            type="email"
                            name="email"
                            value={form.email}
                            onChange={handleChange}
                            required
                            style={{
                                width: "100%",
                                padding: "10px",
                                marginTop: "5px"
                            }}
                        />

                    </div>


                    <div style={{ marginBottom: "15px" }}>

                        <label>
                            Password
                        </label>

                        <br />

                        <input
                            type="password"
                            name="password"
                            value={form.password}
                            onChange={handleChange}
                            required
                            style={{
                                width: "100%",
                                padding: "10px",
                                marginTop: "5px"
                            }}
                        />

                    </div>


                    <button
                        type="submit"
                        disabled={creating}
                        style={{
                            padding: "10px 20px",
                            cursor: creating
                                ? "not-allowed"
                                : "pointer"
                        }}
                    >

                        {creating
                            ? "Creating..."
                            : "Create Candidate"}

                    </button>

                </form>

            </div>


            {/* ERROR */}

            {error && (

                <div
                    style={{
                        color: "red",
                        marginTop: "20px"
                    }}
                >
                    {error}
                </div>

            )}


            {/* SUCCESS */}

            {success && (

                <div
                    style={{
                        color: "green",
                        marginTop: "20px"
                    }}
                >
                    {success}
                </div>

            )}


            {/* =========================
                CANDIDATE LIST
               ========================= */}

            <div style={{ marginTop: "40px" }}>

                <h2>Candidates</h2>


                {loading ? (

                    <p>Loading candidates...</p>

                ) : candidates.length === 0 ? (

                    <p>
                        No candidates found.
                    </p>

                ) : (

                    <table
                        style={{
                            width: "100%",
                            borderCollapse: "collapse",
                            marginTop: "15px"
                        }}
                    >

                        <thead>

                            <tr>

                                <th style={tableCellStyle}>
                                    ID
                                </th>

                                <th style={tableCellStyle}>
                                    Name
                                </th>

                                <th style={tableCellStyle}>
                                    Email
                                </th>

                                <th style={tableCellStyle}>
                                    Status
                                </th>

                                <th style={tableCellStyle}>
                                    Created
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {candidates.map(
                                (candidate) => (

                                    <tr key={candidate.id}>

                                        <td style={tableCellStyle}>
                                            {candidate.id}
                                        </td>

                                        <td style={tableCellStyle}>
                                            {candidate.name}
                                        </td>

                                        <td style={tableCellStyle}>
                                            {candidate.email}
                                        </td>

                                        <td style={tableCellStyle}>
                                            {candidate.status}
                                        </td>

                                        <td style={tableCellStyle}>
                                            {candidate.createdAt
                                                ? new Date(
                                                    candidate.createdAt
                                                ).toLocaleString()
                                                : "-"}
                                        </td>

                                    </tr>

                                )
                            )}

                        </tbody>

                    </table>

                )}

            </div>

        </div>
    );
}


/*
 * Temporary styling.
 *
 * We can move this to CSS when the
 * functionality is complete.
 */
const tableCellStyle = {

    border: "1px solid #ddd",
    padding: "12px",
    textAlign: "left"
};


export default Candidates;