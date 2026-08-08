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

    const [subscriptions, setSubscriptions] = useState([]);
    const [organization, setOrganization] = useState(null);
    const [paymentHistory, setPaymentHistory] = useState([]);

    const [loading, setLoading] = useState(true);
    const [paymentLoading, setPaymentLoading] = useState(null);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");


    // =========================================================
    // LOAD PAGE DATA
    // =========================================================

    const loadPageData = async () => {

        try {

            setLoading(true);
            setError("");

            const [
                subscriptionResponse,
                organizationResponse,
                paymentHistoryResponse
            ] = await Promise.all([

                axiosInstance.get(
                    "/admin/subscriptions"
                ),

                getCurrentHrOrganization(),

                getHrPaymentHistory()

            ]);


            const activePlans =
                (subscriptionResponse.data || [])
                    .filter(
                        plan =>
                            String(plan.status).toUpperCase() ===
                            "ACTIVE"
                    );


            setSubscriptions(activePlans);

            setOrganization(
                organizationResponse
            );

            setPaymentHistory(
                paymentHistoryResponse || []
            );

        } catch (err) {

            console.error(
                "Unable to load subscription page:",
                err
            );

            setError(
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                "Unable to load subscription information."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        loadPageData();

    }, []);


    // =========================================================
    // RAZORPAY PAYMENT
    // =========================================================

    const handleChoosePlan = async (subscription) => {

        try {

            setPaymentLoading(subscription.id);
            setError("");
            setSuccess("");


            // -------------------------------------------------
            // CREATE ORDER
            // -------------------------------------------------

            const order =
                await createPaymentOrder(
                    subscription.id
                );


            // -------------------------------------------------
            // LOAD RAZORPAY
            // -------------------------------------------------

            const loaded =
                await loadRazorpayScript();


            if (!loaded) {

                throw new Error(
                    "Unable to load Razorpay Checkout."
                );

            }


            // -------------------------------------------------
            // RAZORPAY OPTIONS
            // -------------------------------------------------

            const options = {

                key:
                    order.razorpayKey,

                amount:
                    Math.round(
                        Number(order.amount) * 100
                    ),

                currency:
                    order.currency || "INR",

                name:
                    "SandBox ATS",

                description:
                    `${order.planName} Subscription`,

                order_id:
                    order.razorpayOrderId,


                handler:
                    async function (response) {

                        try {

                            setError("");

                            setSuccess(
                                "Payment completed. Verifying payment..."
                            );


                            // ---------------------------------
                            // VERIFY PAYMENT
                            // ---------------------------------

                            await verifyPayment({

                                razorpayOrderId:
                                    response.razorpay_order_id,

                                razorpayPaymentId:
                                    response.razorpay_payment_id,

                                razorpaySignature:
                                    response.razorpay_signature

                            });


                            // ---------------------------------
                            // REFRESH DATA
                            // ---------------------------------

                            await loadPageData();


                            setSuccess(
                                "Payment successful! Your subscription has been activated."
                            );

                        } catch (err) {

                            console.error(
                                "Payment verification failed:",
                                err
                            );

                            setSuccess("");

                            setError(
                                err?.response?.data?.message ||
                                err?.response?.data?.error ||
                                "Payment was completed, but verification failed."
                            );

                        }

                    },


                theme: {
                    color: "#2563eb"
                }

            };


            const razorpay =
                new window.Razorpay(options);


            razorpay.on(
                "payment.failed",
                function (response) {

                    console.error(
                        "Payment failed:",
                        response?.error
                    );

                    setSuccess("");

                    setError(
                        response?.error?.description ||
                        "Payment failed. Please try again."
                    );

                }
            );


            razorpay.open();

        } catch (err) {

            console.error(
                "Unable to start payment:",
                err
            );

            setSuccess("");

            setError(
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                err?.message ||
                "Unable to start payment."
            );

        } finally {

            setPaymentLoading(null);

        }
    };


    // =========================================================
    // FORMAT DATE
    // =========================================================

    const formatDate = (date) => {

        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleDateString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric"
            }
        );
    };


    const formatDateTime = (date) => {

        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            }
        );
    };


    const formatAmount = (amount) => {

        return Number(
            amount || 0
        ).toLocaleString(
            "en-IN",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }
        );
    };


    // =========================================================
    // LOADING
    // =========================================================

    if (loading) {

        return (

            <div style={styles.page}>

                <div style={styles.loadingContainer}>

                    <div style={styles.spinner}></div>

                    <h2 style={styles.loadingTitle}>
                        Loading subscription
                    </h2>

                    <p style={styles.loadingText}>
                        Fetching plans and organization details...
                    </p>

                </div>

            </div>

        );

    }


    // =========================================================
    // PAGE
    // =========================================================

    return (

        <div style={styles.page}>

            <div style={styles.container}>

                {/* =================================================
                    HEADER
                   ================================================= */}

                <div style={styles.header}>

                    <div>

                        <div style={styles.eyebrow}>
                            BILLING & SUBSCRIPTION
                        </div>

                        <h1 style={styles.title}>
                            Subscription Plans
                        </h1>

                        <p style={styles.subtitle}>
                            Manage your organization's SandBox ATS
                            subscription and billing.
                        </p>

                    </div>

                    <div style={styles.secureBadge}>
                        <span style={styles.secureDot}></span>
                        Secure Billing
                    </div>

                </div>


                {/* =================================================
                    ERROR
                   ================================================= */}

                {error && (

                    <div style={styles.errorBox}>

                        <div style={styles.messageIcon}>
                            !
                        </div>

                        <div>

                            <strong style={styles.errorTitle}>
                                Something went wrong
                            </strong>

                            <div style={styles.errorText}>
                                {error}
                            </div>

                        </div>

                        <button
                            style={styles.dismissButton}
                            onClick={() =>
                                setError("")
                            }
                        >
                            ×
                        </button>

                    </div>

                )}


                {/* =================================================
                    SUCCESS
                   ================================================= */}

                {success && (

                    <div style={styles.successBox}>

                        <div style={styles.successIcon}>
                            ✓
                        </div>

                        <div>
                            <strong>
                                Payment Successful
                            </strong>

                            <div style={styles.successText}>
                                {success}
                            </div>
                        </div>

                    </div>

                )}


                {/* =================================================
                    ORGANIZATION / CURRENT PLAN
                   ================================================= */}

                <section style={styles.currentCard}>

                    <div style={styles.currentTop}>

                        <div>

                            <div style={styles.sectionLabel}>
                                YOUR ORGANIZATION
                            </div>

                            <h2 style={styles.organizationName}>
                                {organization?.organizationName ||
                                    "Organization"}
                            </h2>

                        </div>


                        {organization?.subscriptionActive ? (

                            <div style={styles.activeBadge}>
                                <span style={styles.activeDot}></span>
                                ACTIVE
                            </div>

                        ) : (

                            <div style={styles.expiredBadge}>
                                NO ACTIVE PLAN
                            </div>

                        )}

                    </div>


                    <div style={styles.currentDivider}></div>


                    {organization?.subscriptionId ? (

                        <div style={styles.currentGrid}>

                            <div>

                                <div style={styles.infoLabel}>
                                    CURRENT PLAN
                                </div>

                                <div style={styles.infoValue}>
                                    {organization.planName ||
                                        "Subscription"}
                                </div>

                            </div>


                            <div>

                                <div style={styles.infoLabel}>
                                    START DATE
                                </div>

                                <div style={styles.infoValue}>
                                    {formatDate(
                                        organization.subscriptionStartAt
                                    )}
                                </div>

                            </div>


                            <div>

                                <div style={styles.infoLabel}>
                                    EXPIRY DATE
                                </div>

                                <div style={styles.infoValue}>
                                    {formatDate(
                                        organization.subscriptionExpiresAt
                                    )}
                                </div>

                            </div>


                            <div>

                                <div style={styles.infoLabel}>
                                    STATUS
                                </div>

                                <div
                                    style={{
                                        ...styles.infoValue,
                                        color:
                                            organization.subscriptionActive
                                                ? "#15803d"
                                                : "#dc2626"
                                    }}
                                >
                                    {organization.subscriptionActive
                                        ? "Active"
                                        : "Expired"}
                                </div>

                            </div>

                        </div>

                    ) : (

                        <div style={styles.noPlan}>

                            <div style={styles.noPlanIcon}>
                                !
                            </div>

                            <div>

                                <strong>
                                    No active subscription
                                </strong>

                                <p style={styles.noPlanText}>
                                    Choose a plan below to activate
                                    your organization's subscription.
                                </p>

                            </div>

                        </div>

                    )}

                </section>


                {/* =================================================
                    PLANS
                   ================================================= */}

                <section style={styles.plansSection}>

                    <div style={styles.sectionHeader}>

                        <div>

                            <div style={styles.sectionLabel}>
                                PLANS
                            </div>

                            <h2 style={styles.sectionTitle}>
                                Choose the right plan
                            </h2>

                            <p style={styles.sectionDescription}>
                                Select a subscription for your organization.
                            </p>

                        </div>

                    </div>


                    {subscriptions.length === 0 ? (

                        <div style={styles.emptyPlans}>

                            <div style={styles.emptyIcon}>
                                ◫
                            </div>

                            <h3>
                                No plans available
                            </h3>

                            <p>
                                There are currently no active
                                subscription plans.
                            </p>

                        </div>

                    ) : (

                        <div style={styles.plansGrid}>

                            {subscriptions.map(
                                (plan) => {

                                    const isCurrentPlan =
                                        organization?.subscriptionId ===
                                        plan.id;


                                    return (

                                        <div
                                            key={plan.id}
                                            style={{
                                                ...styles.planCard,
                                                ...(isCurrentPlan
                                                    ? styles.currentPlanCard
                                                    : {})
                                            }}
                                        >

                                            {isCurrentPlan && (

                                                <div style={styles.currentPlanLabel}>
                                                    CURRENT PLAN
                                                </div>

                                            )}


                                            <div style={styles.planTop}>

                                                <div>

                                                    <h3 style={styles.planName}>
                                                        {plan.planName}
                                                    </h3>

                                                    <p style={styles.planDescription}>
                                                        {plan.description ||
                                                            "Professional assessment management for your organization."}
                                                    </p>

                                                </div>

                                            </div>


                                            <div style={styles.priceRow}>

                                                <span style={styles.currency}>
                                                    ₹
                                                </span>

                                                <span style={styles.price}>
                                                    {formatAmount(
                                                        plan.price
                                                    ).split(".")[0]}
                                                </span>

                                                <span style={styles.priceDuration}>
                                                    / {plan.durationMonths}{" "}
                                                    month
                                                    {plan.durationMonths > 1
                                                        ? "s"
                                                        : ""}
                                                </span>

                                            </div>


                                            <div style={styles.planDivider}></div>


                                            <div style={styles.featureList}>

                                                <div style={styles.featureItem}>

                                                    <span style={styles.featureCheck}>
                                                        ✓
                                                    </span>

                                                    <span>
                                                        Up to{" "}
                                                        <strong>
                                                            {plan.maxCandidates}
                                                        </strong>{" "}
                                                        candidates
                                                    </span>

                                                </div>


                                                <div style={styles.featureItem}>

                                                    <span style={styles.featureCheck}>
                                                        ✓
                                                    </span>

                                                    <span>
                                                        Secure online assessments
                                                    </span>

                                                </div>


                                                <div style={styles.featureItem}>

                                                    <span style={styles.featureCheck}>
                                                        ✓
                                                    </span>

                                                    <span>
                                                        Assessment reports
                                                    </span>

                                                </div>


                                                <div style={styles.featureItem}>

                                                    <span style={styles.featureCheck}>
                                                        ✓
                                                    </span>

                                                    <span>
                                                        Proctored examinations
                                                    </span>

                                                </div>

                                            </div>


                                            <button
                                                onClick={() =>
                                                    handleChoosePlan(
                                                        plan
                                                    )
                                                }
                                                disabled={
                                                    paymentLoading ===
                                                    plan.id
                                                }
                                                style={{
                                                    ...styles.chooseButton,
                                                    ...(isCurrentPlan
                                                        ? styles.currentButton
                                                        : {}),
                                                    ...(paymentLoading ===
                                                    plan.id
                                                        ? styles.disabledButton
                                                        : {})
                                                }}
                                            >

                                                {paymentLoading ===
                                                plan.id
                                                    ? "Opening Checkout..."
                                                    : isCurrentPlan
                                                        ? "Renew Plan"
                                                        : "Choose Plan"}

                                            </button>

                                        </div>

                                    );

                                }
                            )}

                        </div>

                    )}

                </section>


                {/* =================================================
                    PAYMENT HISTORY
                   ================================================= */}

                <section style={styles.historySection}>

                    <div style={styles.sectionHeader}>

                        <div>

                            <div style={styles.sectionLabel}>
                                BILLING
                            </div>

                            <h2 style={styles.sectionTitle}>
                                Payment History
                            </h2>

                            <p style={styles.sectionDescription}>
                                View your organization's previous payments.
                            </p>

                        </div>

                    </div>


                    {paymentHistory.length === 0 ? (

                        <div style={styles.emptyHistory}>

                            <div style={styles.emptyIcon}>
                                ₹
                            </div>

                            <h3>
                                No payments yet
                            </h3>

                            <p>
                                Your organization's payment history
                                will appear here.
                            </p>

                        </div>

                    ) : (

                        <div style={styles.tableWrapper}>

                            <table style={styles.table}>

                                <thead>

                                    <tr>

                                        <th style={styles.th}>
                                            Payment
                                        </th>

                                        <th style={styles.th}>
                                            Amount
                                        </th>

                                        <th style={styles.th}>
                                            Status
                                        </th>

                                        <th style={styles.th}>
                                            Created
                                        </th>

                                        <th style={styles.th}>
                                            Paid
                                        </th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {paymentHistory.map(
                                        (payment) => (

                                            <tr
                                                key={
                                                    payment.id
                                                }
                                                style={styles.tr}
                                            >

                                                <td style={styles.td}>

                                                    <div style={styles.paymentId}>
                                                        #{payment.id}
                                                    </div>

                                                    <div style={styles.orderId}>
                                                        {payment.razorpayOrderId ||
                                                            "Razorpay order pending"}
                                                    </div>

                                                </td>


                                                <td style={styles.td}>

                                                    <strong>
                                                        ₹
                                                        {formatAmount(
                                                            payment.amount
                                                        )}
                                                    </strong>

                                                    <div style={styles.currencySmall}>
                                                        {payment.currency ||
                                                            "INR"}
                                                    </div>

                                                </td>


                                                <td style={styles.td}>

                                                    <span
                                                        style={
                                                            String(
                                                                payment.status
                                                            ).toUpperCase() ===
                                                            "SUCCESS"
                                                                ? styles.paymentSuccess
                                                                : String(
                                                                    payment.status
                                                                ).toUpperCase() ===
                                                                "FAILED"
                                                                    ? styles.paymentFailed
                                                                    : styles.paymentPending
                                                        }
                                                    >

                                                        {payment.status}

                                                    </span>

                                                </td>


                                                <td style={styles.td}>

                                                    {formatDateTime(
                                                        payment.createdAt
                                                    )}

                                                </td>


                                                <td style={styles.td}>

                                                    {payment.paidAt
                                                        ? formatDateTime(
                                                            payment.paidAt
                                                        )
                                                        : "-"}

                                                </td>

                                            </tr>

                                        )
                                    )}

                                </tbody>

                            </table>

                        </div>

                    )}

                </section>


                {/* =================================================
                    FOOTER NOTE
                   ================================================= */}

                <div style={styles.footerNote}>

                    <span>
                        🔒
                    </span>

                    <span>
                        Payments are securely processed through Razorpay.
                        SandBox never stores your card or banking credentials.
                    </span>

                </div>

            </div>

        </div>

    );
}


