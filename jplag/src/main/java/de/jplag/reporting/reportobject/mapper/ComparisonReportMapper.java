package de.jplag.reporting.reportobject.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.*;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.FilesOfSubmission;
import de.jplag.reporting.reportobject.model.Match;

public class ComparisonReportMapper {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonReportMapper.class);
    private final HashMap<Long, String> lineLookUpTable = new HashMap<>();
    private long currentLineIndex;

    /**
     * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult.
     * @param jPlagResult The JPlagResult to generate the comparison reports from.
     * @return A ComparisonReportMapperResult consisting of two DTOs: the list of ComparisonsReport. A ComparisonReport
     * contains information about a comparison between two submission - including their files. These files are not saved in
     * plain text though, they are saved as numbers. these numbers are indices to the second DTO of the
     * ComparisonReportMapperResult, the lineLookUpTable.
     */
    public ComparisonReportMapperResult generateComparisonReports(JPlagResult jPlagResult) {
        List<ComparisonReport> comparisons = new ArrayList<>();
        int maxNumOfComparisons = jPlagResult.getOptions().getMaximumNumberOfComparisons();
        jPlagResult.getComparisons(maxNumOfComparisons).forEach(comparison -> comparisons.add( //
                new ComparisonReport(comparison.getFirstSubmission().getName(), //
                        comparison.getSecondSubmission().getName(), //
                        comparison.similarity(), //
                        getFilesForSubmission(comparison.getFirstSubmission()), //
                        getFilesForSubmission(comparison.getSecondSubmission()), //
                        convertMatchesToReportMatches(jPlagResult, comparison) //
                )));
        return new ComparisonReportMapperResult(comparisons, lineLookUpTable);
    }

    private List<FilesOfSubmission> getFilesForSubmission(Submission submission) {
        return submission.getFiles().stream().map(file -> new FilesOfSubmission(file.getName(), readFileLines(file))).collect(Collectors.toList());
    }

    private List<Long> readFileLines(File file) {
        List<Long> lineIndices = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!lineLookUpTable.containsValue(line)) {
                    lineLookUpTable.put(currentLineIndex, line);
                    lineIndices.add(currentLineIndex);
                    currentLineIndex++;
                } else {
                    lineIndices.add(getIndexOfLine(line));
                }
            }
        } catch (IOException exception) {
            logger.error("Could not read file: " + exception.getMessage());
        }
        return lineIndices;
    }

    private Long getIndexOfLine(String line) {
        return lineLookUpTable.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), line)).map(Map.Entry::getKey).findFirst()
                .orElseThrow();
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

    public record ComparisonReportMapperResult(List<ComparisonReport> comparisonReports, Map<Long, String> lineLookupTable) {
    }

}
