package com.kingoftheserver.plugin.util;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and formats human-readable duration strings such as {@code 7m}, {@code 90s}
 * or {@code 1h30m}. Used for configuration values so the seven-minute default never
 * needs to be hardcoded outside of {@code config.yml}.
 */
public final class DurationUtil {

    private static final Pattern COMPONENT_PATTERN = Pattern.compile("(\\d+)([dhms])", Pattern.CASE_INSENSITIVE);

    private DurationUtil() {
    }

    /**
     * Parses a duration string composed of one or more {@code <number><unit>} components,
     * where unit is one of {@code d}, {@code h}, {@code m} or {@code s}. A bare number is
     * treated as a number of seconds.
     *
     * @throws IllegalArgumentException if the string cannot be parsed or is not positive
     */
    public static Duration parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Duration string cannot be empty");
        }

        String trimmed = raw.trim();
        if (trimmed.chars().allMatch(Character::isDigit)) {
            return Duration.ofSeconds(Long.parseLong(trimmed));
        }

        Matcher matcher = COMPONENT_PATTERN.matcher(trimmed);
        long totalSeconds = 0;
        int matchedChars = 0;
        boolean foundAny = false;

        while (matcher.find()) {
            foundAny = true;
            matchedChars += matcher.end() - matcher.start();
            long value = Long.parseLong(matcher.group(1));
            char unit = Character.toLowerCase(matcher.group(2).charAt(0));
            totalSeconds += switch (unit) {
                case 'd' -> value * 86_400L;
                case 'h' -> value * 3_600L;
                case 'm' -> value * 60L;
                case 's' -> value;
                default -> throw new IllegalArgumentException("Unknown duration unit: " + unit);
            };
        }

        if (!foundAny || matchedChars != trimmed.length()) {
            throw new IllegalArgumentException("Invalid duration string: " + raw);
        }
        if (totalSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be positive: " + raw);
        }

        return Duration.ofSeconds(totalSeconds);
    }

    /**
     * Formats a duration as {@code mm:ss}, clamping negative durations to zero.
     */
    public static String formatClock(Duration duration) {
        long totalSeconds = Math.max(0, duration.getSeconds());
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
