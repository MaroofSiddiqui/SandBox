package com.sandbox.exception;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sandbox.exception.RateLimitExceededException;

/*
 * GLOBAL EXCEPTION HANDLER
 *
 * Purpose:
 * This class provides centralized exception handling
 * for all REST controllers in the application.
 *
 * Instead of writing try-catch blocks inside every controller,
 * exceptions can be thrown from controllers/services and handled here.
 *
 * This keeps API error responses consistent.
 *
 * Current mappings:
 *
 * Validation Error             -> 400 Bad Request
 * Invalid Credentials          -> 401 Unauthorized
 * Illegal Argument/Conflict    -> 409 Conflict
 * Resource Not Found           -> 404 Not Found
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    /*
     * VALIDATION EXCEPTION HANDLER
     *
     * Handles validation failures caused by @Valid.
     *
     * For example, suppose CandidateRequest contains:
     *
     * @NotBlank(message = "Name is required")
     * private String name;
     *
     * and the client sends:
     *
     * {
     *     "name": ""
     * }
     *
     * Spring throws MethodArgumentNotValidException,
     * which is automatically handled by this method.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {


        /*
         * This map stores validation errors field-by-field.
         *
         * Example:
         *
         * {
         *     "name": "Name is required",
         *     "email": "Email is required"
         * }
         */
        Map<String, String> errors = new HashMap<>();


        /*
         * getBindingResult()
         * -> Gives the result of request validation.
         *
         * getFieldErrors()
         * -> Gives all validation errors related to fields.
         *
         * forEach(...)
         * -> Loops through every validation error.
         *
         * For each error:
         *
         * error.getField()
         * -> Field that failed validation.
         *
         * error.getDefaultMessage()
         * -> Message written in the validation annotation.
         *
         * Example:
         *
         * @NotBlank(message = "Email is required")
         *
         * Field   = email
         * Message = Email is required
         */
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );


        /*
         * Create the final error response that
         * will be returned to the client.
         */
        Map<String, Object> response = new HashMap<>();

        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", "Validation failed");
        response.put("errors", errors);


        /*
         * Return:
         *
         * HTTP 400 Bad Request
         *
         * along with validation details.
         */
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    /*
     * INVALID CREDENTIALS HANDLER
     *
     * Handles our custom InvalidCredentialsException.
     *
     * This exception is thrown during login when
     * authentication credentials are incorrect.
     *
     * Example:
     *
     * Correct email + wrong password
     *              ↓
     * InvalidCredentialsException
     *              ↓
     * This method
     *              ↓
     * 401 Unauthorized
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        /*
         * HTTP 401 means:
         *
         * Authentication failed / credentials are invalid.
         *
         * Map.of() creates a small immutable map that
         * Spring converts into JSON.
         */
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        Map.of(
                                "status", 401,
                                "error", "Unauthorized",
                                "message", ex.getMessage()
                        )
                );
    }


    /*
     * ILLEGAL ARGUMENT HANDLER
     *
     * In the current application, IllegalArgumentException
     * is mainly being used for business conflicts.
     *
     * Example:
     *
     * Trying to create an HR using an email
     * that already exists.
     *
     * userRepository.existsByEmail(email)
     *              ↓
     * true
     *              ↓
     * throw new IllegalArgumentException(
     *     "Email already exists"
     * )
     *              ↓
     * This handler
     *              ↓
     * 409 Conflict
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {


        /*
         * LinkedHashMap maintains insertion order.
         *
         * Therefore the JSON response normally appears as:
         *
         * status
         * error
         * message
         */
        Map<String, Object> response = new LinkedHashMap<>();


        /*
         * HttpStatus.CONFLICT.value()
         * returns the numeric HTTP status:
         *
         * 409
         */
        response.put(
                "status",
                HttpStatus.CONFLICT.value()
        );

        response.put("error", "Conflict");

        /*
         * Use the message from the exception.
         *
         * Example:
         * "Email already exists"
         */
        response.put("message", ex.getMessage());


        /*
         * Return HTTP 409 Conflict.
         */
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }


    /*
     * RESOURCE NOT FOUND HANDLER
     *
     * Handles our custom ResourceNotFoundException.
     *
     * Used when a requested database resource
     * does not exist.
     *
     * Examples:
     *
     * Organization ID 999 does not exist
     * Candidate ID 999 does not exist
     *
     * Service:
     *
     * .orElseThrow(() ->
     *     new ResourceNotFoundException(
     *         "Candidate not found"
     *     )
     * )
     *
     *              ↓
     *
     * This handler
     *
     *              ↓
     *
     * 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String, Object> response = new LinkedHashMap<>();


        /*
         * HttpStatus.NOT_FOUND.value()
         * returns:
         *
         * 404
         */
        response.put(
                "status",
                HttpStatus.NOT_FOUND.value()
        );

        response.put("error", "Not Found");

        // Add the specific exception message.
        response.put("message", ex.getMessage());


        /*
         * Return:
         *
         * HTTP 404 Not Found
         */
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    
    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleAccountInactive(
            AccountInactiveException ex) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Forbidden");
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
    
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOtp(
            InvalidOtpException ex) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /*
     * RATE LIMIT EXCEEDED
     *
     * Triggered when a client sends too many login
     * requests within the configured time window.
     *
     * HTTP Status: 429 Too Many Requests
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<?> handleRateLimitExceeded(
            RateLimitExceededException ex) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        response.put("error", "Too Many Requests");
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(response);
    }
}