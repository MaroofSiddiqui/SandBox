import axiosInstance from "./axiosInstance";

/*
 * Create Razorpay order
 *
 * Backend:
 * POST /admin/payments/orders
 */
export const createPaymentOrder = async (
  organizationId,
  subscriptionId
) => {

  const response = await axiosInstance.post(
    "/admin/payments/orders",
    {
      organizationId,
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