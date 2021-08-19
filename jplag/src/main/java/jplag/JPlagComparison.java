package jplag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This method represents the whole result of a comparison between two submissions.
 */
public class JPlagComparison implements Comparator<JPlagComparison> {

    private Submission firstSubmission;
    private Submission secondSubmission;

    private JPlagComparison firstBaseCodeMatches = null;
    private JPlagComparison secondBaseCodeMatches = null;

    private List<Match> matches = new ArrayList<>();

    public JPlagComparison(Submission firstSubmission, Submission secondSubmission) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
    }

    public final void addMatch(int startA, int startB, int length) {
        for (Match match : matches) {
            if (match.overlap(startA, startB, length)) {
                return;
            }
        }

        matches.add(new Match(startA, startB, length));
    }

    /**
     * The bigger a match (length) is relatively to the biggest match the redder is the color returned by this method.
     */
    public String color(int length) {
        int longestMatch = matches.stream().mapToInt(it -> it.getLength()).max().orElse(0);
        int color = 255 * length / longestMatch;
        String help = (color < 16 ? "0" : "") + Integer.toHexString(color);
        return "#" + help + "0000";
    }

    /*
     * A few methods to calculate some statistical data
     */

    @Override
    public int compare(JPlagComparison comparison1, JPlagComparison comparison2) {
        return Float.compare(comparison2.percent(), comparison1.percent()); // comparison2 first!
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JPlagComparison)) {
            return false;
        }
        return (compare(this, (JPlagComparison) other) == 0);
    }

    /**
     * This method returns all the files which contributed to a match. Parameter: j == 0 submission A, j != 0 submission B.
     */
    public final String[] files(int j) {
        if (matches.size() == 0) {
            return new String[] {};
        }

        TokenList tokenList = (j == 0 ? firstSubmission : secondSubmission).tokenList;
        int i, h, starti, starth, count = 1;

        o1: for (i = 1; i < matches.size(); i++) {
            starti = (j == 0 ? matches.get(i).getStartA() : matches.get(i).getStartB());
            for (h = 0; h < i; h++) {
                starth = (j == 0 ? matches.get(h).getStartA() : matches.get(h).getStartB());
                if (tokenList.getToken(starti).file.equals(tokenList.getToken(starth).file)) {
                    continue o1;
                }
            }
            count++;
        }

        String[] res = new String[count];
        res[0] = tokenList.getToken((j == 0 ? matches.get(0).getStartA() : matches.get(0).getStartB())).file;
        count = 1;

        o2: for (i = 1; i < matches.size(); i++) {
            starti = (j == 0 ? matches.get(i).getStartA() : matches.get(i).getStartB());
            for (h = 0; h < i; h++) {
                starth = (j == 0 ? matches.get(h).getStartA() : matches.get(h).getStartB());
                if (tokenList.getToken(starti).file.equals(tokenList.getToken(starth).file)) {
                    continue o2;
                }
            }
            res[count++] = tokenList.getToken(starti).file;
        }

        /*
         * sort by file name. (so that equally named files are displayed approximately side by side.)
         */
        Arrays.sort(res);

        return res;
    }

    /**
     * Get the total number of matched tokens for this comparison.
     */
    public final int getNumberOfMatchedTokens() {
        int numberOfMatchedTokens = 0;

        for (Match match : matches) {
            numberOfMatchedTokens += match.getLength();
        }

        return numberOfMatchedTokens;
    }

    /**
     * @return Similarity in percent.
     */
    public final float percent() {
        float sa, sb;
        if (secondBaseCodeMatches != null && firstBaseCodeMatches != null) {
            sa = firstSubmission.getNumberOfTokens() - firstSubmission.files.size() - firstBaseCodeMatches.getNumberOfMatchedTokens();
            sb = secondSubmission.getNumberOfTokens() - secondSubmission.files.size() - secondBaseCodeMatches.getNumberOfMatchedTokens();
        } else {
            sa = firstSubmission.getNumberOfTokens() - firstSubmission.files.size();
            sb = secondSubmission.getNumberOfTokens() - secondSubmission.files.size();
        }
        return (200 * getNumberOfMatchedTokens()) / (sa + sb);
    }

    /**
     * @return Similarity in percent for the first submission.
     */
    public final float percentA() {
        int divisor;
        if (firstBaseCodeMatches != null) {
            divisor = firstSubmission.getNumberOfTokens() - firstSubmission.files.size() - firstBaseCodeMatches.getNumberOfMatchedTokens();
        } else {
            divisor = firstSubmission.getNumberOfTokens() - firstSubmission.files.size();
        }
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }

    /**
     * @return Similarity in percent for the second submission.
     */
    public final float percentB() {
        int divisor;
        if (secondBaseCodeMatches != null) {
            divisor = secondSubmission.getNumberOfTokens() - secondSubmission.files.size() - secondBaseCodeMatches.getNumberOfMatchedTokens();
        } else {
            divisor = secondSubmission.getNumberOfTokens() - secondSubmission.files.size();
        }
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }

    /**
     * @return Maximum similarity in percent of both submissions.
     */
    public final float percentMaxAB() {
        return Math.max(percentA(), percentB());
    }

    /**
     * @return Minimum similarity in percent of both submissions.
     */
    public final float percentMinAB() {
        return Math.min(percentA(), percentB());
    }

    /**
     * @return Similarity in percent rounded to the nearest tenth.
     */
    public final float roundedPercent() {
        float percent = percent();
        return ((int) (percent * 10)) / (float) 10;
    }

    public final float roundedPercentBasecodeA() {
        float percent = percentBasecodeA();
        return ((int) (percent * 10)) / (float) 10;
    }

    public final float roundedPercentBasecodeB() {
        float percent = percentBasecodeB();
        return ((int) (percent * 10)) / (float) 10;
    }

    /*
     * s==0 uses the start indexes of subA as key for the sorting algorithm. Otherwise the start indexes of subB are used.
     */
    public final int[] sort_permutation(int s) {   // bubblesort!!!
        int size = matches.size();
        int[] perm = new int[size];
        int i, j, tmp;

        // initialize permutation array
        for (i = 0; i < size; i++) {
            perm[i] = i;
        }

        if (s == 0) {     // submission A
            for (i = 1; i < size; i++) {
                for (j = 0; j < (size - i); j++) {
                    if (matches.get(perm[j]).getStartA() > matches.get(perm[j + 1]).getStartA()) {
                        tmp = perm[j];
                        perm[j] = perm[j + 1];
                        perm[j + 1] = tmp;
                    }
                }
            }
        } else {        // submission B
            for (i = 1; i < size; i++) {
                for (j = 0; j < (size - i); j++) {
                    if (matches.get(perm[j]).getStartB() > matches.get(perm[j + 1]).getStartB()) {
                        tmp = perm[j];
                        perm[j] = perm[j + 1];
                        perm[j + 1] = tmp;
                    }
                }
            }
        }
        return perm;
    }

    @Override
    public String toString() {
        return firstSubmission.name + " <-> " + secondSubmission.name;
    }

    private final float percentBasecodeA() {
        float sa = firstSubmission.getNumberOfTokens() - firstSubmission.files.size();
        return firstBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sa;
    }

    private final float percentBasecodeB() {
        float sb = secondSubmission.getNumberOfTokens() - secondSubmission.files.size();
        return secondBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sb;
    }

    /**
     * @return the first of the two submissions.
     */
    public Submission getFirstSubmission() {
        return firstSubmission;
    }

    /**
     * @return the second of the two submissions.
     */
    public Submission getSecondSubmission() {
        return secondSubmission;
    }

    /**
     * @return the base code matches of the first submission.
     */
    public JPlagComparison getFirstBaseCodeMatches() {
        return firstBaseCodeMatches;
    }

    /**
     * @return the base code matches of the second submissions.
     */
    public JPlagComparison getSecondBaseCodeMatches() {
        return secondBaseCodeMatches;
    }

    /**
     * @return all matches between the two submissions.
     */
    public List<Match> getMatches() {
        return matches;
    }

    /**
     * Sets the base code matches of the second submissions.
     */
    public void setFirstBaseCodeMatches(JPlagComparison firstBaseCodeMatches) {
        this.firstBaseCodeMatches = firstBaseCodeMatches;
    }

    /**
     * Sets the base code matches of the second submissions.
     */
    public void setSecondBaseCodeMatches(JPlagComparison secondBaseCodeMatches) {
        this.secondBaseCodeMatches = secondBaseCodeMatches;
    }

}
