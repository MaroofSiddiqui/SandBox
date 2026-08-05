import { useEffect, useState } from "react";
import {
    createPaymentOrder,
    verifyPayment
} from "../api/paymentApi";
import { loadRazorpayScript } from "../utils/razorpay";
import axiosInstance from "../api/axiosInstance";
import { getCurrentHrOrganization } from "../api/hrOrganizationApi";

function SubscriptionPlans() {

    const [subscriptions, setSubscriptions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [paymentLoading, setPaymentLoading] = useState(null);
    const [error, setError] = useState("");
    const [organization, setOrganization] = useState(null);

    /*
     * Load available subscription plans.
     */
    useEffect(() => {

        const loadPageData = async () => {

            try {

                const [
                    subscriptionResponse,
                    organizationResponse
                ] = await Promise.all([

                    axiosInstance.get(
                        "/admin/subscriptions"
                    ),

                    getCurrentHrOrganization()

                ]);


                const activePlans =
                    subscriptionResponse.data.filter(
                        (plan) => plan.status === "ACTIVE"
                    );


                setSubscriptions(activePlans);

                setOrganization(
                    organizationResponse
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
     * Start payment.
     */
    const handleChoosePlan = async (subscription) => {

        try {

            setPaymentLoading(subscription.id);
            setError("");

            /*
             * Backend identifies the organization from
             * the authenticated HR user's JWT.
             *
             * Frontend only sends subscriptionId.
             */
            const order = await createPaymentOrder(
                subscription.id
            );

            console.log("Payment order:", order);


            /*
             * Load Razorpay Checkout.
             */
            const loaded = await loadRazorpayScript();

            if (!loaded) {

                setError(
                    "Unable to load Razorpay Checkout."
                );

                return;
            }


            /*
             * Razorpay Checkout configuration.
             */
            const options = {

                key: order.razorpayKey,

                amount: Math.round(
                    Number(order.amount) * 100
                ),

                currency: order.currency,

                name: "SandBox ATS",

                description:
                    `${order.planName} Subscription`,

                order_id: order.razorpayOrderId,


                /*
                 * Razorpay calls this after
                 * successful payment.
                 */
                handler: async function (response) {

                    try {

                        console.log(
                            "Razorpay payment completed."
                        );


                        /*
                         * Verify payment on backend.
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
                         * Reload organization information.
                         *
                         * verifyPayment() activates the
                         * subscription in the backend, so
                         * fetch the latest organization data.
                         */
                        const updatedOrganization =
                            await getCurrentHrOrganization();

                        setOrganization(
                            updatedOrganization
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


                theme: {
                    color: "#2563eb"
                }
            };


            /*
             * Open Razorpay Checkout.
             */
            const razorpay =
                new window.Razorpay(options);


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


    if (loading) {

        return (
            <div style={{ padding: "30px" }}>
                Loading subscription plans...
            </div>
        );

    }


    return (

        <div style={{ padding: "30px" }}>

            <h1>Subscription Plans</h1>

            {organization && (
                <div>
                    <h2>
                        Organization: {organization.name}
                    </h2>

                    {organization && (
                        <div>

                            <h2>
                                Organization: {organization.organizationName}
                            </h2>

                            {organization.subscriptionId ? (
                                <>
                                    <p>
                                        Current Plan: {organization.planName}
                                    </p>

                                    <p>
                                        Status:{" "}
                                        {organization.subscriptionActive
                                            ? "ACTIVE"
                                            : "EXPIRED"}
                                    </p>

                                    <p>
                                        Started: {organization.subscriptionStartAt}
                                    </p>

                                    <p>
                                        Expires: {organization.subscriptionExpiresAt}
                                    </p>
                                </>
                            ) : (
                                <p>No active subscription.</p>
                            )}

                        </div>
                    )}
                </div>
            )}

            <p>
                Choose a subscription plan for your organization.
            </p>


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

                        <h2>{plan.planName}</h2>

                        <p>{plan.description}</p>

                        <h2>
                            ₹{Number(plan.price).toLocaleString("en-IN")}
                        </h2>

                        <p>
                            Duration: {plan.durationMonths} months
                        </p>

                        <p>
                            Maximum Candidates: {plan.maxCandidates}
                        </p>

                        <button
                            onClick={() => handleChoosePlan(plan)}
                            disabled={paymentLoading === plan.id}
                            style={{
                                marginTop: "15px",
                                padding: "10px 20px",
                                cursor: "pointer"
                            }}
                        >

                            {paymentLoading === plan.id
                                ? "Please wait..."
                                : "Choose Plan"}

                        </button>

                    </div>

                ))}

            </div>

        </div>

    );

}

export default SubscriptionPlans;