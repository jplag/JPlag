package de.jplag.reporting.reportobject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.*;
import de.jplag.reporting.reportobject.mapper.ClusteringResultMapper;
import de.jplag.reporting.reportobject.model.*;
import de.jplag.reporting.reportobject.model.Match;

/**
 * Factory class, responsible for converting a JPlagResult object to Overview and Comparison DTO classes.
 */
public class ReportObjectFactory {

    private static final Logger logger = LoggerFactory.getLogger(ReportObjectFactory.class);
    private static final ClusteringResultMapper clusteringResultMapper = new ClusteringResultMapper();

    /**
     * Converts a JPlagResult to a JPlagReport.
     * @return JPlagReport for the given JPlagResult.
     */
    public static JPlagReport getReportObject(JPlagResult result) {
        OverviewReport overviewReport = generateOverviewReport(result);
        List<ComparisonReport> comparisons = generateComparisonReports(result);
        return new JPlagReport(overviewReport, comparisons);
    }

    /**
     * Generates an Overview DTO of a JPlagResult.
     */
    private static OverviewReport generateOverviewReport(JPlagResult result) {
        int numberOfComparisons = result.getOptions().getMaximumNumberOfComparisons();
        List<JPlagComparison> comparisons = result.getComparisons(numberOfComparisons);
        OverviewReport overviewReport = new OverviewReport();

        // TODO: Consider to treat entries that were checked differently from old entries with prior work.
        List<String> folders = new ArrayList<>();
        folders.addAll(result.getOptions().getSubmissionDirectories());
        folders.addAll(result.getOptions().getOldSubmissionDirectories());
        overviewReport.setSubmissionFolderPath(folders);

        String baseCodePath = result.getOptions().hasBaseCode() ? result.getOptions().getBaseCodeSubmissionName().orElse("") : "";
        overviewReport.setBaseCodeFolderPath(baseCodePath);

        overviewReport.setLanguage(result.getOptions().getLanguage().getName());
        overviewReport.setFileExtensions(List.of(result.getOptions().getFileSuffixes()));
        overviewReport.setSubmissionIds(extractSubmissionNames(comparisons));
        overviewReport.setFailedSubmissionNames(List.of());  // No number of failed submissions
        overviewReport.setExcludedFiles(result.getOptions().getExcludedFiles());
        overviewReport.setMatchSensitivity(result.getOptions().getMinimumTokenMatch());
        overviewReport.setDateOfExecution(getDate());
        overviewReport.setExecutionTime(result.getDuration());
        overviewReport.setComparisonNames(getComparisonNames(comparisons));
        overviewReport.setMetrics(getMetrics(result));
        overviewReport.setClusters(clusteringResultMapper.map(result));

        return overviewReport;
    }

    /**
     * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult.
     * @return A list with ComparisonReport DTOs.
     */
    private static List<ComparisonReport> generateComparisonReports(JPlagResult result) {
        List<ComparisonReport> comparisons = new ArrayList<>();
        int numberOfComparisons = result.getOptions().getMaximumNumberOfComparisons();
        result.getComparisons(numberOfComparisons).forEach(comparison -> comparisons.add( //
                new ComparisonReport(comparison.getFirstSubmission().getName(), //
                        comparison.getSecondSubmission().getName(), //
                        comparison.similarity(), //
                        getFilesForSubmission(comparison.getFirstSubmission()), //
                        getFilesForSubmission(comparison.getSecondSubmission()), //
                        convertMatchesToReportMatches(result, comparison, comparison.getMatches()) //
                )));
        return comparisons;
    }

    private static List<Match> convertMatchesToReportMatches(JPlagResult result, JPlagComparison comparison, List<de.jplag.Match> matches) {
        return matches.stream().map(match -> convertMatchToReportMatch(comparison, match, result.getOptions().getLanguage().usesIndex()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the names of all submissions.
     * @return A list containing all submission names.
     */
    private static List<String> extractSubmissionNames(List<JPlagComparison> comparisons) {
        HashSet<String> names = new HashSet<>();
        comparisons.forEach(comparison -> {
            names.add(comparison.getFirstSubmission().getName());
            names.add(comparison.getSecondSubmission().getName());
        });
        return new ArrayList<>(names);
    }

    /**
     * Gets the names of all comparison.
     * @return A list containing all comparisons.
     */
    private static List<String> getComparisonNames(List<JPlagComparison> comparisons) {
        List<String> names = new ArrayList<>();
        comparisons.forEach(
                comparison -> names.add(String.join("-", comparison.getFirstSubmission().getName(), comparison.getSecondSubmission().getName())));
        return names;
    }

    // Currently, only one metric can be obtained.

    /**
     * Gets the used metric in a JPlag comparison.
     * @return A list contains Metric DTOs.
     */
    private static List<Metric> getMetrics(JPlagResult result) {
        List<Metric> metrics = new ArrayList<>();
        metrics.add(new Metric(result.getOptions().getSimilarityMetric().name(), result.getOptions().getSimilarityThreshold(),
                intArrayToList(result.getSimilarityDistribution()), getTopComparisons(result.getComparisons())));
        return metrics;
    }

    /**
     * Converts JPlagComparison to a DTO for displaying only comparisons. See
     * {@link #generateComparisonReports(JPlagResult)} for a more detailed representation of a comparison.
     * @return List containing TopComparison DTOs.
     */
    private static List<TopComparison> getTopComparisons(List<JPlagComparison> comparisons) {
        List<TopComparison> topComparisons = new ArrayList<>();
        comparisons.forEach(comparison -> topComparisons.add(
                new TopComparison(comparison.getFirstSubmission().getName(), comparison.getSecondSubmission().getName(), comparison.similarity())));
        return topComparisons;
    }

    /**
     * Converts files of a submission to FilesOFSubmission DTO.
     * @return A list containing FilesOfSubmission DTOs.
     */
    private static List<FilesOfSubmission> getFilesForSubmission(Submission submission) {
        return submission.getFiles().stream().map(file -> new FilesOfSubmission(file.getName(), readFileLines(file))).collect(Collectors.toList());
    }

    /**
     * Converts a JPlag Match object to a Match DTO.
     * @param comparison The comparison from which the match originates.
     * @param match The match to be converted.
     * @param usesIndex Indicates whether the language uses indexes.
     * @return A Match DTO.
     */
    private static Match convertMatchToReportMatch(JPlagComparison comparison, de.jplag.Match match, Boolean usesIndex) {
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

    private static List<String> readFileLines(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException exception) {
            logger.error("Could not read file: " + exception.getMessage());
        }
        return lines;
    }

    private static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private static List<Integer> intArrayToList(int[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }
}
