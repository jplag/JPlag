package de.jplag.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

public class FooterHandler {
    private PdfDocument document;

    private PdfImageXObject logo;
    private PdfFormXObject totalPageNumber;

    public FooterHandler(PdfDocument document) {
        this.document = document;
        ImageData logoData = ImageDataFactory.createPng(FooterHandler.class.getResource("/assets/jplag-dark-transparent.png"));
        logo = new PdfImageXObject(logoData);

        totalPageNumber = new PdfFormXObject(new Rectangle(50, 10));

        document.addEventHandler(PdfDocumentEvent.END_PAGE, (event) -> drawFooter(event));
    }

    private void drawFooter(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();

        int pageNum = pdf.getPageNumber(page);

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setLineWidth(1);
        canvas.moveTo(50, 40).lineTo(page.getPageSize().getWidth() - 50, 40).closePathStroke();

        PdfFont font = pdf.getDefaultFont();
        String prefix = pageNum + "/";

        float prefixWidth = font.getWidth(prefix, 10);

        canvas.beginText()
                //.setTextMatrix(page.getPageSize().getWidth() - 10 - prefixWidth, 30)
                .setTextMatrix(page.getPageSize().getWidth() - 50 - prefixWidth, 17)
                .setFontAndSize(font, 10)
                .showText(prefix)
                .endText();

        canvas.addXObjectAt(totalPageNumber, page.getPageSize().getWidth() - 50, 17);

        canvas.concatMatrix(.15, 0, 0, .15, 30, 8);
        canvas.addXObjectAt(logo, 0, 0);

        canvas.release();
    }

    public void end() {
        PdfFont font = document.getDefaultFont();
        PdfCanvas canvas = new PdfCanvas(totalPageNumber, document);
        canvas.beginText()
                .setFontAndSize(font, 10)
                .showText(String.valueOf(document.getNumberOfPages()))
                .endText();
        canvas.release();
    }
}
