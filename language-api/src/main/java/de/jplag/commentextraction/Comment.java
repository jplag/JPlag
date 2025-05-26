package de.jplag.commentextraction;

import java.io.File;

public record Comment(File file, String content, int line, int column, CommentType type) {

}
