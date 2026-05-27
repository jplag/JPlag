package de.jplag.endtoend.model;

import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.Set;

import de.jplag.JPlagComparison;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The data for the gold standard.
 * @param matchAverage The average similarity of all submission in the gold standard
 * @param nonMatchAverage The average similarity of all submission outside the gold standard
 */
public record GoldStandard(@JsonProperty double matchAverage, @JsonProperty double nonMatchAverage) {

    /**
     * Builds a GoldStandard object from a collection of comparisons.
     * @param comparisonList the collection of JPlagComparison objects
     * @param comparisonIdentifiers the set of comparison identifiers considered as matches
     * @return a GoldStandard object containing average match and non-match similarities
     */
    public static GoldStandard buildFromComparisons(Collection<JPlagComparison> comparisonList, Set<ComparisonIdentifier> comparisonIdentifiers) {
        DoubleSummaryStatistics match = new DoubleSummaryStatistics();
        DoubleSummaryStatistics nonMatch = new DoubleSummaryStatistics();

        for (JPlagComparison comparison : comparisonList) {
            ComparisonIdentifier comparisonIdentifier = new ComparisonIdentifier(comparison.firstSubmission().getName(),
                    comparison.secondSubmission().getName());
            if (comparisonIdentifiers.contains(comparisonIdentifier)) {
                match.accept(comparison.similarity());
            } else {
                nonMatch.accept(comparison.similarity());
            }
        }

        return new GoldStandard(match.getAverage(), nonMatch.getAverage());
    }
}
