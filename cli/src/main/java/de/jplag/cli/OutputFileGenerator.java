package de.jplag.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagResult;
import de.jplag.cli.options.CliOptions;
import de.jplag.exceptions.FileException;
import de.jplag.reporting.csv.comparisons.CsvComparisonOutput;
import de.jplag.reporting.reportobject.ReportObjectFactory;

/**
 * Manages the creation of output files.
 */
public final class OutputFileGenerator {
    private static final Logger logger = LoggerFactory.getLogger(OutputFileGenerator.class);

    private OutputFileGenerator() {
    }

    /**
     * Exports the given result as CSVs, if the csvExport is activated in the options. Both a full and an anonymized version
     * will be written.
     * @param result The result to export
     * @param outputRoot The root folder for the output
     * @param options The cli options
     */
    public static void generateCsvOutput(JPlagResult result, Path outputRoot, CliOptions options) {
        if (options.advanced.csvExport) {
            try {
                CsvComparisonOutput.writeCsvResults(result.getAllComparisons(), false, outputRoot, "results");
                CsvComparisonOutput.writeCsvResults(result.getAllComparisons(), true, outputRoot, "results-anonymous");
            } catch (IOException e) {
                logger.warn("Could not write csv results", e);
            }
        }
    }

    /**
     * Generates the JPLag result file out of a given {@link JPlagResult}.
     * @param result is the JPlag result to export.
     * @param outputFile is the target for the result file.
     * @throws FileNotFoundException if the file cannot be written-
     * @throws FileException If the file handling fails
     */
    public static void generateJPlagResultFile(JPlagResult result, File outputFile) throws FileNotFoundException, FileException {
        ReportObjectFactory reportObjectFactory = new ReportObjectFactory(outputFile);
        reportObjectFactory.createAndSaveReport(result);
        logger.info("Successfully written the result: {}", outputFile.getPath());
        logger.info("View the result using --mode");
    }
}
