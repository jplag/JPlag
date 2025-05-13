package de.jplag.pdf;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.pdf.pdfComponents.DistributionDiagram;
import de.jplag.pdf.pdfComponents.Tables;
import de.jplag.pdf.pdfComponents.Texts;
import de.jplag.pdf.utils.MathUtils;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

public class OverviewPrinter {
    public static void printOverview(Document doc, JPlagResult result) {
        Texts.addTitle(doc, "Overview");

        Texts.addSubtitle(doc, "Average Similarity Distribution:");
        DistributionDiagram.addDiagram(doc, result);

        Texts.addSubtitle(doc, "Comparison table:");
        createComparisonsTable(result, doc);
    }

    private static void createComparisonsTable(JPlagResult result, Document doc) {
        Table fullTable = new Table(6);

        Tables.addHeaderCell(fullTable, "Rank", 2, 1);
        Tables.addHeaderCell(fullTable, "Submissions in comparison", 2, 2);
        Tables.addHeaderCell(fullTable, "Similarities", 1, 2);
        Tables.addHeaderCell(fullTable, "Cluster", 2, 1);

        Tables.addHeaderCell(fullTable, "AVG");
        Tables.addHeaderCell(fullTable, "MAX");

        for (int i = 0; i < result.getAllComparisons().size(); i++) {
            JPlagComparison comparison = result.getAllComparisons().get(i);

            Tables.addTextCell(fullTable, i);
            Tables.addTextCell(fullTable, comparison.firstSubmission().getName());
            Tables.addTextCell(fullTable, comparison.secondSubmission().getName());
            Tables.addTextCell(fullTable, MathUtils.convertToPercent(comparison.similarity()));
            Tables.addTextCell(fullTable, MathUtils.convertToPercent(comparison.maximalSimilarity()));
            Tables.addTextCell(fullTable, "?");
        }

        doc.add(fullTable.useAllAvailableWidth());
    }
}
