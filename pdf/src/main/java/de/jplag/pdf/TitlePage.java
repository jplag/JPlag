package de.jplag.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

public class TitlePage {
    public static void addTitlePage(Document doc, String subText) {
        float pageHeight = doc.getPageEffectiveArea(PageSize.A4).getHeight();

        Div fullPage = new Div();
        fullPage.setHeight(pageHeight);
        fullPage.setWidth(UnitValue.createPercentValue(100));
        fullPage.setHorizontalAlignment(HorizontalAlignment.CENTER);
        fullPage.setVerticalAlignment(VerticalAlignment.MIDDLE);

        ImageData logoData = ImageDataFactory.createPng(TitlePage.class.getResource("/assets/jplag-dark-transparent.png"));
        Image logo = new Image(logoData);

        Paragraph center = new Paragraph();
        center.setFontSize(40);
        center.setWidth(new UnitValue(UnitValue.PERCENT, 100));
        center.setHorizontalAlignment(HorizontalAlignment.CENTER);
        center.setTextAlignment(TextAlignment.CENTER);
        center.add("JPlag Report\n\n");
        center.add(logo);
        center.add("\n\n" + subText);

        fullPage.add(center);

        doc.add(fullPage);
    }
}
