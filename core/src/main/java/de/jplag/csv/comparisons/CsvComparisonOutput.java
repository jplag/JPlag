package de.jplag.csv.comparisons;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.csv.CsvDataMapper;
import de.jplag.csv.CsvPrinter;
import de.jplag.csv.HardcodedCsvDataMapper;
import de.jplag.csv.ReflectiveCsvDataMapper;
import de.jplag.options.SimilarityMetric;

/**
 * Frontend for writing the result comparisons as a csv.
 */
public class CsvComparisonOutput {
    private static final String[] titles = {"submissionName1", "submissionName2", "averageSimilarity", "maxSimilarity"};

    private CsvComparisonOutput() {
    }

    /**
     * Writes the comparisons as a csv.
     * @param comparisons The list of comparisons
     * @param anonymize If true only random ids will be printed and an additional file will contain the actual names
     * @param directory The directory to write into
     * @param fileName The base name for the file without ".csv"
     * @throws IOException if the output cannot be written.
     */
    public static void writeCsvResults(List<JPlagComparison> comparisons, boolean anonymize, File directory, String fileName) throws IOException {
        NameMapper mapper = new NameMapper.IdentityMapper();
        directory.mkdirs();

        if (anonymize) {
            mapper = new NameMapperIncrementalIds();
        }

        CsvDataMapper<CsvComparisonData> dataMapper = new ReflectiveCsvDataMapper<>(CsvComparisonData.class, titles);
        CsvPrinter<CsvComparisonData> printer = new CsvPrinter<>(dataMapper);

        for (JPlagComparison comparison : comparisons) {
            double average = SimilarityMetric.AVG.applyAsDouble(comparison);
            double max = SimilarityMetric.MAX.applyAsDouble(comparison);
            String firstName = mapper.map(comparison.firstSubmission().getName());
            String secondName = mapper.map(comparison.secondSubmission().getName());
            printer.addRow(new CsvComparisonData(firstName, secondName, average, max));
        }

        printer.printToFile(new File(directory, fileName + ".csv"));

        if (anonymize) {
            List<Map.Entry<String, String>> nameMap = mapper.getNameMap();
            CsvDataMapper<Map.Entry<String, String>> namesMapMapper = new HardcodedCsvDataMapper<>(2, it -> new String[] {it.getValue(), it.getKey()},
                    new String[] {"id", "realName"});
            CsvPrinter<Map.Entry<String, String>> namesPrinter = new CsvPrinter<>(namesMapMapper);
            namesPrinter.addRows(nameMap);
            namesPrinter.printToFile(new File(directory, fileName + "-names.csv"));
        }
    }
}
