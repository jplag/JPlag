package de.jplag.pdf.pdfComponents;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.UnitValue;

public class Spacer {
    public static void addSpacer(Document doc, int height) {
        doc.add(new Div().setHeight(new UnitValue(UnitValue.POINT, height)));
    }
}
