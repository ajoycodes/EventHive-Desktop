package app.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

/**
 * Utility class for input validation and sanitization
 * Helps prevent SQL injection, XSS, and validates user inputs
 */
public class ValidationUtils {

    // Username validation: 3-20 characters, alphanumeric and underscore only
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    // Alphanumeric with spaces (for names, titles)
    private static final Pattern ALPHANUMERIC_SPACE_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]+$");

    // Potentially dangerous characters for SQL injection
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(".*[';\"\\-\\-#].*");

    // XSS patterns
    private static final Pattern XSS_PATTERN = Pattern.compile(".*[<>].*");

    /**
     * Validate email format using Apache Commons Validator
     * 
     * @param email The email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    /**
     * Validate username format
     * Must be 3-20 characters, alphanumeric and underscore only
     * 
     * @param username The username to validate
     * @return true if valid username format
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Check if a string is empty or null
     * 
     * @param str The string to check
     * @return true if string is null or empty/whitespace
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if a string is not empty
     * 
     * @param str The string to check
     * @return true if string is not null and not empty/whitespace
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Sanitize input to prevent SQL injection
     * Note: This is a basic check. Always use PreparedStatements for SQL queries!
     * 
     * @param input The input to sanitize
     * @return Sanitized input or null if contains dangerous patterns
     */
    public static String sanitizeSqlInput(String input) {
        if (input == null) {
            return null;
        }

        // Check for SQL injection patterns
        if (SQL_INJECTION_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException("Input contains potentially dangerous characters");
        }

        return input.trim();
    }

    /**
     * Sanitize input to prevent XSS attacks
     * 
     * @param input The input to sanitize
     * @return Sanitized input with HTML characters escaped
     */
    public static String sanitizeXssInput(String input) {
        if (input == null) {
            return null;
        }

        // Replace HTML special characters
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * General purpose sanitization for text input
     * Removes leading/trailing whitespace and limits length
     * 
     * @param input     The input to sanitize
     * @param maxLength Maximum allowed length
     * @return Sanitized input
     */
    public static String sanitizeInput(String input, int maxLength) {
        if (input == null) {
            return null;
        }

        String sanitized = input.trim();

        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }

        return sanitized;
    }

    /**
     * Validate that input contains only alphanumeric characters and spaces
     * 
     * @param input The input to validate
     * @return true if valid
     */
    public static boolean isAlphanumericWithSpaces(String input) {
        if (input == null) {
            return false;
        }
        return ALPHANUMERIC_SPACE_PATTERN.matcher(input).matches();
    }

    /**
     * Validate a phone number (basic validation)
     * 
     * @param phone The phone number to validate
     * @return true if valid phone format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        // Remove common formatting characters
        String cleaned = phone.replaceAll("[\\s\\-().]", "");

        // Check if it's 10-15 digits
        return cleaned.matches("^[0-9]{10,15}$");
    }

    /**
     * Validate a number is within a range
     * 
     * @param value The value to check
     * @param min   Minimum value (inclusive)
     * @param max   Maximum value (inclusive)
     * @return true if value is within range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validate a number is within a range
     * 
     * @param value The value to check
     * @param min   Minimum value (inclusive)
     * @param max   Maximum value (inclusive)
     * @return true if value is within range
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Validate that a string matches a pattern
     * 
     * @param input   The input to validate
     * @param pattern The regex pattern
     * @return true if matches
     */
    public static boolean matchesPattern(String input, String pattern) {
        if (input == null || pattern == null) {
            return false;
        }
        return Pattern.compile(pattern).matcher(input).matches();
    }
}
