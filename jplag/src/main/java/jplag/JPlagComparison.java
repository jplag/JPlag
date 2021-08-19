package jplag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This method represents the whole result of a comparison between two submissions.
 */
public class JPlagComparison implements Comparator<JPlagComparison> {

    private static final int ROUNDING_FACTOR = 10;
    
    private Submission firstSubmission;
    private Submission secondSubmission;

    private JPlagComparison firstBaseCodeMatches = null;
    private JPlagComparison secondBaseCodeMatches = null;

    private List<Match> matches = new ArrayList<>();

    public JPlagComparison(Submission firstSubmission, Submission secondSubmission) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
    }

    /**
     * Add a match to the comparison (token indices and number of tokens), if it does not overlap with the existing matches.
     * @see Match#Match(int, int, int)
     */
    public final void addMatch(int startOfFirst, int startOfSecond, int length) {
        for (Match match : matches) {
            if (match.overlap(startOfFirst, startOfSecond, length)) {
                return;
            }
        }
        matches.add(new Match(startOfFirst, startOfSecond, length));
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

    @Override
    public int compare(JPlagComparison comparison1, JPlagComparison comparison2) {
        return Float.compare(comparison2.similarity(), comparison1.similarity()); // comparison2 first!
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JPlagComparison)) {
            return false;
        }
        return (compare(this, (JPlagComparison) other) == 0);
    }

    /**
     * This method returns all the files which contributed to a match. Parameter: j == 0 1st submission, j != 0 2nd submission.
     */
    public final String[] files(int j) {
        if (matches.size() == 0) {
            return new String[] {};
        }

        TokenList tokenList = (j == 0 ? firstSubmission : secondSubmission).tokenList;
        int i, h, starti, starth, count = 1;

        o1: for (i = 1; i < matches.size(); i++) {
            starti = (j == 0 ? matches.get(i).getStartOfFirst() : matches.get(i).getStartOfSecond());
            for (h = 0; h < i; h++) {
                starth = (j == 0 ? matches.get(h).getStartOfFirst() : matches.get(h).getStartOfSecond());
                if (tokenList.getToken(starti).file.equals(tokenList.getToken(starth).file)) {
                    continue o1;
                }
            }
            count++;
        }

        String[] res = new String[count];
        res[0] = tokenList.getToken((j == 0 ? matches.get(0).getStartOfFirst() : matches.get(0).getStartOfSecond())).file;
        count = 1;

        o2: for (i = 1; i < matches.size(); i++) {
            starti = (j == 0 ? matches.get(i).getStartOfFirst() : matches.get(i).getStartOfSecond());
            for (h = 0; h < i; h++) {
                starth = (j == 0 ? matches.get(h).getStartOfFirst() : matches.get(h).getStartOfSecond());
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
     * @return the base code matches of the first submission.
     */
    public JPlagComparison getFirstBaseCodeMatches() {
        return firstBaseCodeMatches;
    }

    /**
     * @return the first of the two submissions.
     */
    public Submission getFirstSubmission() {
        return firstSubmission;
    }

    /**
     * @return all matches between the two submissions.
     */
    public List<Match> getMatches() {
        return matches;
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
     * @return the base code matches of the second submissions.
     */
    public JPlagComparison getSecondBaseCodeMatches() {
        return secondBaseCodeMatches;
    }

    /**
     * @return the second of the two submissions.
     */
    public Submission getSecondSubmission() {
        return secondSubmission;
    }

    /**
     * @return Maximum similarity in percent of both submissions.
     */
    public final float maximalPercent() {
        return Math.max(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Minimum similarity in percent of both submissions.
     */
    public final float minimalPercent() {
        return Math.min(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Similarity in percent.
     */
    public final float similarity() {
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
    public final float similarityOfFirst() {
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
    public final float similarityOfSecond() {
        int divisor;
        if (secondBaseCodeMatches != null) {
            divisor = secondSubmission.getNumberOfTokens() - secondSubmission.files.size() - secondBaseCodeMatches.getNumberOfMatchedTokens();
        } else {
            divisor = secondSubmission.getNumberOfTokens() - secondSubmission.files.size();
        }
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }

    /**
     * @return Similarity in percent rounded down to the nearest tenth.
     */
    public final float roundedSimilarity() {
        return ((int) (similarity() * ROUNDING_FACTOR)) / (float) ROUNDING_FACTOR;
    }

    /**
     * @return Similarity of the first submission to the basecode in percent rounded down to the nearest tenth.
     */
    public final float basecodeSimilarityOfFirst() {
        return ((int) (firstBasecodeSimilarity() * ROUNDING_FACTOR)) / (float) ROUNDING_FACTOR;
    }
    
    /**
     * @return Similarity of the second submission to the basecode in percent rounded down to the nearest tenth.
     */
    public final float basecodeSimilarityOfSecond() {
        return ((int) (secondBasecodeSimilarity() * ROUNDING_FACTOR)) / (float) ROUNDING_FACTOR;
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

    /*
     * s==0 uses the start indexes of 1st submission as key for the sorting algorithm. Otherwise the start indexes of 2nd submission are used.
     */
    public final int[] sort_permutation(int s) {   // bubblesort!!!
        int size = matches.size();
        int[] perm = new int[size];
        int i, j, tmp;

        // initialize permutation array
        for (i = 0; i < size; i++) {
            perm[i] = i;
        }

        if (s == 0) {     // First Submission
            for (i = 1; i < size; i++) {
                for (j = 0; j < (size - i); j++) {
                    if (matches.get(perm[j]).getStartOfFirst() > matches.get(perm[j + 1]).getStartOfFirst()) {
                        tmp = perm[j];
                        perm[j] = perm[j + 1];
                        perm[j + 1] = tmp;
                    }
                }
            }
        } else {        // Second submission
            for (i = 1; i < size; i++) {
                for (j = 0; j < (size - i); j++) {
                    if (matches.get(perm[j]).getStartOfSecond() > matches.get(perm[j + 1]).getStartOfSecond()) {
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

    private final float firstBasecodeSimilarity() {
        float sa = firstSubmission.getNumberOfTokens() - firstSubmission.files.size();
        return firstBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sa;
    }

    private final float secondBasecodeSimilarity() {
        float sb = secondSubmission.getNumberOfTokens() - secondSubmission.files.size();
        return secondBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sb;
    }

}
