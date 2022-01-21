package de.jplag.reportingV2.reportobject;

import de.jplag.*;
import de.jplag.options.JPlagOptions;
import de.jplag.reportingV2.reportobject.model.*;
import de.jplag.reportingV2.reportobject.model.Match;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory class, responsible for converting a JPlagResult object to Overview and Comparison DTO classes.
 */
public class ReportObjectFactory {

	/**
	 * Converts a JPlagResult to a JPlagReport.
	 * @param result
	 * @return JPlagReport for the given JPlagResult.
	 */
	public static JPlagReport getReportObject(JPlagResult result) {
		OverviewReport overviewReport = generateOverviewReport(result);
		List<ComparisonReport> comparisons = generateComparisonReports(result);
		return new JPlagReport(overviewReport, comparisons);
	}

	/**
	 * Generates an Overview DTO of a JPlagResult.
	 * @param result
	 * @return
	 */
	private static  OverviewReport generateOverviewReport(JPlagResult result) {
		List<JPlagComparison> comparisons = result.getComparisons();
		OverviewReport overviewReport = new OverviewReport();

		overviewReport.setSubmission_folder_path(result.getOptions().getRootDirectoryName());

		String baseCodePath = result.getOptions().hasBaseCode()? result.getOptions().getBaseCodeSubmissionName() : "";
		overviewReport.setBase_code_folder_path(baseCodePath);

		overviewReport.setLanguage(result.getOptions().getLanguage().getName());
		overviewReport.setFile_extensions(List.of(result.getOptions().getFileSuffixes()));
		overviewReport.setSubmission_ids(extractSubmissionNames(comparisons));
		overviewReport.setFailed_submission_names(List.of());  //No number of failed submissions
		overviewReport.setExcluded_files(getExcludedFilesNames(result.getOptions())); //Read exclusion file
		overviewReport.setMatch_sensitivity(result.getOptions().getMinimumTokenMatch());
		overviewReport.setDate_of_execution(getDate());
		overviewReport.setExecution_time(result.getDuration());
		overviewReport.setComparison_names(getComparisonNames(comparisons));
		overviewReport.setMetrics(getMetrics(result));

		return overviewReport;
	}

	/**
	 * Generates detailed ComparisonReport DTO for each comparison in a JPlagResult.
	 * @param result
	 * @return A list with ComparisonReport DTOs.
	 */
	private static List<ComparisonReport> generateComparisonReports(JPlagResult result) {
		List<ComparisonReport> comparisons = new ArrayList<>();
		result.getComparisons().forEach( c -> comparisons.add(
			new ComparisonReport(
				c.getFirstSubmission().getName(),
				c.getSecondSubmission().getName(),
				c.similarity(),
				getFilesForSubmission(c.getFirstSubmission()),
				getFilesForSubmission(c.getSecondSubmission()),
				c.getMatches().stream().map(m -> convertMatchToReportMatch(c, m,
						result.getOptions().getLanguage().usesIndex())).collect(Collectors.toList())
			)
		));
		return comparisons;
	}

	/**
	 * Gets the names of all submissions.
	 * @param comparisons
	 * @return A list containing all submission names.
	 */
	private static List<String> extractSubmissionNames(List<JPlagComparison> comparisons) {
		HashSet<String> names = new HashSet<>();
		comparisons.forEach(c -> {
			names.add(c.getFirstSubmission().getName());
			names.add(c.getSecondSubmission().getName());
		});
		return new ArrayList<>(names);
	}

	/**
	 * Gets the names of all comparison.
	 * @param comparisons
	 * @return A list containing all comparisons.
	 */
	private static List<String> getComparisonNames(List<JPlagComparison> comparisons) {
		List<String> names = new ArrayList<>();
		comparisons.forEach(
				c -> names.add(String.join("-", c.getFirstSubmission().getName(), c.getSecondSubmission().getName()))
		);
		return names;
	}

	// Currently, only one metric can be obtained.

	/**
	 * Gets the used metric in a JPlag comparison.
	 * @param result
	 * @return A list contains Metric DTOs.
	 */
	private static List<Metric> getMetrics(JPlagResult result) {
		List<Metric> metrics = new ArrayList<>();
		metrics.add(new Metric(
				result.getOptions().getSimilarityMetric().name(),
				result.getOptions().getSimilarityThreshold(),
				intArrayToList(result.getSimilarityDistribution()),
				getTopComparisons(result.getComparisons())
		));
		return metrics;
	}


	/**
	 * Converts JPlagComparison to a DTO for displaying only comparisons. See {@link #generateComparisonReports(JPlagResult)}
	 * for a more detailed representation of a comparison.
	 * @param comparisons
	 * @return List containing TopComparison DTOs.
	 */
	private static List<TopComparison> getTopComparisons(List<JPlagComparison> comparisons) {
		List<TopComparison> topComparisons = new ArrayList<>();
		comparisons.forEach( c -> topComparisons.add(new TopComparison(c.getFirstSubmission().getName(), c.getSecondSubmission().getName(), c.similarity())));
		return topComparisons;
	}

	/**
	 * Gets the names of excluded files in a JPlag comparison.
	 * To be changed in the future to direct access to the file names. No file reading.
	 * @param options
	 * @return List with the names of the files.
	 */
	private static List<String> getExcludedFilesNames(JPlagOptions options) {
		if (options.getExclusionFileName() == null) {
			return List.of();
		}
		HashSet<String> excludedFileNames = new HashSet<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(options.getExclusionFileName(), JPlagOptions.CHARSET));
			String line;

			while ((line = reader.readLine()) != null) {
				excludedFileNames.add(line.trim());
			}

			reader.close();
		} catch (IOException exception) {
			System.out.println("Could not read exclusion file: " + exception.getMessage());
		}

		return new ArrayList<>(excludedFileNames);
	}

	/**
	 * Converts files of a submission to FilesOFSubmission DTO.
	 * @param submission
	 * @return A list containing FilesOfSubmission DTOs.
	 */
	private static List<FilesOfSubmission> getFilesForSubmission(Submission submission) {
		return submission.getFiles().stream().map( f -> new FilesOfSubmission(f.getName(), readFileLines(f))).collect(Collectors.toList());
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
		Token startFirstToken = tokensFirst.getToken(match.getStartOfFirst());
		Token endFirstToken = tokensFirst.getToken(match.getStartOfFirst() + match.getLength() - 1);
		Token startSecondToken = tokensSecond.getToken(match.getStartOfSecond());
		Token endSecondToken = tokensSecond.getToken(match.getStartOfSecond() + match.getLength() - 1);

		int startFirst = usesIndex ? startFirstToken.getIndex() : startFirstToken.getLine();
		int endFirst = usesIndex ? endFirstToken.getIndex() : endFirstToken.getLine();
		int startSecond = usesIndex ? startSecondToken.getIndex() : startSecondToken.getLine();
		int endSecond = usesIndex ? endSecondToken.getIndex() : endSecondToken.getLine();
		int tokens = match.getLength();

		return new Match(startFirstToken.file, startSecondToken.file,
				startFirst,
				endFirst,
				startSecond,
				endSecond,
				tokens
		);
	}

	private static List<String> readFileLines(File file) {
		ArrayList<String> lines = new ArrayList<>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} catch ( IOException exception) {
			System.out.println("Could not read file: " + exception.getMessage());
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

