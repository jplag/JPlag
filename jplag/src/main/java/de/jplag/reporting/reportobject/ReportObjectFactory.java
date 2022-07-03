package de.jplag.reporting.reportobject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.reporting.reportobject.mapper.ClusteringResultMapper;
import de.jplag.reporting.reportobject.mapper.ComparisonReportMapper;
import de.jplag.reporting.reportobject.mapper.MetricMapper;
import de.jplag.reporting.reportobject.model.ComparisonReport;
import de.jplag.reporting.reportobject.model.JPlagReport;
import de.jplag.reporting.reportobject.model.Metric;
import de.jplag.reporting.reportobject.model.OverviewReport;

/**
 * Factory class, responsible for converting a JPlagResult object to Overview and Comparison DTO classes.
 */
public class ReportObjectFactory {

    private static final ClusteringResultMapper clusteringResultMapper = new ClusteringResultMapper();
    private static final MetricMapper metricMapper = new MetricMapper();
    private static final ComparisonReportMapper comparisonReportMapper = new ComparisonReportMapper();

    /**
     * Converts a JPlagResult to a JPlagReport.
     * @return JPlagReport for the given JPlagResult.
     */
    public static JPlagReport getReportObject(JPlagResult result) {
        OverviewReport overviewReport = generateOverviewReport(result);
        var comparisonReportMapperResult = comparisonReportMapper.generateComparisonReports(result);
        List<ComparisonReport> comparisons = comparisonReportMapperResult.comparisonReports();
        return new JPlagReport(overviewReport, comparisons, comparisonReportMapperResult.lineLookupTable());
    }

    /**
     * Generates an Overview DTO of a JPlagResult.
     */
    private static OverviewReport generateOverviewReport(JPlagResult result) {
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

        return overviewReport;
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
