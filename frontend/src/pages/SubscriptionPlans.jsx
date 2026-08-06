import { useEffect, useState } from "react";

import {
    createPaymentOrder,
    verifyPayment,
    getHrPaymentHistory
} from "../api/paymentApi";

import { loadRazorpayScript } from "../utils/razorpay";

import axiosInstance from "../api/axiosInstance";

import {
    getCurrentHrOrganization
} from "../api/hrOrganizationApi";


function SubscriptionPlans() {

    /*
     * =========================
     * STATE
     * =========================
     */

    const [subscriptions, setSubscriptions] = useState([]);

    const [organization, setOrganization] = useState(null);

    const [paymentHistory, setPaymentHistory] = useState([]);

    const [loading, setLoading] = useState(true);

    const [paymentLoading, setPaymentLoading] = useState(null);

    const [error, setError] = useState("");


    /*
     * =========================
     * LOAD PAGE DATA
     * =========================
     *
     * Loads:
     *
     * 1. Available subscription plans
     * 2. Current HR organization
     * 3. Organization payment history
     */
    useEffect(() => {

        const loadPageData = async () => {

            try {

                setError("");

                const [
                    subscriptionResponse,
                    organizationResponse,
                    paymentHistoryResponse
                ] = await Promise.all([

                    /*
                     * Get all subscription plans.
                     */
                    axiosInstance.get(
                        "/admin/subscriptions"
                    ),

                    /*
                     * Get currently authenticated
                     * HR's organization.
                     */
                    getCurrentHrOrganization(),

                    /*
                     * Get payment history belonging
                     * to HR's organization.
                     */
                    getHrPaymentHistory()

                ]);


                /*
                 * Only show ACTIVE plans to HR.
                 */
                const activePlans =
                    subscriptionResponse.data.filter(
                        (plan) =>
                            plan.status === "ACTIVE"
                    );


                setSubscriptions(activePlans);

                setOrganization(
                    organizationResponse
                );

                setPaymentHistory(
                    paymentHistoryResponse
                );


            } catch (err) {

                console.error(
                    "Unable to load subscription page:",
                    err
                );

                setError(
                    err.response?.data?.message ||
                    "Unable to load subscription information."
                );

            } finally {

                setLoading(false);

            }
        };


        loadPageData();

    }, []);


    /*
     * =========================
     * CHOOSE SUBSCRIPTION PLAN
     * =========================
     */
    const handleChoosePlan = async (subscription) => {

        try {

            setPaymentLoading(subscription.id);

            setError("");


            /*
             * Backend determines organizationId
             * from authenticated HR's JWT.
             *
             * Frontend sends only subscriptionId.
             */
            const order =
                await createPaymentOrder(
                    subscription.id
                );


            console.log(
                "Payment order:",
                order
            );


            /*
             * =========================
             * LOAD RAZORPAY
             * =========================
             */

            const loaded =
                await loadRazorpayScript();


            if (!loaded) {

                setError(
                    "Unable to load Razorpay Checkout."
                );

                return;
            }


            /*
             * =========================
             * RAZORPAY OPTIONS
             * =========================
             */
            const options = {

                /*
                 * Razorpay public key returned
                 * by backend.
                 */
                key: order.razorpayKey,


                /*
                 * Razorpay expects amount
                 * in paise.
                 *
                 * Example:
                 *
                 * ₹1299
                 * =
                 * 129900 paise
                 */
                amount: Math.round(
                    Number(order.amount) * 100
                ),


                currency: order.currency,


                name: "SandBox ATS",


                description:
                    `${order.planName} Subscription`,


                /*
                 * Razorpay order generated
                 * by backend.
                 */
                order_id:
                    order.razorpayOrderId,


                /*
                 * =========================
                 * PAYMENT SUCCESS
                 * =========================
                 *
                 * Razorpay executes this function
                 * after successful payment.
                 */
                handler: async function (response) {

                    try {

                        console.log(
                            "Razorpay payment completed."
                        );


                        /*
                         * Send Razorpay payment details
                         * to backend for signature
                         * verification.
                         */
                        const verifiedPayment =
                            await verifyPayment({

                                razorpayOrderId:
                                    response.razorpay_order_id,

                                razorpayPaymentId:
                                    response.razorpay_payment_id,

                                razorpaySignature:
                                    response.razorpay_signature

                            });


                        console.log(
                            "Verified payment:",
                            verifiedPayment
                        );


                        /*
                         * =========================
                         * REFRESH PAGE DATA
                         * =========================
                         *
                         * Backend has now activated/
                         * updated the organization's
                         * subscription.
                         *
                         * Therefore reload:
                         *
                         * 1. Organization
                         * 2. Payment history
                         */
                        const [
                            updatedOrganization,
                            updatedPaymentHistory
                        ] = await Promise.all([

                            getCurrentHrOrganization(),

                            getHrPaymentHistory()

                        ]);


                        setOrganization(
                            updatedOrganization
                        );


                        setPaymentHistory(
                            updatedPaymentHistory
                        );


                        alert(
                            "Payment verified successfully!"
                        );


                    } catch (err) {

                        console.error(
                            "Payment verification failed:",
                            err
                        );


                        setError(
                            err.response?.data?.message ||
                            "Payment was completed, but verification failed."
                        );

                    }
                },


                /*
                 * Razorpay Checkout theme.
                 */
                theme: {

                    color: "#2563eb"

                }

            };


            /*
             * =========================
             * OPEN RAZORPAY CHECKOUT
             * =========================
             */

            const razorpay =
                new window.Razorpay(options);


            /*
             * Handle payment failure.
             */
            razorpay.on(
                "payment.failed",
                function (response) {

                    console.error(
                        "Payment failed:",
                        response.error
                    );


                    setError(
                        response.error.description ||
                        "Payment failed."
                    );

                }
            );


            /*
             * Open Razorpay payment window.
             */
            razorpay.open();


        } catch (err) {

            console.error(
                "Unable to start payment:",
                err
            );


            setError(
                err.response?.data?.message ||
                "Unable to start payment."
            );


        } finally {

            setPaymentLoading(null);

        }

    };


    /*
     * =========================
     * LOADING SCREEN
     * =========================
     */
    if (loading) {

        return (

            <div
                style={{
                    padding: "30px"
                }}
            >

                Loading subscription plans...

            </div>

        );

    }


    /*
     * =========================
     * PAGE
     * =========================
     */

    return (

        <div
            style={{
                padding: "30px"
            }}
        >


            {/* =========================
                PAGE TITLE
               ========================= */}

            <h1>
                Subscription Plans
            </h1>


            {/* =========================
                ORGANIZATION INFORMATION
               ========================= */}

            {organization && (

                <div
                    style={{
                        marginBottom: "30px"
                    }}
                >

                    <h2>
                        Organization:{" "}
                        {organization.organizationName}
                    </h2>


                    {organization.subscriptionId ? (

                        <>

                            <p>
                                Current Plan:{" "}
                                <strong>
                                    {organization.planName}
                                </strong>
                            </p>


                            <p>

                                Status:{" "}

                                <strong>

                                    {organization.subscriptionActive
                                        ? "ACTIVE"
                                        : "EXPIRED"}

                                </strong>

                            </p>


                            <p>
                                Started:{" "}
                                {organization.subscriptionStartAt
                                    ? new Date(
                                        organization.subscriptionStartAt
                                    ).toLocaleString()
                                    : "-"}
                            </p>


                            <p>
                                Expires:{" "}
                                {organization.subscriptionExpiresAt
                                    ? new Date(
                                        organization.subscriptionExpiresAt
                                    ).toLocaleString()
                                    : "-"}
                            </p>

                        </>

                    ) : (

                        <p>
                            No active subscription.
                        </p>

                    )}

                </div>

            )}


            {/* =========================
                ERROR MESSAGE
               ========================= */}

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


            {/* =========================
                SUBSCRIPTION PLANS
               ========================= */}

            <h2>
                Available Plans
            </h2>


            <p>
                Choose a subscription plan for your organization.
            </p>


            <div
                style={{
                    display: "flex",
                    gap: "20px",
                    flexWrap: "wrap",
                    marginTop: "30px"
                }}
            >

                {subscriptions.map((plan) => (

                    <div
                        key={plan.id}
                        style={{
                            width: "280px",
                            padding: "25px",
                            border: "1px solid #ddd",
                            borderRadius: "12px",
                            background: "white"
                        }}
                    >


                        <h2>
                            {plan.planName}
                        </h2>


                        <p>
                            {plan.description}
                        </p>


                        <h2>

                            ₹{Number(
                                plan.price
                            ).toLocaleString(
                                "en-IN"
                            )}

                        </h2>


                        <p>
                            Duration:{" "}
                            {plan.durationMonths}{" "}
                            months
                        </p>


                        <p>
                            Maximum Candidates:{" "}
                            {plan.maxCandidates}
                        </p>


                        <button
                            onClick={() =>
                                handleChoosePlan(plan)
                            }

                            disabled={
                                paymentLoading === plan.id
                            }

                            style={{
                                marginTop: "15px",
                                padding: "10px 20px",
                                cursor:
                                    paymentLoading === plan.id
                                        ? "not-allowed"
                                        : "pointer"
                            }}
                        >

                            {paymentLoading === plan.id
                                ? "Please wait..."
                                : "Choose Plan"}

                        </button>


                    </div>

                ))}

            </div>


            {/* =========================
                PAYMENT HISTORY
               ========================= */}

            <div
                style={{
                    marginTop: "50px"
                }}
            >

                <h2>
                    Payment History
                </h2>


                {paymentHistory.length === 0 ? (

                    <p>
                        No payment history available.
                    </p>

                ) : (

                    <div
                        style={{
                            overflowX: "auto"
                        }}
                    >

                        <table
                            style={{
                                width: "100%",
                                borderCollapse: "collapse",
                                marginTop: "20px"
                            }}
                        >


                            <thead>

                                <tr>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        ID
                                    </th>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        Amount
                                    </th>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        Currency
                                    </th>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        Status
                                    </th>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        Razorpay Order
                                    </th>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        Razorpay Payment
                                    </th>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        Created
                                    </th>


                                    <th
                                        style={{
                                            border: "1px solid #ddd",
                                            padding: "10px",
                                            textAlign: "left"
                                        }}
                                    >
                                        Paid
                                    </th>


                                </tr>

                            </thead>


                            <tbody>


                                {paymentHistory.map(
                                    (payment) => (

                                        <tr
                                            key={payment.id}
                                        >


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                {payment.id}

                                            </td>


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                ₹{Number(
                                                    payment.amount
                                                ).toLocaleString(
                                                    "en-IN"
                                                )}

                                            </td>


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                {payment.currency}

                                            </td>


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                {payment.status}

                                            </td>


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                {payment.razorpayOrderId || "-"}

                                            </td>


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                {payment.razorpayPaymentId || "-"}

                                            </td>


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                {payment.createdAt
                                                    ? new Date(
                                                        payment.createdAt
                                                    ).toLocaleString()
                                                    : "-"}

                                            </td>


                                            <td
                                                style={{
                                                    border: "1px solid #ddd",
                                                    padding: "10px"
                                                }}
                                            >

                                                {payment.paidAt
                                                    ? new Date(
                                                        payment.paidAt
                                                    ).toLocaleString()
                                                    : "-"}

                                            </td>


                                        </tr>

                                    )
                                )}


                            </tbody>


                        </table>

                    </div>

                )}

            </div>


        </div>

    );

}


export default SubscriptionPlans;