package de.jplag.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;

import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;
import de.jplag.clustering.algorithm.SpectralClustering;
import de.jplag.clustering.preprocessors.CumulativeDistributionFunctionPreprocessor;

/**
 * These test are not meant to be run during normal unit testing. They can be used to test the clustering algorithms
 * against data from the private pseudomized reports repository. These tests test PROBABILISTIC behavior, so use with
 * caution!
 */
public class ClusteringRealDataTest {

    private static class TestFile {

        private String uri;
        private Optional<List<String>> expected;

        public TestFile(String uri, Optional<List<String>> expected) {
            this.uri = uri;
            this.expected = expected;
        }
    }

    private static List<String> B_POSITIVE = Arrays.asList(new String[] {"Student (31)", "Student (223)"});
    private static List<String> C_POSITIVE = Arrays.asList(new String[] {"Student (166)", "Student (212)", "Student (236)", "Student (229)"});

    private static final TestFile[] OLD_CLUSTERING_DATA = {new TestFile("de/jplag/PseudonymizedReports/alt/A_1000_matches_max.csv", Optional.empty()),
            new TestFile("de/jplag/PseudonymizedReports/alt/B_1000_matches_max.csv", Optional.of(B_POSITIVE)),
            new TestFile("de/jplag/PseudonymizedReports/alt/C_1000_matches_max.csv", Optional.of(C_POSITIVE)),};

    private static final TestFile[] NEW_CLUSTERING_DATA = {new TestFile("de/jplag/PseudonymizedReports/neu/A_matches_avg.csv", Optional.empty()),
            new TestFile("de/jplag/PseudonymizedReports/neu/B_matches_avg.csv", Optional.of(B_POSITIVE)),
            new TestFile("de/jplag/PseudonymizedReports/neu/C_matches_avg.csv", Optional.of(C_POSITIVE)),};

    private String str(float f) {
        return String.format("%.4f", f);
    }

    private URL loadFromClasspath(String file) throws FileNotFoundException {
        URL url = getClass().getClassLoader().getResource(file);
        if (url == null) {
            assumeTrue(false, file + " not found. 'de/jpag/PseudonymizedReports' must contain the data from the PseudonymizedReports repository.");
        }
        return url;
    }

    private void doTesting(ReadResult readResult, Optional<List<String>> expected) {
        RealMatrix clusteringSimilarity = new Array2DRowRealMatrix(readResult.similarity.getData());

        /*
         * AgglomerativeClustering.ClusteringOptions options = new AgglomerativeClustering.ClusteringOptions();
         * options.minimalSimilarity = 0.15f; options.similarity = AgglomerativeClustering.InterClusterSimilarity.AVERAGE;
         * ClusteringAlgorithm clusteringAlg = new AgglomerativeClustering(options);
         */

        SpectralClustering clusteringAlg = new SpectralClustering(ClusteringOptions.DEFAULTS);
        ClusteringPreprocessor preprocessor = new CumulativeDistributionFunctionPreprocessor();
        GenericClusteringAlgorithm preprocessedClusteringAlg = new PreprocessedClusteringAlgorithm(clusteringAlg, preprocessor);
        Collection<Collection<Integer>> clustering = preprocessedClusteringAlg.cluster(clusteringSimilarity);
        ClusteringResult<Integer> mRes = ClusteringResult.fromIntegerCollections(new ArrayList<>(clustering), readResult.similarity);
        List<Cluster<Integer>> clusters = new ArrayList<>(mRes.getClusters());
        clusters.sort(Comparator.comparingDouble(c -> -c.getNormalizedCommunityStrengthPerConnection()));

        System.out.println("cs\tncsm\tavgSim\tcombined\tmembers");
        for (Cluster<Integer> c : clusters) {
            float ncsm = c.getCommunityStrengthPerConnection();
            float avgSim = c.averageSimilarity((a, b) -> (float) readResult.similarity.getEntry(a, b));
            System.out.println(str(c.getCommunityStrength()) + "\t" + str(ncsm) + "\t" + str(avgSim) + "\t"
                    + c.getMembers().stream().map(readResult.mapping::unmap).collect(Collectors.toList()));
        }
        System.out.println("Community Strength: " + mRes.getCommunityStrength());
        System.out.println("Clusters: " + clusters.size());

        expected.ifPresent(expectedIdentifiers -> {
            Set<String> expectedIdentifiersSet = new HashSet<>(expectedIdentifiers);
            Set<String> bestClusters = clusters.get(0).getMembers().stream().map(readResult.mapping::unmap).collect(Collectors.toSet());
            assertEquals(expectedIdentifiersSet, bestClusters);
            System.out.println("hey");
        });
    }

