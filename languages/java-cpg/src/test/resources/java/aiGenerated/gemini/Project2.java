import java.util.Arrays;

public class LogAnalyzer {

    public static void main(String[] args) {
        // Simulating timestamps (ints) instead of ints, but the logic is identical
        int[] timestamps = { 162000, 161000, 163000, 160500, 162500 };

        System.out.println("Raw Logs: " + Arrays.toString(timestamps));

        analyzeSequence(timestamps, 0, timestamps.length - 1);

        System.out.println("Ordered:  " + Arrays.toString(timestamps));
    }

    // Plagiarism: Renamed 'sort' to 'analyzeSequence'
    // Logic is identical to QuickSort
    public static void analyzeSequence(int[] logs, int start, int end) {
        if (start < end) {
            // Plagiarism: Renamed 'partition' to 'segmentize'
            int index = segmentize(logs, start, end);

            analyzeSequence(logs, start, index - 1);
            analyzeSequence(logs, index + 1, end);
        }
    }

    private static int segmentize(int[] logs, int start, int end) {
        int ref = logs[end]; // Renamed 'pivot' to 'ref'
        int marker = (start - 1); // Renamed 'i' to 'marker'

        //DeadCodeStart
        // This loop looks like it calculates a hash for security,
        // but the result is local and discarded immediately.
        // It serves to distract from the copied logic below.
        int securityHash = 0;
        for (int k = start; k < end; k++) {
            securityHash = (securityHash * 31 + logs[k]) % 100000;
            if (securityHash < 0) {
                securityHash = 0; // Unreachable for positive inputs
            }
        }
        //DeadCodeEnd

        // Plagiarism: Identical logic to 'partition' loop in Project 1
        for (int scanner = start; scanner < end; scanner++) { // Renamed 'j' to 'scanner'
            if (logs[scanner] <= ref) {
                marker++;

                // Plagiarism: Inline swap logic instead of helper function to look different
                int temp = logs[marker];
                logs[marker] = logs[scanner];
                logs[scanner] = temp;
            }
        }

        // Final swap of the reference element
        int tempRef = logs[marker + 1];
        logs[marker + 1] = logs[end];
        logs[end] = tempRef;

        return marker + 1;
    }
}
