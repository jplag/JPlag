package de.jplag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This method represents the whole result of a comparison between two submissions.
 */
public class JPlagComparison implements Comparator<JPlagComparison> { // FIXME TS: contains a lot of code duplication

    private static final int ROUNDING_FACTOR = 10;

    private final Submission firstSubmission;
    private final Submission secondSubmission;

    private JPlagComparison firstBaseCodeMatches = null;
    private JPlagComparison secondBaseCodeMatches = null;

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
        int i, h, starti, starth, count = 1;

        o1: for (i = 1; i < matches.size(); i++) {
            starti = matches.get(i).getStart(j == 0);
            for (h = 0; h < i; h++) {
                starth = matches.get(h).getStart(j == 0);
                if (tokenList.getToken(starti).file.equals(tokenList.getToken(starth).file)) {
                    continue o1;
                }
            }
            count++;
        }

        String[] res = new String[count];
        res[0] = tokenList.getToken(matches.get(0).getStart(j == 0)).file;
        count = 1;

        o2: for (i = 1; i < matches.size(); i++) {
            starti = matches.get(i).getStart(j == 0);
            for (h = 0; h < i; h++) {
                starth = matches.get(h).getStart(j == 0);
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
        float sa, sb;
        if (secondBaseCodeMatches != null && firstBaseCodeMatches != null) {
            sa = firstSubmission.getNumberOfTokens() - firstSubmission.getFiles().size() - firstBaseCodeMatches.getNumberOfMatchedTokens();
            sb = secondSubmission.getNumberOfTokens() - secondSubmission.getFiles().size() - secondBaseCodeMatches.getNumberOfMatchedTokens();
        } else {
            sa = firstSubmission.getNumberOfTokens() - firstSubmission.getFiles().size();
            sb = secondSubmission.getNumberOfTokens() - secondSubmission.getFiles().size();
        }
        return (200 * getNumberOfMatchedTokens()) / (sa + sb);
    }

    /**
     * @return Similarity in percent for the first submission (what percent of the first submission is similar to the
     * second).
     */
    public final float similarityOfFirst() {
        int divisor;
        if (firstBaseCodeMatches != null) {
            divisor = firstSubmission.getNumberOfTokens() - firstSubmission.getFiles().size() - firstBaseCodeMatches.getNumberOfMatchedTokens();
        } else {
            divisor = firstSubmission.getNumberOfTokens() - firstSubmission.getFiles().size();
        }
        return (divisor == 0 ? 0f : (getNumberOfMatchedTokens() * 100 / (float) divisor));
    }

    /**
     * @return Similarity in percent for the second submission (what percent of the second submission is similar to the
     * first).
     */
    public final float similarityOfSecond() {
        int divisor;
        if (secondBaseCodeMatches != null) {
            divisor = secondSubmission.getNumberOfTokens() - secondSubmission.getFiles().size() - secondBaseCodeMatches.getNumberOfMatchedTokens();
        } else {
            divisor = secondSubmission.getNumberOfTokens() - secondSubmission.getFiles().size();
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
        float sa = firstSubmission.getNumberOfTokens() - firstSubmission.getFiles().size();
        return firstBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sa;
    }

    private final float secondBasecodeSimilarity() {
        float sb = secondSubmission.getNumberOfTokens() - secondSubmission.getFiles().size();
        return secondBaseCodeMatches.getNumberOfMatchedTokens() * 100 / sb;
    }

}
