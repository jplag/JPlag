package de.jplag.reporting.reportobject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.reporting.jsonfactory.FileWriter;
import de.jplag.reporting.reportobject.mapper.ClusteringResultMapper;
import de.jplag.reporting.reportobject.mapper.ComparisonReportMapper;
import de.jplag.reporting.reportobject.mapper.MetricMapper;
import de.jplag.reporting.reportobject.model.Metric;
import de.jplag.reporting.reportobject.model.OverviewReport;

/**
 * Factory class, responsible for converting a JPlagResult object to Overview and Comparison DTO classes.
 */
public class ReportObjectFactory {
    private static final Logger logger = LoggerFactory.getLogger(ReportObjectFactory.class);

    private static final ClusteringResultMapper clusteringResultMapper = new ClusteringResultMapper();
    private static final MetricMapper metricMapper = new MetricMapper();
    private static final ComparisonReportMapper comparisonReportMapper = new ComparisonReportMapper();
    private static final FileWriter fileWriter = new FileWriter();
    public static final String OVERVIEW_FILE_NAME = "overview.json";
    public static final String SUBMISSIONS_FOLDER = "submissions";

    /**
     * @param result The JPlagResult to be converted into a report.
     * @param path The Path to save the report to
     */
    public static void createAndSaveReport(JPlagResult result, String path) {
        createDirectory(path);
        writeOverview(result, path);
        copySubmissionFilesToReport(path, result);
        comparisonReportMapper.writeComparisonReports(result, path);
    }

    private static File createDirectory(String path, String name) {
        File directory = new File(path.concat("/").concat(name));
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Failed to create dir.");
        }
        return directory;
    }

    private static void createDirectory(String path) {
        createDirectory(path, "");
    }

    private static void writeOverview(JPlagResult result, String path) {
        List<JPlagComparison> comparisons = getComparisons(result);
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

        fileWriter.saveAsJSON(overviewReport, path, OVERVIEW_FILE_NAME);

    }

    private static void copySubmissionFilesToReport(String path, JPlagResult result) {
        List<JPlagComparison> comparisons = result.getComparisons(result.getOptions().getMaximumNumberOfComparisons());
        var submissions = getSubmissions(comparisons);
        var submissionsPath = createDirectory(path, SUBMISSIONS_FOLDER);
        for (var submission : submissions) {
            File directory = createDirectory(submissionsPath.getPath(), submission.getName());
            for (var file : submission.getFiles()) {
                try {
                    Files.copy(file.toPath(), (new File(directory, file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.error("Could not save submission file " + file, e);
                }
            }
        }
    }

    private static Set<Submission> getSubmissions(List<JPlagComparison> comparisons) {
        var submissions = comparisons.stream().map(JPlagComparison::getFirstSubmission).collect(Collectors.toSet());
        Set<Submission> secondSubmissions = comparisons.stream().map(JPlagComparison::getSecondSubmission).collect(Collectors.toSet());
        submissions.addAll(secondSubmissions);
        return submissions;
    }

    private static List<JPlagComparison> getComparisons(JPlagResult result) {
        int numberOfComparisons = result.getOptions().getMaximumNumberOfComparisons();
        return result.getComparisons(numberOfComparisons);
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

    /**
     * Gets the used metrics in a JPlag comparison. As Max Metric is included in every JPlag run, this always include Max
     * Metric.
     * @return A list contains Metric DTOs.
     */
    private static List<Metric> getMetrics(JPlagResult result) {
        return List.of(metricMapper.getAverageMetric(result), metricMapper.getMaxMetric(result));
    }

    private static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
