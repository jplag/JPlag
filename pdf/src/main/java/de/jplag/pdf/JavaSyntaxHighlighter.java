package de.jplag.pdf;

import com.itextpdf.kernel.colors.ColorConstants;

public class JavaSyntaxHighlighter extends SyntaxHighlighter {
    public JavaSyntaxHighlighter(String[] lines, TextStyleEditor textStyleEditor) {
        super(lines, textStyleEditor);
    }

    @Override
    public void performHighlight() {
        this.highlightKeyword("public", ColorConstants.YELLOW);
        this.highlightRanges("\"", "\"", ColorConstants.GREEN);
        this.highlightRanges("/*", "*/", ColorConstants.GRAY);
        this.highlightToEndOfLine("//", ColorConstants.GRAY);
    }
}
