package app.utils;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * Utility class for generating unique ticket codes
 */
public class TicketCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate a unique ticket code
     * Format: EVT-XXXXXX (where X is alphanumeric)
     */
    public static String generateTicketCode() {
        StringBuilder code = new StringBuilder("EVT-");

        // Add timestamp component (last 3 digits of current time in seconds)
        long timestamp = Instant.now().getEpochSecond();
        String timeComponent = String.format("%03d", timestamp % 1000);

        // Add 3 random characters
        for (int i = 0; i < 3; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        // Add time component
        code.append(timeComponent);

        return code.toString();
    }

    /**
     * Generate a unique ticket code with event ID prefix
     * Format: EVT-{eventId}-XXXXXX
     */
    public static String generateTicketCode(int eventId) {
        StringBuilder code = new StringBuilder("EVT-");
        code.append(eventId).append("-");

        // Add 6 random characters
        for (int i = 0; i < 6; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return code.toString();
    }

    /**
     * Validate ticket code format
     */
    public static boolean isValidTicketCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }

        // Check if it starts with EVT-
        if (!code.startsWith("EVT-")) {
            return false;
        }

        // Check minimum length
        return code.length() >= 10;
    }
}
