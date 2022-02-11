package de.jplag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * This method represents the whole result of a comparison between two submissions.
 */
public class JPlagComparison implements Comparator<JPlagComparison> { // FIXME TS: contains a lot of code duplication

    private static final int ROUNDING_FACTOR = 10;

    private final Submission firstSubmission;
    private final Submission secondSubmission;

    private final List<Match> matches;

    public JPlagComparison(Submission firstSubmission, Submission secondSubmission) {
        this.firstSubmission = firstSubmission;
        this.secondSubmission = secondSubmission;
        matches = new ArrayList<>();
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
     * This method returns all the files which contributed to a match. Parameter: j == 0 1st submission, j != 0 2nd
     * submission.
     */
    public final String[] files(int j) {
        if (matches.size() == 0) {
            return new String[] {};
        }

        TokenList tokenList = (j == 0 ? firstSubmission : secondSubmission).getTokenList();

        /*
         * Collect the file names of the first token of each match.
         */
        Set<String> collectedFiles = new LinkedHashSet<>();
        for (Match match : matches) {
            collectedFiles.add(tokenList.getToken(match.getStart(j == 0)).getFile());
        }

        /*
         * sort by file name. (so that equally named files are displayed approximately side by side.)
         */
        String[] res = collectedFiles.toArray(new String[0]);
        Arrays.sort(res);

        return res;
    }

    /**
     * @return the base code matches of the first submission.
     */
    public JPlagComparison getFirstBaseCodeMatches() {
        return firstSubmission.getBaseCodeComparison();
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
        return secondSubmission.getBaseCodeComparison();
    }

    /**
     * @return the second of the two submissions.
     */
    public Submission getSecondSubmission() {
        return secondSubmission;
    }

    /**
     * @param getFirst Whether to return the first submission, else return the second submission.
     * @return The requested submission.
     */
    public Submission getSubmission(boolean getFirst) {
        return getFirst ? firstSubmission : secondSubmission;
    }

    /**
     * @param getFirst Whether to return the first basecode matches, else return the second basecode matches.
     * @return The requested basecode matches.
     */
    public JPlagComparison getBaseCodeMatches(boolean getFirst) {
        return getSubmission(getFirst).getBaseCodeComparison();
    }

    /**
     * @return Maximum similarity in percent of both submissions.
     */
    public final float maximalSimilarity() {
        return Math.max(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Minimum similarity in percent of both submissions.
     */
    public final float minimalSimilarity() {
        return Math.min(similarityOfFirst(), similarityOfSecond());
    }

    /**
     * @return Similarity in percent (what percentage of tokens across both submissions are matched).
     */
    public final float similarity() {
        boolean subtractBaseCode = firstSubmission.hasBaseCodeMatches() && secondSubmission.hasBaseCodeMatches();
        float sa = firstSubmission.getSimilarityDivisor(subtractBaseCode);
        float sb = secondSubmission.getSimilarityDivisor(subtractBaseCode);
        return (200 * getNumberOfMatchedTokens()) / (sa + sb);
    }

    /**
     * @return Similarity in percent for the first submission (what percent of the first submission is similar to the
     * second).
     */
    public final float similarityOfFirst() {
        int divisor = firstSubmission.getSimilarityDivisor(true);
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }

    /**
     * @return Similarity in percent for the second submission (what percent of the second submission is similar to the
     * first).
     */
    public final float similarityOfSecond() {
        int divisor = secondSubmission.getSimilarityDivisor(true);
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
     * Creates a permutation for the matches based on the indices of the matched token groups.
     * @param useFirst determines whether the start of the first or second submission is compared.
     * @return the permutation indices.
     */
    public final List<Integer> sort_permutation(boolean useFirst) {
        List<Integer> indices = new ArrayList<>(matches.size());
        IntStream.range(0, matches.size()).forEach(index -> indices.add(index));
        Comparator<Integer> comparator = (Integer i, Integer j) -> {
            return Integer.compare(selectStartof(i, useFirst), selectStartof(j, useFirst));
        };
        Collections.sort(indices, comparator);
        return indices;
    }

    private int selectStartof(Integer index, boolean useFirst) {
        Match match = matches.get(index);
        return match.getStart(useFirst);
    }

    @Override
    public String toString() {
        return firstSubmission.getName() + " <-> " + secondSubmission.getName();
    }

    private final float firstBasecodeSimilarity() {
        float sa = firstSubmission.getSimilarityDivisor(false);
        JPlagComparison firstBaseCodeMatches = firstSubmission.getBaseCodeComparison();
        return firstBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sa;
    }

    private final float secondBasecodeSimilarity() {
        float sb = secondSubmission.getSimilarityDivisor(false);
        JPlagComparison secondBaseCodeMatches = secondSubmission.getBaseCodeComparison();
        return secondBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sb;
    }
}
