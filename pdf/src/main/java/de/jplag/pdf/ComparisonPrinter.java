package de.jplag.pdf;

import java.io.File;
import java.io.IOException;

import de.jplag.JPlagComparison;
import de.jplag.Language;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.pdf.highlighting.SyntaxHighlightPerformer;
import de.jplag.pdf.highlighting.SyntaxHighlighterRegistry;
import de.jplag.pdf.pdfComponents.Tables;
import de.jplag.pdf.pdfComponents.Texts;
import de.jplag.pdf.utils.ColorGenerator;
import de.jplag.pdf.utils.Fonts;
import de.jplag.pdf.utils.MathUtils;
import de.jplag.pdf.utils.PathIdLookup;
import de.jplag.pdf.utils.SourceFileTreeBuilder;
import de.jplag.util.FileUtils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.BorderRadius;

public class ComparisonPrinter {
    private final Document doc;
    private final JPlagComparison comparison;
    private final ColorGenerator<Match> colorGenerator;
    private final Language language;

    public ComparisonPrinter(Document doc, JPlagComparison comparison, Language language) {
        this.doc = doc;
        this.comparison = comparison;
        this.colorGenerator = new ColorGenerator<>(this.comparison.matches().size());
        this.language = language;
    }

    public void printComparison() throws IOException {
        String title = "Comparison:\n" + this.comparison.firstSubmission().getName() + " - " + this.comparison.secondSubmission().getName();
        Texts.addTitle(this.doc, title);

        printMetadata();

        this.doc.add(new AreaBreak());
        printSubmissionData(this.comparison.firstSubmission());
        this.doc.add(new AreaBreak());
        printSubmissionData(this.comparison.secondSubmission());
    }

    public void printMetadata() {
        Table metadataTable = new Table(3);

        Submission left = this.comparison.firstSubmission();
        Submission right = this.comparison.secondSubmission();

        Tables.addHeaderCell(metadataTable, "");
        Tables.addHeaderCell(metadataTable, left.getName());
        Tables.addHeaderCell(metadataTable, right.getName());

        Tables.addHeaderCell(metadataTable, "Average Similarity");
        Tables.addTextCell(metadataTable, MathUtils.convertToPercent(this.comparison.similarity()), 1, 2);
        Tables.addHeaderCell(metadataTable, "Maximum Similarity");
        Tables.addTextCell(metadataTable, MathUtils.convertToPercent(this.comparison.maximalSimilarity()), 1, 2);

        Tables.addHeaderCell(metadataTable, "Similarity");
        Tables.addTextCell(metadataTable, MathUtils.convertToPercent(this.comparison.similarityOfFirst()));
        Tables.addTextCell(metadataTable, MathUtils.convertToPercent(this.comparison.similarityOfSecond()));

        Tables.addHeaderCell(metadataTable, "Token count");
        Tables.addTextCell(metadataTable, left.getNumberOfTokens());
        Tables.addTextCell(metadataTable, right.getNumberOfTokens());

        this.doc.add(metadataTable.useAllAvailableWidth());
    }

    private void printSubmissionData(Submission submission) throws IOException {
        Texts.addSubtitle(this.doc, "Submission: " + submission.getName());

        printFileTree(submission);
        for (File file : submission.getFiles()) {
            printSourceFile(file, submission);
        }
    }

    private void printSourceFile(File file, Submission submission) throws IOException {
        this.doc.add(new AreaBreak());
        String text = FileUtils.readFileContent(file).replaceAll("\t", "    ");

        this.doc.add(new Paragraph(file.getName()).setDestination(PathIdLookup.getInstance().getIdFor(file)).setBorder(new SolidBorder(1f))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        Table codeTable = new Table(2);
        codeTable.useAllAvailableWidth();
        String[] lines = text.split(System.lineSeparator());

        StringBuilder lineNumbers = new StringBuilder();
        int numberOfDigits = MathUtils.getNumberOfDigits(lines.length);

        for (int i = 0; i < lines.length; i++) {
            String lineNumber = String.valueOf(i + 1);
            lineNumbers.append("\n").append(" ".repeat((numberOfDigits - lineNumber.length()) + 1)).append(lineNumber);
        }

        TextStyleEditor styleEditor = new TextStyleEditor(lines);
        SyntaxHighlightPerformer performer = new SyntaxHighlightPerformer(lines, styleEditor);
        SyntaxHighlighterRegistry.getInstance().getSyntaxHighlighterForFile(file, this.language).performHighlight(performer);

        applyMatchHighlighting(styleEditor, file, submission);

        styleEditor.addTo(codeTable, Fonts.jetBrainsMono(this.doc));

        this.doc.add(codeTable);
    }

    private void applyMatchHighlighting(TextStyleEditor styleEditor, File file, Submission submission) {
        for (Match match : this.comparison.matches()) {
            int startIndex, endIndex;
            if (submission.equals(this.comparison.firstSubmission())) {
                startIndex = match.startOfFirst();
                endIndex = match.endOfFirst();
            } else {
                startIndex = match.startOfSecond();
                endIndex = match.endOfSecond();
            }
            if (submission.getTokenList().get(startIndex).getFile().equals(file)) {
                Token start = submission.getTokenList().get(startIndex);
                Token end = submission.getTokenList().get(endIndex);

                styleEditor.styleRange(start.getLine() - 1, start.getColumn() - 1, end.getLine() - 1, end.getColumn() - 1,
                        this.colorGenerator.getColor(match), null);
            }
        }
    }

    private void printFileTree(Submission submission) throws IOException {
        SourceFileTreeBuilder treeBuilder = new SourceFileTreeBuilder(submission);
        Table overviewTable = new Table(1).setBorder(new SolidBorder(1f)).setBorderRadius(new BorderRadius(20f));
        overviewTable.addCell(new Cell().add(new Paragraph("File tree").setFont(Fonts.jetBrainsMono(this.doc))));
        overviewTable.addCell(new Cell().add(new Paragraph().addAll(treeBuilder.buildText()).setFont(Fonts.jetBrainsMono(this.doc))));
        this.doc.add(overviewTable.useAllAvailableWidth());
    }
}
