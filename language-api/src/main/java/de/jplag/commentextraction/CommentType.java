package de.jplag.commentextraction;

/**
 * Enum specifying the type of a comment.
 */
public enum CommentType {
    /**
     * Comment that stretches until the end of the current line.
     */
    LINE,
    /**
     * Comment that can span multiple lines until the ending delimiter.
     */
    BLOCK,
}
