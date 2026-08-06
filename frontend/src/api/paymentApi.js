import axiosInstance from "./axiosInstance";


/*
 * Create Razorpay payment order.
 *
 * Backend identifies organization
 * from authenticated HR's JWT.
 */
export const createPaymentOrder = async (subscriptionId) => {

    const response = await axiosInstance.post(
        "/admin/payments/orders",
        {
            subscriptionId
        }
    );

    return response.data;
};


/*
 * Verify Razorpay payment.
 */
export const verifyPayment = async (paymentData) => {

    const response = await axiosInstance.post(
        "/admin/payments/verify",
        paymentData
    );

    return response.data;
};


/*
 * Get payment history of currently
 * authenticated HR's organization.
 */
export const getHrPaymentHistory = async () => {

    const response = await axiosInstance.get(
        "/hr/subscription/payments"
    );

    return response.data;
};