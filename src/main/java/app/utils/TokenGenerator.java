package app.utils;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for generating secure tokens
 * Used for password reset, session management, and other security features
 */
public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generate a cryptographically secure random token
     * 
     * @param length The length of the random bytes (will be longer after encoding)
     * @return A secure random token
     */
    public static String generateSecureToken(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    /**
     * Generate a password reset token
     * 
     * @return A 32-byte secure random token
     */
    public static String generateResetToken() {
        return generateSecureToken(32);
    }

    /**
     * Generate a session token
     * 
     * @return A 48-byte secure random token
     */
    public static String generateSessionToken() {
        return generateSecureToken(48);
    }

    /**
     * Generate a simple numeric code (for CAPTCHA or verification)
     * 
     * @param length Number of digits
     * @return A numeric code as a string
     */
    public static String generateNumericCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Check if a token has expired based on creation time
     * 
     * @param createdAt   When the token was created
     * @param expiryHours How many hours until expiry
     * @return true if token is expired
     */
    public static boolean isTokenExpired(Timestamp createdAt, int expiryHours) {
        if (createdAt == null) {
            return true;
        }

        long expiryTime = createdAt.getTime() + TimeUnit.HOURS.toMillis(expiryHours);
        long currentTime = System.currentTimeMillis();

        return currentTime > expiryTime;
    }

    /**
     * Check if a timestamp is expired based on minutes
     * 
     * @param timestamp     The timestamp to check
     * @param expiryMinutes How many minutes until expiry
     * @return true if expired
     */
    public static boolean isExpired(Timestamp timestamp, int expiryMinutes) {
        if (timestamp == null) {
            return true;
        }

        long expiryTime = timestamp.getTime() + TimeUnit.MINUTES.toMillis(expiryMinutes);
        long currentTime = System.currentTimeMillis();

        return currentTime > expiryTime;
    }

    /**
     * Get the remaining time in minutes before a timestamp expires
     * 
     * @param timestamp     The timestamp to check
     * @param expiryMinutes Total expiry time in minutes
     * @return Remaining minutes (0 if expired)
     */
    public static long getRemainingMinutes(Timestamp timestamp, int expiryMinutes) {
        if (timestamp == null) {
            return 0;
        }

        long expiryTime = timestamp.getTime() + TimeUnit.MINUTES.toMillis(expiryMinutes);
        long currentTime = System.currentTimeMillis();
        long remaining = expiryTime - currentTime;

        if (remaining <= 0) {
            return 0;
        }

        return TimeUnit.MILLISECONDS.toMinutes(remaining);
    }
}
