package de.jplag.pdf.pdfComponents;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

public class Tables {
    public static void addHeaderCell(Table table, String text, int rowSpan, int colSpan) {
        table.addCell(new Cell(rowSpan, colSpan).setBackgroundColor(ColorConstants.LIGHT_GRAY).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(new Paragraph(text).setTextAlignment(TextAlignment.CENTER)));
    }

    public static void addHeaderCell(Table table, String text) {
        addHeaderCell(table, text, 1, 1);
    }

    public static void addTextCell(Table table, Object text, int rowSpan, int colSpan) {
        table.addCell(new Cell(rowSpan, colSpan).add(new Paragraph(String.valueOf(text)).setTextAlignment(TextAlignment.CENTER)));
    }

    public static void addTextCell(Table table, Object text) {
        addTextCell(table, text, 1, 1);
    }
}
