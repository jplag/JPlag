package de.jplag.reporting.reportobject.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.FilesOfSubmission;

public class ComparisonReportMapperTest extends TestBase {

    private final ComparisonReportMapper comparisonReportMapper = new ComparisonReportMapper();

    @Test
    public void test() throws ExitException {
        // given

        JPlagResult jPlagResult = runJPlagWithDefaultOptions("PartialPlagiarism");

        // when
        var actualMapperResult = comparisonReportMapper.generateComparisonReports(jPlagResult);
        // then
        assertSubmissionFilesAreCorrect(jPlagResult, actualMapperResult, "A", "B", "C", "D", "E");
    }

    private void assertSubmissionFilesAreCorrect(JPlagResult jPlagResult, ComparisonReportMapper.ComparisonReportMapperResult actualMapperResult,
            String... submissionNames) {
        for (String submissionName : submissionNames) { // per submission
            Map<String, List<String>> fileNameToFileLinesExpected = getAllFilesOfSubmission(submissionName, jPlagResult); // collect all files in a
                                                                                                                          // map [fileName]->
                                                                                                                          // <linesOfFile>

            var unresolvedLinesFilesActual = actualFilesOfSubmission(submissionName, actualMapperResult); // get all files of submission from mapping
                                                                                                          // result.
            // their lines are unresolved, so they are only numbers until we looked them up with the lineLookUpTable

            for (FilesOfSubmission fileOfSubmissionActual : unresolvedLinesFilesActual) { // per mapped submissionFile
                var resolvedLinesOfFile = fileOfSubmissionActual.lines().stream().map(actualMapperResult.lineLookupTable()::get).toList(); // resolve
                                                                                                                                           // lines
                                                                                                                                           // via
                                                                                                                                           // lookup
                var linesOfExpectedFile = fileNameToFileLinesExpected.get(fileOfSubmissionActual.fileName()); // fetch expected lines
                Assertions.assertIterableEquals(linesOfExpectedFile, resolvedLinesOfFile); // compare
            }
        }

    }

    private List<FilesOfSubmission> actualFilesOfSubmission(String submissionName,
            ComparisonReportMapper.ComparisonReportMapperResult actualMapperResult) {
        return actualMapperResult.comparisonReports().stream().filter(comparisonReportContainsSubmission(submissionName)).findFirst()
                .map(extractFilesOfSubmission(submissionName)).orElseThrow();
    }

    private Function<ComparisonReport, List<FilesOfSubmission>> extractFilesOfSubmission(String submissionName) {
        return report -> report.secondSubmissionId().equals(submissionName) ? report.filesOfSecondSubmission() : report.filesOfFirstSubmission();
    }

    private Predicate<ComparisonReport> comparisonReportContainsSubmission(String submissionName) {
        return comparisonReport -> comparisonReport.firstSubmissionId().equals(submissionName)
                || comparisonReport.secondSubmissionId().equals(submissionName);
    }

    private Map<String, List<String>> getAllFilesOfSubmission(String submissionName, JPlagResult jPlagResult) {
        return jPlagResult.getAllComparisons().stream().filter(comparisonContainsSubmission(submissionName)).findFirst()
                .map(getFilesOfComparison(submissionName)).orElseThrow().stream().collect(Collectors.toMap(File::getName, this::readLines));
    }

    private Function<JPlagComparison, Collection<File>> getFilesOfComparison(String submissionName) {
        return comparison -> comparison.getFirstSubmission().getName().equals(submissionName) ? comparison.getFirstSubmission().getFiles()
                : comparison.getSecondSubmission().getFiles();
    }

    private Predicate<JPlagComparison> comparisonContainsSubmission(String submissionName) {
        return comparison -> comparison.getFirstSubmission().getName().equals(submissionName)
                || comparison.getSecondSubmission().getName().equals(submissionName);
    }

    private List<String> readLines(File file) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ignored) {
        }

        return lines;
    }
}
