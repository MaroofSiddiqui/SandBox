import { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";

function PaymentMonitoring() {

    const [payments, setPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [status, setStatus] = useState("ALL");


    /*
     * LOAD PAYMENTS
     *
     * ALL:
     * GET /admin/payments
     *
     * Filtered:
     * GET /admin/payments/status/SUCCESS
     */
    const loadPayments = async (selectedStatus = "ALL") => {

        try {

            setLoading(true);
            setError("");

            let response;

            if (selectedStatus === "ALL") {

                response = await axiosInstance.get(
                    "/admin/payments"
                );

            } else {

                response = await axiosInstance.get(
                    `/admin/payments/status/${selectedStatus}`
                );
            }

            setPayments(response.data);

        } catch (err) {

            console.error(
                "Unable to load payments:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Unable to load payment records."
            );

        } finally {

            setLoading(false);
        }
    };


    /*
     * Initial load.
     */
    useEffect(() => {

        loadPayments("ALL");

    }, []);


    /*
     * Change payment status filter.
     */
    const handleStatusChange = (event) => {

        const selectedStatus =
            event.target.value;

        setStatus(selectedStatus);

        loadPayments(selectedStatus);
    };


    /*
     * Format amount.
     */
    const formatAmount = (amount) => {

        return Number(amount).toLocaleString(
            "en-IN",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }
        );
    };


    /*
     * Format database date.
     */
    const formatDate = (date) => {

        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleString(
            "en-IN"
        );
    };


    return (

        <div style={{ padding: "30px" }}>

            <h1>Payment Monitoring</h1>

            <p>
                Monitor organization subscription payments
                and Razorpay transactions.
            </p>


            {/* PAYMENT STATUS FILTER */}

            <div
                style={{
                    marginTop: "25px",
                    marginBottom: "25px"
                }}
            >

                <label>
                    Payment Status:{" "}
                </label>

                <select
                    value={status}
                    onChange={handleStatusChange}
                >

                    <option value="ALL">
                        All
                    </option>

                    <option value="SUCCESS">
                        Success
                    </option>

                    <option value="CREATED">
                        Created
                    </option>

                    <option value="FAILED">
                        Failed
                    </option>

                </select>

            </div>


            {error && (

                <div
                    style={{
                        color: "red",
                        marginBottom: "20px"
                    }}
                >
                    {error}
                </div>

            )}


            {loading ? (

                <p>Loading payments...</p>

            ) : payments.length === 0 ? (

                <p>No payment records found.</p>

            ) : (

                <div
                    style={{
                        overflowX: "auto"
                    }}
                >

                    <table
                        style={{
                            width: "100%",
                            borderCollapse: "collapse"
                        }}
                    >

                        <thead>

                            <tr>

                                <th style={headerStyle}>
                                    ID
                                </th>

                                <th style={headerStyle}>
                                    Organization
                                </th>

                                <th style={headerStyle}>
                                    Subscription
                                </th>

                                <th style={headerStyle}>
                                    Amount
                                </th>

                                <th style={headerStyle}>
                                    Currency
                                </th>

                                <th style={headerStyle}>
                                    Status
                                </th>

                                <th style={headerStyle}>
                                    Razorpay Order
                                </th>

                                <th style={headerStyle}>
                                    Razorpay Payment
                                </th>

                                <th style={headerStyle}>
                                    Created
                                </th>

                                <th style={headerStyle}>
                                    Paid
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {payments.map((payment) => (

                                <tr key={payment.id}>

                                    <td style={cellStyle}>
                                        {payment.id}
                                    </td>

                                    <td style={cellStyle}>
                                        {payment.organizationId}
                                    </td>

                                    <td style={cellStyle}>
                                        {payment.subscriptionId}
                                    </td>

                                    <td style={cellStyle}>
                                        ₹{formatAmount(
                                            payment.amount
                                        )}
                                    </td>

                                    <td style={cellStyle}>
                                        {payment.currency}
                                    </td>

                                    <td style={cellStyle}>
                                        {payment.status}
                                    </td>

                                    <td style={cellStyle}>
                                        {payment.razorpayOrderId}
                                    </td>

                                    <td style={cellStyle}>
                                        {payment.razorpayPaymentId || "-"}
                                    </td>

                                    <td style={cellStyle}>
                                        {formatDate(
                                            payment.createdAt
                                        )}
                                    </td>

                                    <td style={cellStyle}>
                                        {formatDate(
                                            payment.paidAt
                                        )}
                                    </td>

                                </tr>

                            ))}

                        </tbody>

                    </table>

                </div>

            )}

        </div>
    );
}


const headerStyle = {

    border: "1px solid #ddd",
    padding: "12px",
    textAlign: "left",
    background: "#f5f5f5"
};


const cellStyle = {

    border: "1px solid #ddd",
    padding: "12px"
};


export default PaymentMonitoring;