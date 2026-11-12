package de.jplag.pdf;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.clustering.Cluster;
import de.jplag.pdf.pdfComponents.DistributionDiagram;
import de.jplag.pdf.pdfComponents.Links;
import de.jplag.pdf.pdfComponents.Spacer;
import de.jplag.pdf.pdfComponents.Tables;
import de.jplag.pdf.pdfComponents.Texts;
import de.jplag.pdf.utils.JPlagLinkType;
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

        if (clusters != null) {
            Spacer.addSpacer(doc, 100);
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
                    int clusterNumber = clusterMap.get(comparison.firstSubmission());
                    Tables.addCell(fullTable, Links.createTextLink(String.valueOf(clusterNumber), JPlagLinkType.CLUSTER, clusterNumber, (p) -> p.setTextAlignment(TextAlignment.CENTER)));
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

            final String link = JPlagLinkType.CLUSTER.resolve(i);
            Texts.addSubSubTitle(doc, "Cluster number " + i + ":", (p) -> p.setDestination(link));

            Paragraph items = new Paragraph();
            items.setMarginLeft(50);
            for (Submission member : cluster.getMembers()) {
                items.add(new Text(member.getName() + "\n"));
            }
            doc.add(items);
            Spacer.addSpacer(doc, 20);
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
