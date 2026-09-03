package de.jplag.pdf.pdfComponents;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;

import java.util.function.Consumer;

public class Texts {
    private static int TITLE_SIZE = 30;
    private static int SUBTITLE_SIEZ = 20;
    private static int SUBSUBTITLE_SIZE = 14;

    public static Paragraph addTitle(Document doc, String title) {
        return addTitle(doc, title, TITLE_SIZE);
    }

    public static void addSubtitle(Document doc, String title) {
        addTitle(doc, title, SUBTITLE_SIEZ);
    }

    public static Paragraph addSubSubTitle(Document doc, String title) {
        return addSubSubTitle(doc, title, (_) -> {});
    }

    public static Paragraph addSubSubTitle(Document doc, String title, Consumer<Paragraph> styler) {
        return addTitle(doc, title, SUBSUBTITLE_SIZE, (p) -> {
            p.setTextAlignment(TextAlignment.LEFT);
            styler.accept(p);
        });
    }

    private static Paragraph addTitle(Document doc, String title, int size) {
        return addTitle(doc, title, size, (_) -> {});
    }

    private static Paragraph addTitle(Document doc, String title, int size, Consumer<Paragraph> styler) {
        Paragraph p = new Paragraph(title).setFontSize(size).setTextAlignment(TextAlignment.CENTER);
        styler.accept(p);
        doc.add(p);
        Spacer.addSpacer(doc, size);
        return p;
    }
}
