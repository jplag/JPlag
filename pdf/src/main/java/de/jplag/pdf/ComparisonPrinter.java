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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class ComparisonPrinter {
    public static void printComparison(JPlagComparison comparison, Document doc) throws IOException {
        String title = "Comparison:\n" + comparison.firstSubmission().getName() + " - " + comparison.secondSubmission().getName();

        doc.add(new Paragraph(title).setFontSize(30).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Div().setHeight(new UnitValue(UnitValue.POINT, 30)));

        printMetadata(doc, comparison);

        InputStream inputStream = ComparisonPrinter.class.getResourceAsStream("/JetBrainsMono-Regular.ttf");
        byte[] fontBytes = inputStream.readAllBytes();
        PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.UTF8);

        doc.add(new AreaBreak());
        printSubmissionData(doc, comparison, comparison.firstSubmission(), font);
        doc.add(new AreaBreak());
        printSubmissionData(doc, comparison, comparison.secondSubmission(), font);
    }

    public static void printMetadata(Document doc, JPlagComparison comparison) {
        Table metadataTable = new Table(3);

        Submission left = comparison.firstSubmission();
        Submission right = comparison.secondSubmission();

        metadataTable.addCell(new Cell().add(new Paragraph("")));
        metadataTable.addCell(new Cell().add(new Paragraph(left.getName())));
        metadataTable.addCell(new Cell().add(new Paragraph(right.getName())));

        metadataTable.addCell(new Cell().add(new Paragraph("Average Similarity")));
        metadataTable.addCell(new Cell(1, 2).add(new Paragraph(String.valueOf(comparison.similarity()))));
        metadataTable.addCell(new Cell().add(new Paragraph("Maximum Similarity")));
        metadataTable.addCell(new Cell(1, 2).add(new Paragraph(String.valueOf(comparison.maximalSimilarity()))));

        metadataTable.addCell(new Cell().add(new Paragraph("Similarity")));
        metadataTable.addCell(new Cell().add(new Paragraph(String.valueOf(comparison.similarityOfFirst()))));
        metadataTable.addCell(new Cell().add(new Paragraph(String.valueOf(comparison.similarityOfSecond()))));

        metadataTable.addCell(new Cell().add(new Paragraph("Token count")));
        metadataTable.addCell(new Cell().add(new Paragraph(String.valueOf(left.getNumberOfTokens()))));
        metadataTable.addCell(new Cell().add(new Paragraph(String.valueOf(right.getNumberOfTokens()))));

        doc.add(metadataTable.useAllAvailableWidth());
    }

    private static void printSubmissionData(Document doc, JPlagComparison comparison, Submission submission, PdfFont font) throws IOException {
        doc.add(new Paragraph("Submission: " + submission.getName()).setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Div().setHeight(new UnitValue(UnitValue.POINT, 20)));

        printFileTree(doc, submission, font);
        for (File file : submission.getFiles()) {
            doc.add(new AreaBreak());
            String text = FileUtils.readFileContent(file).replaceAll("\t", "    ");
            System.out.println(text);
            doc.add(new Paragraph(file.getName()).setDestination(PathIdLookup.getInstance().getIdFor(file)).setBorder(new SolidBorder(1f))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

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
                if (submission.getTokenList().get(match.startOfFirst()).getFile().equals(file)) {
                    Token start = submission.getTokenList().get(match.startOfFirst());
                    Token end = submission.getTokenList().get(match.endOfFirst());

                    styleEditor.styleRange(start.getLine() - 1, start.getColumn() - 1, end.getLine() - 1, end.getColumn() - 1,
                            colorGenerator.getColor(match), null);
                }
            }

            styleEditor.addTo(codeTable, font);

            doc.add(codeTable);
        }
    }

    public static void printFileTree(Document doc, Submission submission, PdfFont font) {
        SourceFileTreeBuilder treeBuilder = new SourceFileTreeBuilder(submission);
        Table overviewTable = new Table(1).setBorder(new SolidBorder(1f)).setBorderRadius(new BorderRadius(20f));
        overviewTable.addCell(new Cell().add(new Paragraph("File tree").setFont(font)));
        overviewTable.addCell(new Cell().add(new Paragraph().addAll(treeBuilder.buildText()).setFont(font)));
        doc.add(overviewTable.useAllAvailableWidth());
    }
}
