package de.jplag.cli.antlrtesttool;

/**
 * Data for the output tree.
 * @param start The start of the highlight matching this node
 * @param end The end of the highlight matching this node
 * @param text The text to display
 */
public record TreeEntry(int start, int end, String text) {
    @Override
    public String toString() {
        return text;
    }
}
