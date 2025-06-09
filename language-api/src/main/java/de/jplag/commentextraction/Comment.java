package de.jplag.commentextraction;

import java.io.File;

/**
 * Stores a single comment of a submitted file.
 * @param file File the comment originated from
 * @param content Textual content of the comment
 * @param line Line of the comment (1-indexed)
 * @param column Column of the comment (1-indexed)
 * @param type Type of the comment
 */
public record Comment(File file, String content, int line, int column, CommentType type) {

}
