import axiosInstance from "./axiosInstance";

/*
 * Create Razorpay order
 *
 * Backend:
 * POST /admin/payments/orders
 */
/*
 * Create Razorpay order.
 *
 * SECURITY:
 * organizationId is obtained by the backend
 * from the authenticated HR.
 */
export const createPaymentOrder = async (
    subscriptionId
) => {

    const response =
        await axiosInstance.post(
            "/admin/payments/orders",
            {
                subscriptionId
            }
        );

    return response.data;
};


/*
 * Verify Razorpay payment
 *
 * We will use this after Razorpay Checkout
 * successfully completes the payment.
 */
export const verifyPayment = async (paymentData) => {

  const response = await axiosInstance.post(
    "/admin/payments/verify",
    paymentData
  );

  return response.data;
};