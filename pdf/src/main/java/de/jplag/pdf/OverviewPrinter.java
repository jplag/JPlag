package de.jplag.pdf;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.clustering.Cluster;
import de.jplag.pdf.pdfComponents.DistributionDiagram;
import de.jplag.pdf.pdfComponents.Tables;
import de.jplag.pdf.pdfComponents.Texts;
import de.jplag.pdf.utils.MathUtils;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewPrinter {
    public static void printOverview(Document doc, JPlagResult result) {
        Texts.addTitle(doc, "Overview");

        Texts.addSubtitle(doc, "Average Similarity Distribution:");
        DistributionDiagram.addDiagram(doc, result);

        List<Cluster<Submission>> clusters = getAllClusters(result);
        Map<Submission, Integer> clusterMap = getClusterMap(clusters);

        Texts.addSubtitle(doc, "Comparison table:");
        createComparisonsTable(result, doc, clusterMap);

        if(clusters != null) {
            printClustersPage(doc, clusters);
        }
    }

    private static void createComparisonsTable(JPlagResult result, Document doc, Map<Submission, Integer> clusterMap) {
        Table fullTable = new Table(6);

        Tables.addHeaderCell(fullTable, "Rank", 2, 1);
        Tables.addHeaderCell(fullTable, "Submissions in comparison", 2, 2);
        Tables.addHeaderCell(fullTable, "Similarities", 1, 2);
        if (clusterMap != null) {
            Tables.addHeaderCell(fullTable, "Cluster", 2, 1);
        }

        Tables.addHeaderCell(fullTable, "AVG");
        Tables.addHeaderCell(fullTable, "MAX");

        for (int i = 0; i < result.getAllComparisons().size(); i++) {
            JPlagComparison comparison = result.getAllComparisons().get(i);

            Tables.addTextCell(fullTable, i);
            Tables.addTextCell(fullTable, comparison.firstSubmission().getName());
            Tables.addTextCell(fullTable, comparison.secondSubmission().getName());
            Tables.addTextCell(fullTable, MathUtils.convertToPercent(comparison.similarity()));
            Tables.addTextCell(fullTable, MathUtils.convertToPercent(comparison.maximalSimilarity()));
            if (clusterMap != null) {
                if (clusterMap.containsKey(comparison.firstSubmission()) && clusterMap.containsKey(comparison.secondSubmission()) &&
                        clusterMap.get(comparison.firstSubmission()) == clusterMap.get(comparison.secondSubmission())) {
                    Tables.addTextCell(fullTable, clusterMap.get(comparison.firstSubmission()));
                } else {
                    Tables.addTextCell(fullTable, "-");
                }
            }
        }

        doc.add(fullTable.useAllAvailableWidth());
    }

    private static void printClustersPage(Document doc, List<Cluster<Submission>> clusters) {
        Texts.addSubtitle(doc, "Clusters:");

        for (int i = 0; i < clusters.size(); i++) {
            Cluster<Submission> cluster = clusters.get(i);

            Texts.addSubSubTitle(doc, "Cluster number " + i + ":");

            Paragraph items = new Paragraph();
            items.setMarginLeft(10);
            for (Submission member : cluster.getMembers()) {
                items.add(new Text(member.getName()));
            }
            doc.add(items);
        }
    }

    private static List<Cluster<Submission>> getAllClusters(JPlagResult result) {
        if (result.getClusteringResult() == null) {
            return null;
        } else {
            return result.getClusteringResult().stream().flatMap(it -> it.getClusters().stream()).toList();
        }
    }

    private static Map<Submission, Integer> getClusterMap(List<Cluster<Submission>> clusters) {
        if (clusters == null) {
            return null;
        }

        Map<Submission, Integer> map = new HashMap<>();
        for (int i = 0; i < clusters.size(); i++) {
            Cluster<Submission> cluster = clusters.get(i);
            for (Submission member : cluster.getMembers()) {
                map.put(member, i);
            }
        }
        return map;
    }
}
