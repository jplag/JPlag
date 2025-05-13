package de.jplag.pdf.pdfComponents;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

public class Texts {
    private static int TITLE_SIZE = 30;
    private static int SUBTITLE_SIEZ = 20;

    public static void addTitle(Document doc, String title) {
        addTitle(doc, title, TITLE_SIZE);
    }

    public static void addSubtitle(Document doc, String title) {
        addTitle(doc, title, SUBTITLE_SIEZ);
    }

    private static void addTitle(Document doc, String title, int size) {
        doc.add(new Paragraph(title).setFontSize(size).setTextAlignment(TextAlignment.CENTER));
        Spacer.addSpacer(doc, size);
    }
}
