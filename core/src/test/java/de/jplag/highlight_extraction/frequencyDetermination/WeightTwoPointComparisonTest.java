package de.jplag.highlight_extraction.frequencyDetermination;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jplag.*;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.highlight_extraction.*;

//todo aktuell 74% 19 plagiat, 60 % aufällig
// 1. sortirung auf comparisons
// 0.1 intervalle zwichen 0 und 0.5
// nur noch stärker gewichten window und complete
// 56 plagiat 76%, auffällig 60 %  =>solanged der großteil der gruppe stimmt argumentieren
public class WeightTwoPointComparisonTest extends TestBase {

    private static JPlagResult result;
    private static Path baseOutputDir;
    private static SubmissionSet submissionSet;
    private static LongestCommonSubsequenceSearch strategy;
    public static JPlagOptions options;
    private static final List<String[]> timingResults = new ArrayList<>();


    private static final String[] datasetPaths = {
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\00000019\\Java",
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\00000056\\Java",
            "C:\\Users\\Elisa\\Projekte\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\FrequencyDetermination\\Sheet3TaskA"
    };

    private static final FrequencyStrategy[] freqStrategies = {
            new CompleteMatchesStrategy(),
            new ContainedStrategy(),
            new SubMatchesStrategy(),
            new WindowOfMatchesStrategy()
    };

    @BeforeAll
    static void setupOutputDir() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        baseOutputDir = Paths.get("output", "two_point_comparison_" + timestamp);
        Files.createDirectories(baseOutputDir);
    }

    @Test
    void runTwoWeightComparisonExport() throws IOException, ExitException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path outputRoot = baseOutputDir.resolve("two_weight_similarity_" + timestamp);
        timingResults.add(new String[]{"Dataset", "Strategy", "DurationMillis"});

        for (String datasetPath : datasetPaths) {
            System.out.println("=== Zwei-Gewicht-Vergleich für Datensatz: " + datasetPath + " ===");

            options = getOptions(List.of(datasetPath), List.of(), o -> o);
            if (datasetPath.contains("00000019\\Java")) {
                String baseCodeDir = "C:\\Data\\progpedia\\base\\main.java";
                options = options.withBaseCodeSubmissionName(baseCodeDir);
            }

            SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
            submissionSet = builder.buildSubmissionSet();
            strategy = new LongestCommonSubsequenceSearch(options);
            result = strategy.compareSubmissions(submissionSet);
            System.out.println("Vergleiche: " + result.getAllComparisons().size());

            Path datasetPathObj = Paths.get(datasetPath);
            String parentName = datasetPathObj.getParent().getFileName().toString();
            String languageName = datasetPathObj.getFileName().toString();
            String datasetName = "progpedia_" + parentName + "_" + languageName;

            LabelledWeighting.DatasetType datasetType = switch (parentName) { //fixme neu
                case "00000019" -> LabelledWeighting.DatasetType.DS19;
                case "00000056" -> LabelledWeighting.DatasetType.DS56;
                default -> LabelledWeighting.DatasetType.A3;
            };

            for (FrequencyStrategy freqStrategy : freqStrategies) {
                System.out.println("→ Strategie: " + freqStrategy.getClass().getSimpleName());
                long start = System.nanoTime();
                //strategie
                FrequencyDetermination fd = new FrequencyDetermination(freqStrategy, 35);
                fd.runAnalysis(result.getAllComparisons());

                //Matching
                MatchWeighting weighting = new MatchWeighting(freqStrategy, fd.getTokenFrequencyMap());
                for (var comparison : result.getAllComparisons()) {
                    weighting.weightAllMatches(
                            comparison.matches(),
                            comparison.firstSubmission().getTokenList().stream().map(t -> t.getType().toString()).toList()
                    );
                }

                FrequencySimilarity similarity = new FrequencySimilarity(result.getAllComparisons());
                LabelledWeighting lw = new LabelledWeighting();
                lw.classifyComparisons(result.getAllComparisons(), datasetType);
                //System.out.println(lw);

                String strategyName = freqStrategy.getClass().getSimpleName();
                Path outputDir = outputRoot.resolve(datasetName).resolve(strategyName);
                Files.createDirectories(outputDir);

                System.out.println("\nplagiat: " + lw.getPlagiatComparisons().size() + "\n");
                System.out.println("auffaellig: " + lw.getAuffaelligComparisons().size()+ "\n");
                System.out.println("unauffaellig: " + lw.getUnauffaelligComparisons().size() + "\n");
                runAndSaveTwoWeightSimilarity(lw.getPlagiatComparisons(), "plagiat", similarity, outputDir);
                runAndSaveTwoWeightSimilarity(lw.getAuffaelligComparisons(), "auffaellig", similarity, outputDir);
                runAndSaveTwoWeightSimilarity(lw.getUnauffaelligComparisons(), "unauffaellig", similarity, outputDir);
                long end = System.nanoTime();
                long durationMillis = (end - start) / 1_000_000;
                timingResults.add(new String[]{datasetName, strategyName, String.valueOf(durationMillis)});
            }
        }
        Path timingFile = baseOutputDir.resolve("strategy_timings.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(timingFile)) {
            for (String[] row : timingResults) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            System.out.println("✅ Zeitmessung gespeichert in: " + timingFile);
        }
    }

    private void runAndSaveTwoWeightSimilarity(List<JPlagComparison> comparisons, String label,
                                               FrequencySimilarity similarity, Path outputDir) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path file = outputDir.resolve(label + "_three_rare_log_square_Matches_weight_" + timestamp + ".csv");

        try (var writer = Files.newBufferedWriter(file)) {
            writer.write("ComparisonID,Submission1,Submission2,Weight_0.0,Weight_0.1,Weight_0.2,Weight_0.3,Weight_0.4,Weight_0.5,Weight_1.0\n");
            for (JPlagComparison comp : comparisons) {
                String id = comp.toString();
                String s1 = comp.firstSubmission().getName();
                String s2 = comp.secondSubmission().getName();
                double sim0 = comp.similarity();
                double sim1 = similarity.frequencySimilarity(comp, 0.1);
                double sim2 = similarity.frequencySimilarity(comp, 0.2);
                double sim3 = similarity.frequencySimilarity(comp, 0.3);
                double sim4 = similarity.frequencySimilarity(comp, 0.4);
                double sim5 = similarity.frequencySimilarity(comp, 0.5);
                double sim6 = similarity.frequencySimilarity(comp, 1.0);
                sim1 = clampSimilarity(sim1);
                sim2 = clampSimilarity(sim2);
                sim3 = clampSimilarity(sim3);
                sim4 = clampSimilarity(sim4);
                sim5 = clampSimilarity(sim5);
                sim6 = clampSimilarity(sim6);

                writer.write(String.format(Locale.US,"\"%s\",\"%s\",\"%s\",%.5f,%.5f,%.5f,%.5f,%.5f,%.5f,%.5f\n", id, s1, s2, sim0,  sim1, sim2, sim3, sim4, sim5, sim6));
                //writer.write(String.format(Locale.US,"\"%s\",\"%s\",\"%s\",%.5f,%.5f,%.5f\n", id, s1, s2, sim0, sim5, sim6));
            }
        }

        System.out.println("Vergleich mit zwei Gewichtungen gespeichert: " + file);
    }
    private double clampSimilarity(double sim) {
        if (sim > 1.0) return 1.0;
        if (sim < 0.0) return 0.0;
        return sim;
    }
}
