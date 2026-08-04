package de.jplag.pdf.pdfComponents;

import java.util.Arrays;

import com.itextpdf.layout.element.Text;
import de.jplag.JPlagResult;
import de.jplag.pdf.utils.MathUtils;

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

public class DistributionDiagram {
    public static void addDiagram(Document doc, JPlagResult result) {
        int[] buckets = buildBuckets(result);
        int maxBucketValue = Arrays.stream(buckets).max().getAsInt();
        int diagramMaxValue = MathUtils.roundUpTwoSignificantDigits(maxBucketValue);

        UnitValue[] sizes = {new UnitValue(UnitValue.PERCENT, 10), new UnitValue(UnitValue.PERCENT, 40), new UnitValue(UnitValue.PERCENT, 40), new UnitValue(UnitValue.PERCENT, 10)};
        Table table = new Table(sizes);
        for (int i = buckets.length - 1; i >= 0; i--) {
            addRowForBucket(table, buckets[i], i, diagramMaxValue);
        }

        table.addCell(new Cell().setBorder(null));
        table.addCell(new Cell(1, 2).setBorder(null).setBorderTop(new SolidBorder(1f)));
        doc.add(table.useAllAvailableWidth());

        addAxis(doc, diagramMaxValue);
    }

    private static void addRowForBucket(Table table, int bucketValue, int bucketIndex, int diagramMaxValue) {
        String label = bucketIndex * 10 + " - " + (bucketIndex + 1) * 10;
        table.addCell(new Cell().add(new Paragraph(label)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)
                .setBorderRight(new SolidBorder(1f)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        float width = (((float) bucketValue) / diagramMaxValue) * 100;

        Paragraph bar = new Paragraph();
        bar.setHeight(20);
        bar.setWidth(new UnitValue(UnitValue.PERCENT, width));
        bar.setBackgroundColor(ColorConstants.RED);
        Paragraph postText = new Paragraph();

        if(width > 30) {
            bar.setTextAlignment(TextAlignment.RIGHT);
            bar.setFontColor(ColorConstants.WHITE);
            bar.add(String.valueOf(bucketValue));
            table.addCell(new Cell(1, 2).add(bar).setBorder(Border.NO_BORDER));
        } else {
            bar.setWidth(new UnitValue(UnitValue.PERCENT, width * 2));
            postText.add(String.valueOf(bucketValue));
            postText.setRelativePosition((width * 4) - 200,1, 0,0);
            table.addCell(new Cell(1, 1).add(bar).setBorder(Border.NO_BORDER));
            table.addCell(new Cell(1, 1).add(postText).setBorder(Border.NO_BORDER));
        }


        table.addCell(new Cell().setBorder(null));
    }

    private static void addAxis(Document doc, int diagramMaxValue) {
        Table axisTable = new Table(
                new UnitValue[] {per(5), per(10), per(10), per(10), per(10), per(10), per(10), per(10), per(10), per(10), per(5)});
        axisTable.addCell(new Cell().setBorder(null));
        for (int i = 0; i < 9; i++) {
            float value = (i * ((float) diagramMaxValue / 8));
            axisTable.addCell(new Cell().add(createDiagramLabel(String.valueOf(value))).setBorder(null));
        }
        axisTable.addCell(new Cell().setBorder(null));
        doc.add(axisTable.setMarginTop(-7f).useAllAvailableWidth());
    }

    private static Div createDiagramLabel(String label) {
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

    private static int[] buildBuckets(JPlagResult result) {
        int[] distribution = result.getSimilarityDistribution();
        int[] biggerBuckets = new int[10];
        for (int i = 0; i < biggerBuckets.length; i++) {
            int size = 0;
            for (int j = 0; j < 10; j++) {
                size += distribution[(10 * i) + j];
            }
            biggerBuckets[i] = size;
        }
        return biggerBuckets;
    }

    private static UnitValue per(int percent) {
        return new UnitValue(UnitValue.PERCENT, percent);
    }
}
