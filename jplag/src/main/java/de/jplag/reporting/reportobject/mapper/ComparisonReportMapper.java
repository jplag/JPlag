package de.jplag.reporting.reportobject.mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Token;
import de.jplag.TokenList;
import de.jplag.reporting.jsonfactory.JsonWriter;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.Match;

public class ComparisonReportMapper {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonReportMapper.class);
    private static final JsonWriter jsonWriter = new JsonWriter();

    /**
     * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult.
     * @param jPlagResult The JPlagResult to generate the comparison reports from.
     * contains information about a comparison between two submission - including their files. These files are not saved in
     * plain text though, they are saved as numbers. these numbers are indices to the second DTO of the
     * ComparisonReportMapperResult, the lineLookUpTable.
     */
    public void writeComparisonReports(JPlagResult jPlagResult, String path) {
        int maxNumOfComparisons = jPlagResult.getOptions().getMaximumNumberOfComparisons();

        List<JPlagComparison> comparisons = jPlagResult.getComparisons(maxNumOfComparisons);

        var allSubmissions = comparisons.stream().map(JPlagComparison::getFirstSubmission).collect(Collectors.toSet());
        for (var submission : allSubmissions) {

            File directory = new File(path.concat("/").concat(submission.getName()));
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    logger.error("Failed to create dir.");
                }
            }
            for (var file : submission.getFiles()) {

                try {
                    Files.copy(file.toPath(), (new File(directory, file.getName())).toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (JPlagComparison comparison : comparisons) {
            var comparisonReport = new ComparisonReport(comparison.getFirstSubmission().getName(), comparison.getSecondSubmission().getName(),
                    comparison.similarity(), convertMatchesToReportMatches(jPlagResult, comparison));
            String fileName = comparisonReport.firstSubmissionId().concat("-").concat(comparisonReport.secondSubmissionId()).concat(".json");
            jsonWriter.saveFile(comparisonReport, path, fileName);
        }
    }

    private List<Match> convertMatchesToReportMatches(JPlagResult result, JPlagComparison comparison) {
        return comparison.getMatches().stream()
                .map(match -> convertMatchToReportMatch(comparison, match, result.getOptions().getLanguage().usesIndex()))
                .collect(Collectors.toList());
    }

    /**
     * Converts a JPlag Match object to a Match DTO.
     * @param comparison The comparison from which the match originates.
     * @param match The match to be converted.
     * @param usesIndex Indicates whether the language uses indexes.
     * @return A Match DTO.
     */
    private Match convertMatchToReportMatch(JPlagComparison comparison, de.jplag.Match match, Boolean usesIndex) {
        TokenList tokensFirst = comparison.getFirstSubmission().getTokenList();
        TokenList tokensSecond = comparison.getSecondSubmission().getTokenList();
        Token startTokenFirst = tokensFirst.getToken(match.getStartOfFirst());
        Token endTokenFirst = tokensFirst.getToken(match.getStartOfFirst() + match.getLength() - 1);
        Token startTokenSecond = tokensSecond.getToken(match.getStartOfSecond());
        Token endTokenSecond = tokensSecond.getToken(match.getStartOfSecond() + match.getLength() - 1);

        int startFirst = usesIndex ? startTokenFirst.getIndex() : startTokenFirst.getLine();
        int endFirst = usesIndex ? endTokenFirst.getIndex() : endTokenFirst.getLine();
        int startSecond = usesIndex ? startTokenSecond.getIndex() : startTokenSecond.getLine();
        int endSecond = usesIndex ? endTokenSecond.getIndex() : endTokenSecond.getLine();
        int tokens = match.getLength();

        return new Match(startTokenFirst.getFile(), startTokenSecond.getFile(), startFirst, endFirst, startSecond, endSecond, tokens);
    }

}
