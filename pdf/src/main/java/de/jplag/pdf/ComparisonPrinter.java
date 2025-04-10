package de.jplag.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.util.FileUtils;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class ComparisonPrinter {
    public static void printComparison(JPlagComparison comparison, Document doc) throws IOException {
        String title = "Comparison:\n" + comparison.firstSubmission().getName() + " - " + comparison.secondSubmission().getName();

        doc.add(new Paragraph(title).setFontSize(30).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Div().setHeight(new UnitValue(UnitValue.POINT, 30)));

        InputStream inputStream = ComparisonPrinter.class.getResourceAsStream("/JetBrainsMono-Regular.ttf");
        byte[] fontBytes = inputStream.readAllBytes();
        PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.UTF8);
        Color veryLightGray = Color.createColorWithColorSpace(new float[] {.9f, .9f, .9f});
        Submission submission = comparison.firstSubmission();
        for (File file : submission.getFiles()) {
            String text = FileUtils.readFileContent(file).replaceAll("\t", "    ");
            System.out.println(text);
            doc.add(new Paragraph(file.getName()).setBorder(new SolidBorder(1f)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            Table codeTable = new Table(2);
            codeTable.useAllAvailableWidth();
            String[] lines = text.split(System.lineSeparator());

            StringBuilder lineNumbers = new StringBuilder();
            int numberOfDigits = (lines.length == 0) ? 1 : (int) Math.log10(lines.length) + 1;

            for (int i = 0; i < lines.length; i++) {
                String lineNumber = String.valueOf(i + 1);
                lineNumbers.append("\n").append(" ".repeat((numberOfDigits - lineNumber.length()) + 1)).append(lineNumber);
            }

            TextStyleEditor styleEditor = new TextStyleEditor(lines);
            new JavaSyntaxHighlighter(lines, styleEditor).performHighlight();

            ColorGenerator<Match> colorGenerator = new ColorGenerator<>(comparison.matches().size());
            for (Match match : comparison.matches()) {
                if (comparison.firstSubmission().getTokenList().get(match.startOfFirst()).getFile().equals(file)) {
                    Token start = comparison.firstSubmission().getTokenList().get(match.startOfFirst());
                    Token end = comparison.firstSubmission().getTokenList().get(match.endOfFirst());

                    styleEditor.styleRange(start.getLine() - 1, start.getColumn() - 1, end.getLine() - 1, end.getColumn() - 1,
                            colorGenerator.getColor(match), null);
                }
            }

            /*
             * codeTable.addCell(new Cell().add(new
             * Paragraph(lineNumbers.substring(1)).setFixedLeading(17f).setFont(font)).setBorder(null)); Cell second = new Cell();
             * styleEditor.addTo(second, font, veryLightGray);
             * second.setFont(font).setBorder(null).setBackgroundColor(veryLightGray); codeTable.addCell(second);
             */
            styleEditor.addTo(codeTable, font);

            doc.add(codeTable);
        }
    }
}
