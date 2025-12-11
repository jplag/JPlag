package de.jplag;

import java.time.Duration;

/**
 * Utility class for formating runtime values.
 */
public final class TimeUtil {

    private TimeUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Convert a duration in milliseconds to a human-readable representation.
     * @param durationInMilliseconds Number of milliseconds to convert.
     * @return Readable representation of the time interval.
     */
    public static String formatDuration(long durationInMilliseconds) {
        Duration duration = Duration.ofMillis(durationInMilliseconds);
        return String.format("%dh %02dmin %02ds %03dms", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart(),
                duration.toMillisPart());
    }
}
