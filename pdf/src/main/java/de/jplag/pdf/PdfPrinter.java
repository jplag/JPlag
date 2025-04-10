package de.jplag.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PdfPrinter {
    private JPlagResult result;
    private Document document;

    public PdfPrinter(JPlagResult result, File target) throws FileNotFoundException {
        this.result = result;
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(target));
        InputStream licenseStream = this.getClass().getResourceAsStream("/JetBrainsMono-License.txt");
        this.document = new Document(pdfDocument);
        try {
            this.document.getPdfDocument().getDocumentInfo().setMoreInfo("JetBrainsMono-License", readInputStreamToString(licenseStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printOverview() {
        OverviewPrinter.printOverview(this.document, this.result);
    }

    public void printComparison(JPlagComparison comparison) throws IOException {
        ComparisonPrinter.printComparison(comparison, this.document);
    }

    public void save() {
        this.document.close();
    }

    private static String readInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }
}
