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
import de.jplag.Token;

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
            leftSubmission = comparisons.get(i).firstSubmission().clone();
            rightSubmission = comparisons.get(i).secondSubmission().clone();
            globalMatches = new ArrayList<>(comparisons.get(i).matches());
            globalMatches.addAll(comparisons.get(i).ignoredMatches());
            computeNeighbors();
            mergeNeighbors();
            removeBuffer();
            // System.out.println(globalMatches);
            comparisons.set(i, new JPlagComparison(leftSubmission, rightSubmission, globalMatches, new ArrayList<>()));

        }
        return new JPlagResult(comparisons, result.getSubmissions(), result.getDuration(), options);
    }

    private void computeNeighbors() {
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

    private void mergeNeighbors() {
        int i = 0;
        while (i < neighbors.size()) {
            int lengthUpper=neighbors.get(i).get(0).length();
            int lengthLower=neighbors.get(i).get(1).length();
            int seperatingLeft = neighbors.get(i).get(1).startOfFirst() - neighbors.get(i).get(0).endOfFirst() - 1;
            int seperatingRight = neighbors.get(i).get(1).startOfSecond() - neighbors.get(i).get(0).endOfSecond() - 1;
            double seperating = (seperatingLeft + seperatingRight) / 2.0;
            // Checking length is not necessary as GST already checked length while computing matches
            if (seperating <= seperatingThreshold) {
                System.out.println((lengthUpper+lengthLower)/2.0 + " " + seperating);
                System.out.println("Original:" + neighbors.get(i));
                globalMatches.removeAll(neighbors.get(i));
                System.out.println("Merged:" + new Match(neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(),lengthUpper+lengthLower));
                globalMatches.add(new Match(neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(),lengthUpper+lengthLower));
                removeToken(neighbors.get(i).get(0).startOfFirst(), neighbors.get(i).get(0).startOfSecond(),lengthUpper,seperatingLeft,seperatingRight);
                // Manuelles ändern der Nachbarn wäre schneller
                computeNeighbors();
                i = 0;
            } else {
                i++;
            }
        }
    }
    
    private void removeToken(int startLeft,int startRight,int lengthUpper,int seperatingLeft,int seperatingRight) {
        List<Token> tokenLeft=new ArrayList<>(leftSubmission.getTokenList());
        List<Token> tokenRight=new ArrayList<>(rightSubmission.getTokenList());
        tokenLeft.subList(startLeft+lengthUpper,startLeft+lengthUpper+seperatingLeft).clear();
        tokenRight.subList(startRight+lengthUpper,startRight+lengthUpper+seperatingRight).clear();
        leftSubmission.setTokenList(tokenLeft);
        rightSubmission.setTokenList(tokenRight);
        

        for (int i = 0; i < globalMatches.size(); i++) {
            if(globalMatches.get(i).startOfFirst()>startLeft) {
                Match alteredMatch = new Match(globalMatches.get(i).startOfFirst()-seperatingLeft,globalMatches.get(i).startOfSecond(),globalMatches.get(i).length());
                globalMatches.set(i, alteredMatch);
            }
            if(globalMatches.get(i).startOfSecond()>startRight) {
                Match alteredMatch = new Match(globalMatches.get(i).startOfFirst(),globalMatches.get(i).startOfSecond()-seperatingRight,globalMatches.get(i).length());
                globalMatches.set(i, alteredMatch);
            }
        }  
    }

    private void removeBuffer() {
        List<Match> toRemove = new ArrayList<Match>();
        for (Match m : globalMatches) {
            if (m.length() < minimumTokenMatch) {
                toRemove.add(m);
            }
        }
        globalMatches.removeAll(toRemove);
    }
}