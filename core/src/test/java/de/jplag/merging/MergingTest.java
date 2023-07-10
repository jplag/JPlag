package de.jplag.merging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.TestBase;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.options.JPlagOptions;
import de.jplag.strategy.ComparisonStrategy;
import de.jplag.strategy.ParallelComparisonStrategy;

class MergingTest extends TestBase {
    private JPlagOptions options;
    private JPlagResult result;
    private List<Match> matches;
    private List<JPlagComparison> comparisonsBefore;
    private List<JPlagComparison> comparisonsAfter;
    //private static final Logger LOGGER = LoggerFactory.getLogger(MergingTest.class);
    MergingTest() throws ExitException {
        options = getDefaultOptions("merging").withMergingParameters(new MergingParameters(8,2));
        
        GreedyStringTiling coreAlgorithm = new GreedyStringTiling(options);
        ComparisonStrategy comparisonStrategy = new ParallelComparisonStrategy(options, coreAlgorithm);

        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();

        result = comparisonStrategy.compareSubmissions(submissionSet);
        comparisonsBefore=result.getAllComparisons();
        
        result = new MatchMerging(result, options).run();
        comparisonsAfter=result.getAllComparisons();
    }

    @Test
    void testBufferRemoval() {
        boolean allAboveMTM=true;
        for(int i=0; i< comparisonsAfter.size();i++) {
            matches = comparisonsAfter.get(i).matches();
            for(int j=0;j<matches.size();j++) {
                if(matches.get(j).length()<options.minimumTokenMatch()) {
                    allAboveMTM=false;
                }
            }
        }
        assert allAboveMTM;
    }
    
    @Test
    void testGSTMatches() {
        boolean allAboveMTM=true;
        for(int i=0; i< comparisonsBefore.size();i++) {
            matches = comparisonsBefore.get(i).matches();
            for(int j=0;j<matches.size();j++) {
                if(matches.get(j).length()<options.minimumTokenMatch()) {
                    allAboveMTM=false;
                }
            }
        }
        assert allAboveMTM;
    }
    @Test
    void testGSTIgnoredMatches() {
        boolean allAboveMB=true;
        
        for(int i=0; i< comparisonsBefore.size();i++) {
            matches = comparisonsBefore.get(i).ignoredMatches();
            for(int j=0;j<matches.size();j++) {
                if(matches.get(j).length()<options.minimumTokenMatch()-options.mergingParameters().mergeBuffer()) {
                    allAboveMB=false;
                }
            }
        }
        assert allAboveMB;
    }
    
    @Test
    void testSimilarityIncreased() {
        boolean decreasedSimilarity = false;
        for(int i=0; i< comparisonsAfter.size();i++) {
            if(comparisonsAfter.get(i).similarity()<comparisonsBefore.get(i).similarity()) {
                decreasedSimilarity=true;
            }
        }
        assert !decreasedSimilarity;
    }
    
    @Test
    void testFewerMatches() {
        boolean fewerMatches=true;
        for(int i=0; i< comparisonsAfter.size();i++) {
            if(comparisonsAfter.get(i).matches().size()+comparisonsAfter.get(i).ignoredMatches().size()>comparisonsBefore.get(i).matches().size()+comparisonsBefore.get(i).ignoredMatches().size()) {
                fewerMatches=false;
            }
        }
        assert fewerMatches;
    }
    
    @Test
    void testFewerToken() {
        boolean fewerToken=true;
        for(int i=0; i< comparisonsAfter.size();i++) {
            if(comparisonsAfter.get(i).firstSubmission().getTokenList().size()>comparisonsBefore.get(i).firstSubmission().getTokenList().size() || comparisonsAfter.get(i).secondSubmission().getTokenList().size()>comparisonsBefore.get(i).secondSubmission().getTokenList().size()) {
                fewerToken=false;
            }
        }
        assert fewerToken;
    }
    
    @Test
    void testCorrectMerges() {
        boolean correctMerges=true;
        for(int i=0; i< comparisonsAfter.size();i++) {
            matches = comparisonsAfter.get(i).matches();
            List<Match> sortedByFirst= new ArrayList<>(comparisonsBefore.get(i).matches());
            sortedByFirst.addAll(comparisonsBefore.get(i).ignoredMatches());
            Collections.sort(sortedByFirst, (m1, m2) -> m1.startOfFirst() - m2.startOfFirst());
            for(int j=0;j<matches.size();j++) {
                int begin=-1;
                for(int k=0; k< sortedByFirst.size();k++) {
                    if(sortedByFirst.get(k).startOfFirst()==matches.get(j).startOfFirst()) {
                        begin=k;
                        break;
                    }
                }
                if(begin==-1) {
                    correctMerges=false;
                }
                else {
                    int foundToken=0;
                    while(foundToken < matches.get(j).length()) {
                        foundToken+=sortedByFirst.get(begin).length();
                        begin++;
                        if(foundToken > matches.get(j).length()) {
                            correctMerges=false;
                        }
                    }
                }
            }
        }
        assert correctMerges;
    }
}