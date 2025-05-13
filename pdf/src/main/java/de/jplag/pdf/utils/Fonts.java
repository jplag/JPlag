package de.jplag.pdf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.jplag.pdf.ComparisonPrinter;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;

public class Fonts {
    private static Map<Document, PdfFont> JET_BRAINS_MONO = new HashMap<>();

    public static PdfFont jetBrainsMono(Document doc) throws IOException {
        if (!JET_BRAINS_MONO.containsKey(doc)) {
            InputStream inputStream = ComparisonPrinter.class.getResourceAsStream("/JetBrainsMono-Regular.ttf");
            byte[] fontBytes = inputStream.readAllBytes();
            JET_BRAINS_MONO.put(doc, PdfFontFactory.createFont(fontBytes, PdfEncodings.UTF8));
        }

        return JET_BRAINS_MONO.get(doc);
    }
}
