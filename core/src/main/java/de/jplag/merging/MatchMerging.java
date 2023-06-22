package de.jplag.merging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.options.JPlagOptions;

public class MatchMerging {
    private int minimumTokenMatch;
    private int mergeBuffer;
    private Submission leftSubmission;
    private Submission rightSubmission;
    private List<Match> globalMatches;
    private List<List<Match>> neighbors;
    private int seperatingThreshold;
    private JPlagResult result;
    private List<JPlagComparison> comparisons;
    private JPlagOptions options;

    public MatchMerging(JPlagResult r, JPlagOptions o) {
        result = r;
        comparisons = new ArrayList<>(result.getAllComparisons());
        options = o;
        minimumTokenMatch = options.minimumTokenMatch();
        mergeBuffer = o.mergingParameters().mergeBuffer();
        seperatingThreshold = o.mergingParameters().seperatingThreshold();
    }

    public JPlagResult run() {
        for (int i = 0; i < comparisons.size(); i++) {
            leftSubmission = comparisons.get(i).firstSubmission();
            rightSubmission = comparisons.get(i).secondSubmission();
            globalMatches = new ArrayList<>(comparisons.get(i).matches());
            globalMatches.addAll(comparisons.get(i).ignoredMatches());
            // System.out.println(globalMatches);
            computeNeighbors();
            mergeNeighbors();
            // System.out.println(globalMatches);
            removeBuffer();
            // System.out.println(globalMatches);
            comparisons.set(i, new JPlagComparison(leftSubmission, rightSubmission, globalMatches, new ArrayList<>()));

        }
        return new JPlagResult(comparisons, result.getSubmissions(), result.getDuration(), options);
    }

    public int getMergeBuffer() {
        return mergeBuffer;
    }

    public void computeNeighbors() {
        neighbors = new ArrayList<>();
        List<Match> sortedByFirst = new ArrayList<>(globalMatches);
        Collections.sort(sortedByFirst, (m1, m2) -> m1.startOfFirst() - m2.startOfFirst());
        List<Match> sortedBySecond = new ArrayList<>(globalMatches);
        Collections.sort(sortedBySecond, (m1, m2) -> m1.startOfSecond() - m2.startOfSecond());
        for (int i = 0; i < sortedByFirst.size() - 1; i++) {
            if (sortedBySecond.indexOf(sortedByFirst.get(i)) == (sortedBySecond.indexOf(sortedByFirst.get(i + 1)) - 1)) {
                neighbors.add(Arrays.asList(sortedByFirst.get(i), sortedByFirst.get(i + 1)));
            }
        }
        // System.out.println(neighbors);
    }

    public void mergeNeighbors() {
        int i = 0;
        while (i < neighbors.size()) {
            double length = (neighbors.get(i).get(0).length() + neighbors.get(i).get(1).length()) / 2.0;
            double seperating = ((neighbors.get(i).get(1).startOfFirst() - neighbors.get(i).get(0).endOfFirst() - 1)
                    + (neighbors.get(i).get(1).startOfSecond() - neighbors.get(i).get(0).endOfSecond() - 1)) / 2.0;
            // Checking length is not necessary as GST already checked length while computing matches
            if (seperating <= seperatingThreshold) {
                System.out.println(length + " " + seperating);
                System.out.println("Original:" + neighbors.get(i));
                globalMatches.removeAll(neighbors.get(i));
                System.out.println("Merged:" + new Match(neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(),
                        (int) (length * 2 + seperating)));
                globalMatches.add(new Match(neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(),
                        (int) (length * 2/* +seperating */)));
                i = 0;
                // Manuelles ändern wäre schneller
                computeNeighbors();
            } else {
                i++;
            }
        }
    }

    public void removeBuffer() {
        List<Match> toRemove = new ArrayList<Match>();
        for (Match m : globalMatches) {
            if (m.length() < minimumTokenMatch) {
                toRemove.add(m);
            }
        }
        globalMatches.removeAll(toRemove);
    }

    public Submission getLeftSubmission() {
        return leftSubmission;
    }

    public Submission getRightSubmission() {
        return rightSubmission;
    }

    public List<Match> getGlobalMatches() {
        return globalMatches;
    }
}