package de.jplag.reportingV2.reportobject;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.reportingV2.reportobject.model.ComparisonReport;
import de.jplag.reportingV2.reportobject.model.JPlagReport;
import de.jplag.reportingV2.reportobject.model.OverviewReport;
import de.jplag.reportingV2.reportobject.model.TopComparison;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportObjectFactory {

	public static JPlagReport getReportObject(JPlagResult result) {
		OverviewReport overviewReport = generateOverviewReport(result);
		List<ComparisonReport> comparisons = List.of();

		return new JPlagReport(overviewReport, comparisons);
	}

	private static  OverviewReport generateOverviewReport(JPlagResult result) {
		List<JPlagComparison> comparisons = result.getComparisons();
		List<JPlagComparison> topComparisons = result.getComparisons(25);
		OverviewReport overviewReport = new OverviewReport();

		overviewReport.setSubmission_folder_path(result.getOptions().getRootDirectoryName());

		String baseCodePath = result.getOptions().hasBaseCode()? result.getOptions().getBaseCodeSubmissionName() : "";
		overviewReport.setBase_code_folder_path(baseCodePath);

		overviewReport.setLanguage(result.getOptions().getLanguage().getName());
		overviewReport.setFile_extensions(List.of(result.getOptions().getFileSuffixes()));
		overviewReport.setSubmission_ids(extractSubmissionNames(comparisons));
		overviewReport.setFailed_submission_names(List.of());  //No number of failed submissions
		overviewReport.setExcluded_files(List.of(result.getOptions().getExclusionFileName())); //Read exclusion file
		overviewReport.setMax_similarity_threshold(result.getOptions().getSimilarityThreshold());
		overviewReport.setAvg_similarity_threshold(0); //Also only one metric per JPLag result. Needs to be discussed.
		overviewReport.setMatch_sensitivity(result.getOptions().getMinimumTokenMatch());
		overviewReport.setDate_of_execution(getDate());
		overviewReport.setExecution_time(result.getDuration());
		overviewReport.setDistribution_max(intArrayToList(result.getSimilarityDistribution()));
		overviewReport.setDistribution_avg(intArrayToList(new int[0])); //Missing second distribution
		overviewReport.setComparison_names(getComparisonNames(comparisons));
		overviewReport.setTop_max_comparisons(getTopComparisons(topComparisons));
		overviewReport.setTop_avg_comparisons(getTopComparisons(List.of()));  //Missing avg comparisons

		return overviewReport;
	}

	private static List<ComparisonReport> generateComparisonReports(JPlagResult result) {
		List<ComparisonReport> comparisons = List.of();
		result.getComparisons().forEach( c -> {

		});
		return comparisons;
	}

	private static List<String> extractSubmissionNames(List<JPlagComparison> comparisons) {
		HashSet<String> names = new HashSet<>();
		comparisons.forEach(c -> {
			names.add(c.getFirstSubmission().getName());
			names.add(c.getSecondSubmission().getName());
		});
		return new ArrayList<>(names);
	}

	private static List<String> getComparisonNames(List<JPlagComparison> comparisons) {
		List<String> names = new ArrayList<>();
		comparisons.forEach(
				c -> names.add(String.join("-", c.getFirstSubmission().getName(), c.getSecondSubmission().getName()))
		);
		return names;
	}

	private static List<TopComparison> getTopComparisons(List<JPlagComparison> comparisons) {
		List<TopComparison> topComparisons = new ArrayList<>();
		comparisons.forEach( c -> topComparisons.add(new TopComparison(constructComparisonName(c), c.similarity())));
		return topComparisons;
	}

	private static String constructComparisonName(JPlagComparison comparison) {
		return String.join("-",
				comparison.getFirstSubmission().getName(),
				comparison.getSecondSubmission().getName()
		);
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

