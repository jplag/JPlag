package de.jplag.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagResult;
import de.jplag.cli.options.CliOptions;
import de.jplag.csv.comparisons.CsvComparisonOutput;
import de.jplag.reporting.reportobject.ReportObjectFactory;

public interface OutputFileGenerator {
    OutputFileGenerator DEFAULT_OUTPUT_FILE_GENERATOR = new OutputFileGenerator() {
        private static final Logger logger = LoggerFactory.getLogger(OutputFileGenerator.class);

        @Override
        public void generateCsvOutput(JPlagResult result, File outputRoot, CliOptions options) {
            if (options.advanced.csvExport) {
                try {
                    CsvComparisonOutput.writeCsvResults(result.getAllComparisons(), false, outputRoot, "results");
                    CsvComparisonOutput.writeCsvResults(result.getAllComparisons(), true, outputRoot, "results-anonymous");
                } catch (IOException e) {
                    logger.warn("Could not write csv results", e);
                }
            }
        }

        @Override
        public void generateJPlagResultZip(JPlagResult result, File outputFile) throws FileNotFoundException {
            ReportObjectFactory reportObjectFactory = new ReportObjectFactory(outputFile);
            reportObjectFactory.createAndSaveReport(result);
            logger.info("Successfully written the result: {}", outputFile.getPath());
            logger.info("View the result using --mode or at: https://jplag.github.io/JPlag/");
        }
    };

    /**
     * Exports the given result as CSVs, if the csvExport is activated in the options. Both a full and an anonymized version
     * will be written.
     * @param result The result to export
     * @param outputRoot The root folder for the output
     * @param options The cli options
     */
    void generateCsvOutput(JPlagResult result, File outputRoot, CliOptions options);

    /**
     * Generates the JPLag result zip
     * @param result The JPlag result
     * @param outputFile The output file
     * @throws FileNotFoundException If the file cannot be written
     */
    void generateJPlagResultZip(JPlagResult result, File outputFile) throws FileNotFoundException;
}