// =============================================================
// STYLES
// =============================================================

const styles = {

    page: {
        minHeight: "100vh",
        background: "#f5f7fb",
        padding: "36px 24px",
        fontFamily:
            "Inter, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
        color: "#0f172a",
        boxSizing: "border-box"
    },


    container: {
        maxWidth: "1200px",
        margin: "0 auto"
    },


    loadingContainer: {
        minHeight: "80vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center"
    },


    spinner: {
        width: "38px",
        height: "38px",
        border: "4px solid #dbeafe",
        borderTop: "4px solid #2563eb",
        borderRadius: "50%",
        marginBottom: "18px"
    },


    loadingTitle: {
        margin: "0 0 6px",
        fontSize: "20px"
    },


    loadingText: {
        margin: 0,
        color: "#64748b"
    },


    header: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "flex-start",
        gap: "20px",
        marginBottom: "30px"
    },


    eyebrow: {
        fontSize: "12px",
        fontWeight: "700",
        color: "#2563eb",
        letterSpacing: "1.2px",
        marginBottom: "8px"
    },


    title: {
        margin: 0,
        fontSize: "32px",
        fontWeight: "750",
        letterSpacing: "-0.7px",
        color: "#0f172a"
    },


    subtitle: {
        margin: "8px 0 0",
        color: "#64748b",
        fontSize: "15px"
    },


    secureBadge: {
        display: "flex",
        alignItems: "center",
        gap: "8px",
        background: "#ffffff",
        border: "1px solid #e2e8f0",
        padding: "9px 14px",
        borderRadius: "999px",
        color: "#475569",
        fontSize: "13px",
        fontWeight: "600",
        whiteSpace: "nowrap"
    },


    secureDot: {
        width: "8px",
        height: "8px",
        borderRadius: "50%",
        background: "#16a34a"
    },


    errorBox: {
        display: "flex",
        alignItems: "flex-start",
        gap: "12px",
        background: "#fff1f2",
        border: "1px solid #fecdd3",
        color: "#991b1b",
        padding: "15px 16px",
        borderRadius: "12px",
        marginBottom: "20px"
    },


    messageIcon: {
        width: "24px",
        height: "24px",
        borderRadius: "50%",
        background: "#dc2626",
        color: "#ffffff",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontWeight: "800",
        flexShrink: 0
    },


    errorTitle: {
        display: "block",
        marginBottom: "3px"
    },


    errorText: {
        fontSize: "14px"
    },


    dismissButton: {
        marginLeft: "auto",
        border: "none",
        background: "transparent",
        color: "#991b1b",
        fontSize: "22px",
        cursor: "pointer"
    },


    successBox: {
        display: "flex",
        alignItems: "flex-start",
        gap: "12px",
        background: "#f0fdf4",
        border: "1px solid #bbf7d0",
        color: "#166534",
        padding: "15px 16px",
        borderRadius: "12px",
        marginBottom: "20px"
    },


    successIcon: {
        width: "24px",
        height: "24px",
        borderRadius: "50%",
        background: "#16a34a",
        color: "#ffffff",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontWeight: "800",
        flexShrink: 0
    },


    successText: {
        fontSize: "14px",
        marginTop: "3px"
    },


    currentCard: {
        background: "#ffffff",
        border: "1px solid #e2e8f0",
        borderRadius: "16px",
        padding: "25px",
        marginBottom: "42px",
        boxShadow: "0 3px 12px rgba(15,23,42,0.04)"
    },


    currentTop: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        gap: "20px"
    },


    sectionLabel: {
        color: "#64748b",
        fontSize: "11px",
        fontWeight: "800",
        letterSpacing: "1.1px",
        marginBottom: "7px"
    },


    organizationName: {
        margin: 0,
        fontSize: "22px",
        fontWeight: "700"
    },


    activeBadge: {
        display: "flex",
        alignItems: "center",
        gap: "7px",
        color: "#15803d",
        background: "#f0fdf4",
        border: "1px solid #bbf7d0",
        borderRadius: "999px",
        padding: "7px 12px",
        fontSize: "12px",
        fontWeight: "800"
    },


    activeDot: {
        width: "7px",
        height: "7px",
        background: "#16a34a",
        borderRadius: "50%"
    },


    expiredBadge: {
        color: "#b45309",
        background: "#fffbeb",
        border: "1px solid #fde68a",
        borderRadius: "999px",
        padding: "7px 12px",
        fontSize: "12px",
        fontWeight: "800"
    },


    currentDivider: {
        height: "1px",
        background: "#eef2f7",
        margin: "22px 0"
    },


    currentGrid: {
        display: "grid",
        gridTemplateColumns:
            "repeat(4, minmax(0, 1fr))",
        gap: "20px"
    },


    infoLabel: {
        color: "#94a3b8",
        fontSize: "11px",
        fontWeight: "700",
        textTransform: "uppercase",
        letterSpacing: "0.7px",
        marginBottom: "6px"
    },


    infoValue: {
        fontSize: "15px",
        fontWeight: "650",
        color: "#1e293b"
    },


    noPlan: {
        display: "flex",
        alignItems: "center",
        gap: "14px",
        padding: "16px",
        background: "#fffbeb",
        border: "1px solid #fde68a",
        borderRadius: "10px"
    },


    noPlanIcon: {
        width: "32px",
        height: "32px",
        borderRadius: "50%",
        background: "#f59e0b",
        color: "#ffffff",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontWeight: "800"
    },


    noPlanText: {
        margin: "4px 0 0",
        color: "#78716c",
        fontSize: "13px"
    },


    plansSection: {
        marginBottom: "48px"
    },


    sectionHeader: {
        marginBottom: "22px"
    },


    sectionTitle: {
        margin: 0,
        fontSize: "22px",
        fontWeight: "720"
    },


    sectionDescription: {
        margin: "6px 0 0",
        color: "#64748b",
        fontSize: "14px"
    },


    plansGrid: {
        display: "grid",
        gridTemplateColumns:
            "repeat(auto-fit, minmax(270px, 1fr))",
        gap: "20px",
        alignItems: "stretch"
    },


    planCard: {
        position: "relative",
        background: "#ffffff",
        border: "1px solid #e2e8f0",
        borderRadius: "16px",
        padding: "25px",
        display: "flex",
        flexDirection: "column",
        minHeight: "420px",
        boxSizing: "border-box",
        boxShadow: "0 3px 12px rgba(15,23,42,0.04)",
        transition: "transform 0.2s ease"
    },


    currentPlanCard: {
        border: "2px solid #2563eb",
        padding: "24px",
        boxShadow: "0 8px 24px rgba(37,99,235,0.10)"
    },


    currentPlanLabel: {
        position: "absolute",
        top: "0",
        right: "20px",
        transform: "translateY(-50%)",
        background: "#2563eb",
        color: "#ffffff",
        padding: "5px 10px",
        borderRadius: "6px",
        fontSize: "10px",
        fontWeight: "800",
        letterSpacing: "0.6px"
    },


    planTop: {
        minHeight: "80px"
    },


    planName: {
        margin: 0,
        fontSize: "20px",
        fontWeight: "750"
    },


    planDescription: {
        margin: "8px 0 0",
        color: "#64748b",
        fontSize: "13px",
        lineHeight: "1.55"
    },


    priceRow: {
        display: "flex",
        alignItems: "baseline",
        gap: "2px",
        marginTop: "22px"
    },


    currency: {
        fontSize: "20px",
        fontWeight: "700"
    },


    price: {
        fontSize: "36px",
        fontWeight: "800",
        letterSpacing: "-1px"
    },


    priceDuration: {
        color: "#64748b",
        fontSize: "13px",
        marginLeft: "5px"
    },


    planDivider: {
        height: "1px",
        background: "#eef2f7",
        margin: "20px 0"
    },


    featureList: {
        display: "flex",
        flexDirection: "column",
        gap: "12px",
        flex: 1
    },


    featureItem: {
        display: "flex",
        alignItems: "flex-start",
        gap: "9px",
        color: "#475569",
        fontSize: "13px",
        lineHeight: "1.45"
    },


    featureCheck: {
        width: "19px",
        height: "19px",
        borderRadius: "50%",
        background: "#eff6ff",
        color: "#2563eb",
        display: "inline-flex",
        alignItems: "center",
        justifyContent: "center",
        fontWeight: "800",
        fontSize: "11px",
        flexShrink: 0
    },


    chooseButton: {
        width: "100%",
        marginTop: "25px",
        border: "none",
        borderRadius: "9px",
        padding: "12px 16px",
        background: "#2563eb",
        color: "#ffffff",
        fontWeight: "700",
        fontSize: "14px",
        cursor: "pointer"
    },


    currentButton: {
        background: "#1d4ed8"
    },


    disabledButton: {
        background: "#94a3b8",
        cursor: "not-allowed"
    },


    emptyPlans: {
        background: "#ffffff",
        border: "1px solid #e2e8f0",
        borderRadius: "14px",
        padding: "50px",
        textAlign: "center"
    },


    emptyHistory: {
        background: "#ffffff",
        border: "1px solid #e2e8f0",
        borderRadius: "14px",
        padding: "45px",
        textAlign: "center"
    },


    emptyIcon: {
        width: "45px",
        height: "45px",
        borderRadius: "12px",
        background: "#eff6ff",
        color: "#2563eb",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        margin: "0 auto 12px",
        fontWeight: "800",
        fontSize: "20px"
    },


    historySection: {
        marginBottom: "30px"
    },


    tableWrapper: {
        background: "#ffffff",
        border: "1px solid #e2e8f0",
        borderRadius: "14px",
        overflowX: "auto",
        boxShadow: "0 3px 12px rgba(15,23,42,0.03)"
    },


    table: {
        width: "100%",
        borderCollapse: "collapse",
        minWidth: "750px"
    },


    th: {
        textAlign: "left",
        padding: "14px 16px",
        background: "#f8fafc",
        color: "#64748b",
        fontSize: "11px",
        fontWeight: "800",
        textTransform: "uppercase",
        letterSpacing: "0.6px",
        borderBottom: "1px solid #e2e8f0"
    },


    tr: {
        borderBottom: "1px solid #f1f5f9"
    },


    td: {
        padding: "16px",
        fontSize: "13px",
        color: "#334155",
        verticalAlign: "middle"
    },


    paymentId: {
        fontWeight: "700",
        color: "#0f172a"
    },


    orderId: {
        marginTop: "4px",
        fontSize: "11px",
        color: "#94a3b8",
        fontFamily: "monospace"
    },


    currencySmall: {
        marginTop: "3px",
        fontSize: "11px",
        color: "#94a3b8"
    },


    paymentSuccess: {
        display: "inline-block",
        padding: "5px 9px",
        borderRadius: "999px",
        background: "#dcfce7",
        color: "#166534",
        fontWeight: "700",
        fontSize: "11px"
    },


    paymentFailed: {
        display: "inline-block",
        padding: "5px 9px",
        borderRadius: "999px",
        background: "#fee2e2",
        color: "#991b1b",
        fontWeight: "700",
        fontSize: "11px"
    },


    paymentPending: {
        display: "inline-block",
        padding: "5px 9px",
        borderRadius: "999px",
        background: "#fef3c7",
        color: "#92400e",
        fontWeight: "700",
        fontSize: "11px"
    },


    footerNote: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        gap: "8px",
        color: "#94a3b8",
        fontSize: "12px",
        textAlign: "center",
        padding: "10px 0 25px"
    }

};


export default SubscriptionPlans;