package jplag.reporting;

/**
 * This class represents one markup tag that will be included in the text. It is necessary to sort the objects before
 * they are included into the text, so that the original position can be found.
 */
public class MarkupText {
    public int fileIndex, lineIndex, column;
    public String text;
    public boolean frontMarkup = false;

    public MarkupText(int fileIndex, int lineIndex, int column, String text, boolean frontMarkup) {
        this.fileIndex = fileIndex;
        this.lineIndex = lineIndex;
        this.column = column;
        this.text = text;
        this.frontMarkup = frontMarkup;
    }

    @Override
    public String toString() {
        return "MarkUp - file: " + fileIndex + " line: " + lineIndex + " column: " + column + " text: " + text;
    }
}
