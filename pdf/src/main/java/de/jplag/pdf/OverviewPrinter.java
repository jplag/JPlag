package de.jplag.pdf;

import java.text.DecimalFormat;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

public class OverviewPrinter {
    public static void printOverview(Document doc, JPlagResult result) {
        doc.add(new Paragraph("Overview").setTextAlignment(TextAlignment.CENTER).setFontSize(30));

        doc.add(new Div().setHeight(new UnitValue(UnitValue.POINT, 30)));
        doc.add(new Paragraph("Average Similarity Distribution:"));
        createDistributionTable(result, doc);

        doc.add(new Div().setHeight(new UnitValue(UnitValue.POINT, 30)));
        doc.add(new Paragraph("Comparison table:"));
        createComparisonsTable(result, doc);
    }

    private static void createDistributionTable(JPlagResult result, Document doc) {
        int[] distribution = result.getSimilarityDistribution();
        int[] biggerBuckets = new int[10];
        int maxBucket = -1;
        for (int i = 0; i < biggerBuckets.length; i++) {
            int size = 0;
            for (int j = 0; j < 10; j++) {
                size += distribution[(10 * i) + j];
            }
            if (size > maxBucket) {
                maxBucket = size;
            }
            biggerBuckets[i] = size;
        }
        maxBucket = roundUpToTwoSignificantDigits(maxBucket);

        UnitValue[] sizes = {new UnitValue(UnitValue.PERCENT, 10), new UnitValue(UnitValue.PERCENT, 80), new UnitValue(UnitValue.PERCENT, 10)};
        Table table = new Table(sizes);

        for (int i = 0; i < biggerBuckets.length; i++) {
            String label = i * 10 + " - " + (i + 1) * 10;
            table.addCell(new Cell().add(new Paragraph(label)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
                    .setBorderRight(new SolidBorder(1f)).setVerticalAlignment(VerticalAlignment.MIDDLE));

            Paragraph bar = new Paragraph(String.valueOf(biggerBuckets[i]));
            bar.setTextAlignment(TextAlignment.RIGHT);
            float width = (((float) biggerBuckets[i]) / maxBucket) * 100;
            bar.setHeight(20);
            bar.setWidth(new UnitValue(UnitValue.PERCENT, width));
            bar.setBackgroundColor(ColorConstants.RED);
            bar.setFontColor(ColorConstants.WHITE);

            table.addCell(new Cell().add(bar).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setBorder(null));
        }

        table.addCell(new Cell().setBorder(null));
        table.addCell(new Cell().setBorder(null).setBorderTop(new SolidBorder(1f)));

        doc.add(table.useAllAvailableWidth());

        Table axisTable = new Table(
                new UnitValue[] {per(5), per(10), per(10), per(10), per(10), per(10), per(10), per(10), per(10), per(10), per(5)});
        axisTable.addCell(new Cell().setBorder(null));
        for (int i = 0; i < 9; i++) {
            float value = (i * ((float) maxBucket / 8));
            axisTable.addCell(new Cell().add(createLabel(String.valueOf(value))).setBorder(null));
        }
        axisTable.addCell(new Cell().setBorder(null));
        doc.add(axisTable.setMarginTop(-7f).useAllAvailableWidth());
    }

    private static void createComparisonsTable(JPlagResult result, Document doc) {
        Table fullTable = new Table(6);

        fullTable.addHeaderCell(new Cell(2, 1).setBackgroundColor(ColorConstants.LIGHT_GRAY).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(new Paragraph("Rank").setTextAlignment(TextAlignment.CENTER)));
        fullTable.addHeaderCell(new Cell(2, 2).setBackgroundColor(ColorConstants.LIGHT_GRAY).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(new Paragraph("Submissions in comparison").setTextAlignment(TextAlignment.CENTER).setBorderBottom(null)));
        fullTable.addHeaderCell(new Cell(1, 2).setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .add(new Paragraph("Similarities").setTextAlignment(TextAlignment.CENTER).setBorderBottom(null)));
        fullTable.addHeaderCell(new Cell(2, 1).setBackgroundColor(ColorConstants.LIGHT_GRAY).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(new Paragraph("Cluster").setTextAlignment(TextAlignment.CENTER).setBorderBottom(null)));

        fullTable.addHeaderCell(
                new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY).add(new Paragraph("AVG").setTextAlignment(TextAlignment.CENTER)));
        fullTable.addHeaderCell(
                new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY).add(new Paragraph("MAX").setTextAlignment(TextAlignment.CENTER)));

        int rank = 1;
        for (JPlagComparison comparison : result.getAllComparisons()) {
            fullTable.addCell(new Cell().add(new Paragraph(String.valueOf(rank++))).setTextAlignment(TextAlignment.CENTER));

            fullTable.addCell(new Cell().add(new Paragraph(comparison.firstSubmission().getName())).setTextAlignment(TextAlignment.CENTER));
            fullTable.addCell(new Cell().add(new Paragraph(comparison.secondSubmission().getName())).setTextAlignment(TextAlignment.CENTER));

            fullTable.addCell(new Cell().add(new Paragraph(convertToPercent(comparison.similarity()))).setTextAlignment(TextAlignment.CENTER));
            fullTable.addCell(new Cell().add(new Paragraph(convertToPercent(comparison.maximalSimilarity()))).setTextAlignment(TextAlignment.CENTER));

            fullTable.addCell(new Cell().add(new Paragraph("?")).setTextAlignment(TextAlignment.CENTER));
        }

        doc.add(fullTable.useAllAvailableWidth());
    }

    private static int roundUpToTwoSignificantDigits(int number) {
        // Get the order of magnitude
        int orderOfMagnitude = (int) Math.floor(Math.log10(number)) - 1;

        // Get the first two significant digits
        int firstTwoDigits = number / (int) Math.pow(10, orderOfMagnitude);

        // Construct the rounded number
        return (firstTwoDigits + 1) * (int) Math.pow(10, orderOfMagnitude);
    }

    private static String convertToPercent(double value) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(value);
    }

    private static Div createLabel(String label) {
        Paragraph paragraph = new Paragraph(label);

        // Create a vertical line using a Div
        Div verticalLine = new Div();
        verticalLine.setWidth(1); // Set the width of the vertical line
        verticalLine.setHeight(5); // Set the height of the vertical line
        verticalLine.setBackgroundColor(new DeviceRgb(0, 0, 0)); // Set the color of the line (black)
        verticalLine.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Create a Div to hold the vertical line and the paragraph
        Div container = new Div();
        container.add(verticalLine);
        container.add(paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER));
        container.setTextAlignment(TextAlignment.CENTER); // Align text to the left

        return container;
    }

    private static UnitValue per(int percent) {
        return new UnitValue(UnitValue.PERCENT, percent);
    }
}
