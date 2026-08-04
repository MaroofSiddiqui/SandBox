import { useEffect, useState } from "react";
import {
    createPaymentOrder,
    verifyPayment
} from "../api/paymentApi";
import { loadRazorpayScript } from "../utils/razorpay";
import axiosInstance from "../api/axiosInstance";

function SubscriptionPlans() {

    const [subscriptions, setSubscriptions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [paymentLoading, setPaymentLoading] = useState(null);
    const [error, setError] = useState("");

    /*
     * Load available subscription plans.
     */
    useEffect(() => {

        const loadSubscriptions = async () => {

            try {

                const response = await axiosInstance.get(
                    "/admin/subscriptions"
                );

                /*
                 * Only show ACTIVE plans to organizations.
                 */
                const activePlans = response.data.filter(
                    (plan) => plan.status === "ACTIVE"
                );

                setSubscriptions(activePlans);

            } catch (err) {

                console.error(err);

                setError(
                    "Unable to load subscription plans."
                );

            } finally {

                setLoading(false);

            }
        };

        loadSubscriptions();

    }, []);


    /*
     * Start payment.
     */
    const handleChoosePlan = async (subscription) => {

        try {

            setPaymentLoading(subscription.id);
            setError("");

            /*
       * Get the currently logged-in HR from localStorage.
       *
       * Login/AuthContext stores the authenticated user's
       * information here after successful login.
       */
            const storedUser = localStorage.getItem("user");

            if (!storedUser) {
                setError("Unable to identify the logged-in user.");
                return;
            }

            const user = JSON.parse(storedUser);

            /*
             * HR must belong to an organization before purchasing
             * a subscription.
             */
            if (!user.organizationId) {
                setError(
                    "Your account is not associated with an organization."
                );
                return;
            }

            const organizationId = user.organizationId;

            /*
             * Ask backend to create Razorpay order.
             */
            const order = await createPaymentOrder(
                organizationId,
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

                /*
                 * Razorpay expects amount in paise.
                 *
                 * ₹2999 = 299900 paise
                 */
                amount: Math.round(
                    Number(order.amount) * 100
                ),

                currency: order.currency,

                name: "SandBox ATS",

                description:
                    `${order.planName} Subscription`,

                order_id: order.razorpayOrderId,

                /*
                 * Called after successful payment.
                 *
                 * For now we'll only print the response.
                 * Verification comes in Step 19.
                 */
                handler: async function (response) {

                    try {

                        console.log(
                            "Razorpay payment completed."
                        );

                        /*
                         * Send Razorpay payment details to our backend.
                         *
                         * Backend will verify the signature using
                         * our Razorpay secret key.
                         */
                        const verifiedPayment = await verifyPayment({
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
            const razorpay = new window.Razorpay(options);

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

            console.error(err);

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