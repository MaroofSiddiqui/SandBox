package com.sandbox.security.ratelimit;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/*
 * LOGIN RATE LIMITER
 *
 * Protects the login endpoint from excessive requests.
 *
 * Current policy:
 *
 * Maximum requests: 5
 * Time window:      1 minute
 *
 * Example:
 *
 * Request 1  -> allowed
 * Request 2  -> allowed
 * Request 3  -> allowed
 * Request 4  -> allowed
 * Request 5  -> allowed
 * Request 6  -> blocked
 *
 * After the time window expires, the counter resets.
 *
 * NOTE:
 *
 * This implementation stores counters in application memory.
 *
 * This is suitable for the current single-instance SandBox
 * application.
 *
 * For distributed production deployment, this can later
 * be replaced with Redis-based rate limiting.
 */
@Component
public class LoginRateLimiter {

    /*
     * Maximum login requests allowed during one window.
     */
    private static final int MAX_REQUESTS = 10;

    /*
     * Length of rate-limit window in minutes.
     */
    private static final int WINDOW_MINUTES = 1;

    /*
     * Stores rate-limit information for each client.
     *
     * Key:
     * Client identifier (later we'll use IP address)
     *
     * Value:
     * LoginAttemptWindow containing request count
     * and window starting time.
     */
    private final Map<String, LoginAttemptWindow> attempts =
            new ConcurrentHashMap<>();


    /*
     * CHECK WHETHER REQUEST IS ALLOWED
     */
    public boolean isAllowed(String clientKey) {

        LocalDateTime now = LocalDateTime.now();

        /*
         * Create a new window if this client has
         * never attempted login before.
         */
        LoginAttemptWindow window = attempts.computeIfAbsent(
                clientKey,
                key -> new LoginAttemptWindow(
                        0,
                        now
                )
        );

        synchronized (window) {

            /*
             * Check whether current window has expired.
             */
            if (window.getWindowStart()
                    .plusMinutes(WINDOW_MINUTES)
                    .isBefore(now)) {

                window.setRequestCount(0);
                window.setWindowStart(now);
            }

            /*
             * Client has exceeded limit.
             */
            if (window.getRequestCount() >= MAX_REQUESTS) {
                return false;
            }

            /*
             * Count this request.
             */
            window.setRequestCount(
                    window.getRequestCount() + 1
            );

            return true;
        }
    }


    /*
     * INTERNAL RATE-LIMIT WINDOW
     *
     * Stores:
     *
     * - number of requests
     * - beginning of current window
     */
    private static class LoginAttemptWindow {

        private int requestCount;

        private LocalDateTime windowStart;

        public LoginAttemptWindow(
                int requestCount,
                LocalDateTime windowStart) {

            this.requestCount = requestCount;
            this.windowStart = windowStart;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }

        public LocalDateTime getWindowStart() {
            return windowStart;
        }

        public void setWindowStart(
                LocalDateTime windowStart) {

            this.windowStart = windowStart;
        }
    }
}