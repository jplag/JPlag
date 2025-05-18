package de.jplag.commentextraction;

import java.util.List;

public record CommentExtractorSettings(List<EnvironmentDelimiter> noCommentEnvironments, List<String> lineCommentDelimiters,
        List<EnvironmentDelimiter> blockCommentDelimiters, List<String> escapeSequences) {
}
