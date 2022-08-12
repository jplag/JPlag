package de.jplag.reporting.reportobject.mapper;

import java.util.List;

import de.jplag.*;
import de.jplag.reporting.jsonfactory.FileWriter;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.Match;

public class ComparisonReportMapper {

    private static final FileWriter FILE_WRITER = new FileWriter();

    /**
     * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult.
     * @param jPlagResult The JPlagResult to generate the comparison reports from. contains information about a comparison
     * between two submission. The JPlagResult is used to extract the information on matches between two submissions.
     */
    public void writeComparisonReports(JPlagResult jPlagResult, String path) {
        int numberOfComparisons = jPlagResult.getOptions().getMaximumNumberOfComparisons();
        List<JPlagComparison> comparisons = jPlagResult.getComparisons(numberOfComparisons);
        writeComparisons(path, comparisons);
    }

    private void writeComparisons(String path, List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            var comparisonReport = new ComparisonReport(comparison.getFirstSubmission().getName(), comparison.getSecondSubmission().getName(),
                    comparison.similarity(), convertMatchesToReportMatches(comparison));
            String fileName = comparisonReport.firstSubmissionId().concat("-").concat(comparisonReport.secondSubmissionId()).concat(".json");
            FILE_WRITER.saveAsJSON(comparisonReport, path, fileName);
        }
    }

    private List<Match> convertMatchesToReportMatches(JPlagComparison comparison) {
        return comparison.getMatches().stream().map(match -> convertMatchToReportMatch(comparison, match)).toList();
    }

    private Match convertMatchToReportMatch(JPlagComparison comparison, de.jplag.Match match) {
        TokenList tokensFirst = comparison.getFirstSubmission().getTokenList();
        TokenList tokensSecond = comparison.getSecondSubmission().getTokenList();
        Token startTokenFirst = tokensFirst.getToken(match.startOfFirst());
        Token endTokenFirst = tokensFirst.getToken(match.startOfFirst() + match.length() - 1);
        Token startTokenSecond = tokensSecond.getToken(match.startOfSecond());
        Token endTokenSecond = tokensSecond.getToken(match.startOfSecond() + match.length() - 1);

        int startFirst = startTokenFirst.getLine();
        int endFirst = endTokenFirst.getLine();
        int startSecond = startTokenSecond.getLine();
        int endSecond = endTokenSecond.getLine();
        int tokens = match.length();

        return new Match(startTokenFirst.getFile(), startTokenSecond.getFile(), startFirst, endFirst, startSecond, endSecond, tokens);
    }
}
