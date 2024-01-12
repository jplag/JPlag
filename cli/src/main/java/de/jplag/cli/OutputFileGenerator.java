package de.jplag.cli;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagResult;
import de.jplag.csv.comparisons.CsvComparisonOutput;

public final class OutputFileGenerator {
    private static final Logger logger = LoggerFactory.getLogger(OutputFileGenerator.class);

    private OutputFileGenerator() {
        // Prevents default constructor
    }

    /**
     * Exports the given result as csvs, if the csvExport is activated in the options. Both a full and an anonymized version
     * will be written.
     * @param result The result to export
     * @param outputRoot The root folder for the output
     * @param options The cli options
     */
    public static void generateCsvOutput(JPlagResult result, File outputRoot, CliOptions options) {
        if (options.advanced.csvExport) {
            try {
                CsvComparisonOutput.writeCsvResults(result.getAllComparisons(), false, outputRoot, "results");
                CsvComparisonOutput.writeCsvResults(result.getAllComparisons(), true, outputRoot, "results-anonymous");
            } catch (IOException e) {
                logger.warn("Could not write csv results", e);
            }
        }
    }
}
