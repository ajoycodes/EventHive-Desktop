package app.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

/**
 * Utility class for password hashing and verification using BCrypt
 * BCrypt automatically handles salt generation and is resistant to rainbow
 * table attacks
 */
public class PasswordHasher {

    // BCrypt work factor (log rounds) - higher = more secure but slower
    // 12 is a good balance between security and performance
    private static final int BCRYPT_ROUNDS = 12;

    // Password strength requirements
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;

    // Regex patterns for password validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    /**
     * Hash a password using BCrypt
     * 
     * @param plainTextPassword The password to hash
     * @return The hashed password
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (plainTextPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password is too long");
        }

        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify a password against a BCrypt hash
     * 
     * @param plainTextPassword The password to verify
     * @param hashedPassword    The hash to verify against
     * @return true if password matches hash, false otherwise
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * Check if a password meets strength requirements
     * Requirements:
     * - At least 8 characters
     * - Contains uppercase letter
     * - Contains lowercase letter
     * - Contains digit
     * 
     * @param password The password to check
     * @return PasswordStrength object with validation results
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null) {
            return new PasswordStrength(false, "Password cannot be null", 0);
        }

        int score = 0;
        StringBuilder issues = new StringBuilder();

        // Check length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            issues.append("Password must be at least ").append(MIN_PASSWORD_LENGTH).append(" characters. ");
        } else {
            score += 25;
        }

        // Check for uppercase
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            issues.append("Add uppercase letters. ");
        } else {
            score += 25;
        }

        // Check for lowercase
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            issues.append("Add lowercase letters. ");
        } else {
            score += 25;
        }

        // Check for digits
        if (!DIGIT_PATTERN.matcher(password).find()) {
            issues.append("Add numbers. ");
        } else {
            score += 25;
        }

        // Bonus for special characters
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            score = Math.min(100, score + 10);
        }

        // Bonus for longer passwords
        if (password.length() >= 12) {
            score = Math.min(100, score + 10);
        }

        boolean isValid = score >= 75 && issues.length() == 0;
        String message = issues.length() > 0 ? issues.toString().trim() : "Password is strong";

        return new PasswordStrength(isValid, message, score);
    }

    /**
     * Inner class to represent password strength
     */
    public static class PasswordStrength {
        private final boolean isValid;
        private final String message;
        private final int score; // 0-100

        public PasswordStrength(boolean isValid, String message, int score) {
            this.isValid = isValid;
            this.message = message;
            this.score = score;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getMessage() {
            return message;
        }

        public int getScore() {
            return score;
        }

        public String getStrengthLevel() {
            if (score >= 90)
                return "Excellent";
            if (score >= 75)
                return "Strong";
            if (score >= 50)
                return "Moderate";
            if (score >= 25)
                return "Weak";
            return "Very Weak";
        }
    }
}
