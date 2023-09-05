package de.jplag.endtoend.model;

import java.util.Collection;
import java.util.Set;

import de.jplag.JPlagComparison;
import de.jplag.endtoend.helper.AverageCalculator;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The data for the gold standard.
 * @param matchAverage The average similarity of all submission in the gold standard
 * @param nonMatchAverage The average similarity of all submission outside the gold standard
 */
public record GoldStandard(@JsonProperty double matchAverage, @JsonProperty double nonMatchAverage) {
    public static GoldStandard buildFromComparisons(Collection<JPlagComparison> comparisonList, Set<ComparisonIdentifier> comparisonIdentifiers) {
        AverageCalculator match = new AverageCalculator();
        AverageCalculator nonMatch = new AverageCalculator();

        for (JPlagComparison comparison : comparisonList) {
            ComparisonIdentifier comparisonIdentifier = new ComparisonIdentifier(comparison.firstSubmission().getName(),
                    comparison.secondSubmission().getName());
            if (comparisonIdentifiers.contains(comparisonIdentifier)) {
                match.add(comparison.similarity());
            } else {
                nonMatch.add(comparison.similarity());
            }
        }

        return new GoldStandard(match.calculate(), nonMatch.calculate());
    }
}
