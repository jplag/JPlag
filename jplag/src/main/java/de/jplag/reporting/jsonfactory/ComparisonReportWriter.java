package de.jplag.reporting.jsonfactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.jplag.*;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.Match;

public class ComparisonReportWriter {

    private static final FileWriter FILE_WRITER = new FileWriter();
    private final Function<Submission, String> submissionToIdFunction;
    private final Map<String, Map<String, String>> submissionIdToComparisonFileName = new HashMap<>();

    public ComparisonReportWriter(Function<Submission, String> submissionToIdFunction) {
        this.submissionToIdFunction = submissionToIdFunction;
    }

    /**
     * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult.
     * @param jPlagResult The JPlagResult to generate the comparison reports from. contains information about a comparison
     * between two submission. The JPlagResult is used to extract the information on matches between two submissions.
     * @return
     */
    public Map<String, Map<String, String>> writeComparisonReports(JPlagResult jPlagResult, String path) {
        int numberOfComparisons = jPlagResult.getOptions().getMaximumNumberOfComparisons();
        List<JPlagComparison> comparisons = jPlagResult.getComparisons(numberOfComparisons);
        writeComparisons(jPlagResult, path, comparisons);
        return submissionIdToComparisonFileName;
    }

    private void writeComparisons(JPlagResult jPlagResult, String path, List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            String firstSubmissionId = getId(comparison.getFirstSubmission());
            String secondSubmissionId = getId(comparison.getSecondSubmission());
            String fileName = generateComparisonName(firstSubmissionId, secondSubmissionId);
            addToLookUp(firstSubmissionId, secondSubmissionId, fileName);
            var comparisonReport = new ComparisonReport(firstSubmissionId, secondSubmissionId, comparison.similarity(),
                    convertMatchesToReportMatches(jPlagResult, comparison));
            FILE_WRITER.saveAsJSON(comparisonReport, path, fileName);
        }
    }

    private void addToLookUp(String firstSubmissionId, String secondSubmissionId, String fileName) {
        writeToMap(secondSubmissionId, firstSubmissionId, fileName);
        writeToMap(firstSubmissionId, secondSubmissionId, fileName);
    }

    private void writeToMap(String id1, String id2, String comparisonFileName) {
        if (submissionIdToComparisonFileName.containsKey(id1)) {
            submissionIdToComparisonFileName.get(id1).put(id2, comparisonFileName);
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put(id2, comparisonFileName);
            submissionIdToComparisonFileName.put(id1, map);
        }
    }

    private String generateComparisonName(String firstSubmissionId, String secondSubmissionId) {
        return firstSubmissionId.concat("-").concat(secondSubmissionId).concat(".json");

    }

    private String getId(Submission submission) {
        return submissionToIdFunction.apply(submission);
    }

    private List<Match> convertMatchesToReportMatches(JPlagResult result, JPlagComparison comparison) {
        return comparison.getMatches().stream()
                .map(match -> convertMatchToReportMatch(comparison, match, result.getOptions().getLanguage().supportsColumns())).toList();
    }

    private Match convertMatchToReportMatch(JPlagComparison comparison, de.jplag.Match match, boolean languageSupportsColumnsAndLines) {
        TokenList tokensFirst = comparison.getFirstSubmission().getTokenList();
        TokenList tokensSecond = comparison.getSecondSubmission().getTokenList();
        Token startTokenFirst = tokensFirst.getToken(match.startOfFirst());
        Token endTokenFirst = tokensFirst.getToken(match.startOfFirst() + match.length() - 1);
        Token startTokenSecond = tokensSecond.getToken(match.startOfSecond());
        Token endTokenSecond = tokensSecond.getToken(match.startOfSecond() + match.length() - 1);

        int startFirst = getPosition(languageSupportsColumnsAndLines, startTokenFirst);
        int endFirst = getPosition(languageSupportsColumnsAndLines, endTokenFirst);
        int startSecond = getPosition(languageSupportsColumnsAndLines, startTokenSecond);
        int endSecond = getPosition(languageSupportsColumnsAndLines, endTokenSecond);
        int tokens = match.length();

        return new Match(startTokenFirst.getFile(), startTokenSecond.getFile(), startFirst, endFirst, startSecond, endSecond, tokens);
    }

    private int getPosition(boolean languageSupportsColumnsAndLines, Token token) {
        return languageSupportsColumnsAndLines ? token.getLine() : token.getIndex();
    }

}
