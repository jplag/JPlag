package de.jplag;

public final class TimeUtil {

    private TimeUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Convert a duration in milli-seconds to a human-readable representation.
     * @param durationInMiliseconds Number of milli-seconds to convert.
     * @return Readable representation of the time interval.
     */
    public static String formatDuration(long durationInMiliseconds) {
        int timeInSeconds = (int) (durationInMiliseconds / 1000);
        String hours = (timeInSeconds / 3600 > 0) ? (timeInSeconds / 3600) + " h " : "";
        String minutes = (timeInSeconds / 60 > 0) ? ((timeInSeconds / 60) % 60) + " min " : "";
        String seconds = (timeInSeconds % 60) + " sec";
        return hours + minutes + seconds;
    }
}
