package de.jplag.commentextraction;

/**
 * Exception indicating that the CommentExtractor expected a different character sequence at the start of the parsing
 * input, causing the comment extraction to fail.
 */
public class UnexpectedStringException extends Exception {

    private static final long serialVersionUID = 6086579651767129532L; // generated

    public UnexpectedStringException(String message) {
        super(message);
    }
}