    @Test
    public void aClusteringOld() throws FileNotFoundException, URISyntaxException {
        for (TestFile testFile : OLD_CLUSTERING_DATA) {
            URL url = loadFromClasspath(testFile.uri);
            File file = new File(url.toURI());
            ReadResult r = readOldCsv(file);
            doTesting(r, testFile.expected);
        }
    }

    @Test
    public void aClusteringNew() throws FileNotFoundException, URISyntaxException {
        for (TestFile testFile : NEW_CLUSTERING_DATA) {
            URL url = loadFromClasspath(testFile.uri);
            File file = new File(url.toURI());
            ReadResult r = readNewCsv(file);
            doTesting(r, testFile.expected);
        }
    }

    private static class ReadComparison {
        int left;
        int right;
        float similarity;
    }

    private static class ReadResult {
        IntegerMapping<String> mapping;
        RealMatrix similarity;
    }

    private static ReadResult readNewCsv(File fileName) throws FileNotFoundException {
        IntegerMapping<String> mapping = new IntegerMapping<>(512);
        List<ReadComparison> comparisons = new ArrayList<>(512);
        try (CSVReader reader = new CSVReader(fileName, ";")) {
            while (reader.hasNext()) {
                List<String> records = reader.next();
                if (records.isEmpty())
                    continue;
                String leftStudent = records.get(1);
                String rightStudent = records.get(2);
                String similarity = records.get(3);
                ReadComparison comparison = new ReadComparison();
                comparison.left = mapping.map(leftStudent);
                comparison.right = mapping.map(rightStudent);
                comparison.similarity = Float.parseFloat(similarity) / 100;
                comparisons.add(comparison);
            }
        }
        RealMatrix matrix = new Array2DRowRealMatrix(mapping.size(), mapping.size());
        for (ReadComparison comparison : comparisons) {
            matrix.setEntry(comparison.left, comparison.right, comparison.similarity);
            matrix.setEntry(comparison.right, comparison.left, comparison.similarity);
        }
        ReadResult r = new ReadResult();
        r.similarity = matrix;
        r.mapping = mapping;
        return r;
    }

    private static ReadResult readOldCsv(File fileName) throws FileNotFoundException {
        IntegerMapping<String> mapping = new IntegerMapping<>(512);
        List<ReadComparison> comparisons = new ArrayList<>(512);
        try (CSVReader reader = new CSVReader(fileName, ";")) {
            while (reader.hasNext()) {
                List<String> records = reader.next();
                if (records.isEmpty())
                    continue;
                Deque<String> stuff = new ArrayDeque<>(records);
                String leftStudent = stuff.removeFirst();
                int leftID = mapping.map(leftStudent);
                while (stuff.size() >= 3) {
                    ReadComparison comparison = new ReadComparison();
                    comparison.left = leftID;
                    stuff.removeFirst(); // comparison ID not needed
                    String rightStudent = stuff.removeFirst();
                    String similarity = stuff.removeFirst();
                    comparison.right = mapping.map(rightStudent);
                    comparison.similarity = Float.parseFloat(similarity) / 100;
                    comparisons.add(comparison);
                }
            }
        }
        RealMatrix matrix = new Array2DRowRealMatrix(mapping.size(), mapping.size());
        for (ReadComparison comparison : comparisons) {
            matrix.setEntry(comparison.left, comparison.right, comparison.similarity);
            matrix.setEntry(comparison.right, comparison.left, comparison.similarity);
        }
        ReadResult r = new ReadResult();
        r.similarity = matrix;
        r.mapping = mapping;
        return r;
    }

    private static class CSVReader implements AutoCloseable {
        private String delimiter;
        private Scanner scanner;

        private CSVReader(File fileName, String delimiter) throws FileNotFoundException {
            this.delimiter = delimiter;
            scanner = new Scanner(fileName);
        }

        @Override
        public void close() {
            scanner.close();
        }

        List<String> next() {
            String line = scanner.nextLine();
            String[] records = line.split(delimiter);
            return Arrays.asList(records);
        }

        boolean hasNext() {
            return scanner.hasNextLine();
        }
    }
}
