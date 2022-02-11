package de.jplag.clustering;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.ClusteringResult.Cluster;
import de.jplag.clustering.algorithm.ClusteringAlgorithm;
import de.jplag.clustering.algorithm.SpectralClustering;
import de.jplag.clustering.preprocessors.CdfPreprocessor;

public class ClusteringTest {
    
    @Test
    public void testClustering() {
        List<Submission> submissions = IntStream.range(0, 4).mapToObj(x -> mock(Submission.class)).collect(Collectors.toList());
        List<JPlagComparison> comparisons = new ArrayList<>(6);
        for (int i = 0; i < submissions.size(); i++) {
            for (int j = i + 1; j < submissions.size(); j++) {
                JPlagComparison comparison = mock(JPlagComparison.class);
                when(comparison.getFirstSubmission()).thenReturn(submissions.get(i));
                when(comparison.getSecondSubmission()).thenReturn(submissions.get(j));
                comparisons.add(comparison);
            }
        }

        // Mock algorithm that returns everything in a single cluster
        ClusteringAlgorithm algorithm = mock(ClusteringAlgorithm.class);
        when(algorithm.cluster(any(RealMatrix.class))).then((InvocationOnMock invocation) -> {
            RealMatrix arg = invocation.getArgument(0);
            Collection<Collection<Integer>> result = List.of(IntStream.range(0, arg.getRowDimension()).boxed().collect(Collectors.toList()));
            return result;
        });

        ClusteringAdapter clustering = new ClusteringAdapter(comparisons, x -> 0.f);
        ClusteringResult<Submission> clusteringResult = clustering.doClustering(algorithm);

        Collection<Collection<Submission>> expectedResult = List.of(submissions);

        assertEquals(expectedResult, clusteringResult.getClusters().stream().map(Cluster::getMembers).collect(Collectors.toList()));
    }

    private String str(float f) {
        return String.format("%.4f", f);
    }

    private float harmonicMean(float a, float b) {
        return 2 * a * b / (a + b);
    }

    private URL loadFromClasspath(String file) throws FileNotFoundException {
        URL url = getClass().getClassLoader().getResource(file);
        if (url == null) {
            throw new FileNotFoundException(file + " not found. Was the PseudonymizedReports submodule initialized?");
        }
        return url;
    }

    @Test
    @Ignore
    public void aClusteringOld() throws FileNotFoundException, URISyntaxException {
        URL url = loadFromClasspath("de/jplag/PseudonymizedReports/alt/C_1000_matches_max.csv");
        File file = new File(url.toURI());
        ReadResult r = readOldCsv(file);
        Preprocessor preprocessor = new CdfPreprocessor();
        RealMatrix clusteringSimilarity = new Array2DRowRealMatrix(preprocessor.preprocessSimilarities(r.similarity.getData()));

        /*
        AgglomerativeClustering.ClusteringOptions options = new AgglomerativeClustering.ClusteringOptions();
        options.minimalSimilarity = 0.15f;
        options.similarity = AgglomerativeClustering.InterClusterSimilarity.AVERAGE;
        ClusteringAlgorithm clusteringAlg = new AgglomerativeClustering(options);
        */
        
        SpectralClustering clusteringAlg = new SpectralClustering(new SpectralClustering.ClusteringOptions());
        Collection<Collection<Integer>> clustering = clusteringAlg.cluster(clusteringSimilarity);
        clustering = preprocessor.postProcessResult(clustering);
        ClusteringResult<Integer> mRes = new IntegerClusteringResult(clustering, r.similarity);
        List<Cluster<Integer>> clusters = new ArrayList<>(mRes.getClusters());
        clusters.sort(Comparator.comparingDouble(c -> -harmonicMean(c.getCommunityStrengthPerConnection(), avgSimilarity(new ArrayList<>(c.getMembers()), r.similarity))));

        System.out.println("cs\tncsm\tavgSim\tcombined\tmembers");
        for (Cluster<Integer> c : clusters) {
            float ncsm = c.getCommunityStrengthPerConnection();
            float avgSim = c.avgSimilarity((a, b) -> (float) r.similarity.getEntry(a, b));
            float combined = harmonicMean(ncsm, avgSim);
            System.out.println(str(c.getCommunityStrength()) + "\t" + str(ncsm) + "\t" + str(avgSim) + "\t" + str(combined) + "\t" + c.getMembers().stream().map(r.mapping::unmap).collect(Collectors.toList()));
        }
        System.out.println("Community Strength: " + mRes.getCommunityStrength());
        System.out.println("Clusters: " + clusters.size());
    }

    @Test
    @Ignore
    public void aClusteringNew() throws FileNotFoundException, URISyntaxException {
        URL url = loadFromClasspath("de/jplag/PseudonymizedReports/neu/B_matches_avg.csv");
        File file = new File(url.toURI());
        ReadResult r = readNewCsv(file);
        RealMatrix clusteringSimilarity = new Array2DRowRealMatrix(new CdfPreprocessor().preprocessSimilarities(r.similarity.getData()));

        /*
        AgglomerativeClustering.ClusteringOptions options = new AgglomerativeClustering.ClusteringOptions();
        options.minimalSimilarity = 0.3f;
        options.similarity = AgglomerativeClustering.InterClusterSimilarity.AVERAGE;
        ClusteringAlgorithm clusteringAlg = new AgglomerativeClustering(options);
        */
        
        SpectralClustering clusteringAlg = new SpectralClustering(new SpectralClustering.ClusteringOptions());
        
        Collection<Collection<Integer>> clustering = clusteringAlg.cluster(clusteringSimilarity);
        ClusteringResult<Integer> mRes = new IntegerClusteringResult(clustering, r.similarity);
        List<Cluster<Integer>> clusters = new ArrayList<>(mRes.getClusters());
        clusters.sort(Comparator.comparingDouble(c -> -harmonicMean(c.getNormalizedCommunityStrengthPerConnection(), avgSimilarity(new ArrayList<>(c.getMembers()), r.similarity))));

        System.out.println("cs\tncsm\tavgSim\tcombined\tmembers");
        for (Cluster<Integer> c : clusters) {
            if (c.isBadCluster()) continue;
            float ncsm = c.getNormalizedCommunityStrengthPerConnection();
            float avgSim = c.avgSimilarity((a, b) -> (float) r.similarity.getEntry(a, b));
            float combined = harmonicMean(ncsm, avgSim);
            System.out.println(str(c.getCommunityStrength()) + "\t" + str(ncsm) + "\t" + str(avgSim) + "\t" + str(combined) + "\t" + c.getMembers().stream().map(r.mapping::unmap).collect(Collectors.toList()));
        }
        System.out.println("Community Strength: " + mRes.getCommunityStrength());
        System.out.println("Clusters: " + clusters.size());
    }

    private float avgSimilarity(List<Integer> submissions, RealMatrix similarity) {
        float similaritySum = 0;
        int comps = 0;

        for (int i = 0; i < submissions.size(); i++) {
            for (int j = i + 1; j < submissions.size(); j++) {
                similaritySum += similarity.getEntry(submissions.get(i), submissions.get(j));
                comps++;
            }
        }

        return similaritySum / comps;
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
                if (records.isEmpty()) continue;
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
                if (records.isEmpty()) continue;
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
