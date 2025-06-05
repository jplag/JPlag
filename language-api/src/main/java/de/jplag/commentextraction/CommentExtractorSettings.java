package de.jplag.commentextraction;

import java.util.List;

/**
 * Settings for the comment extractor.
 * @param noCommentEnvironments Environments in which comment delimiters are ignored
 * @param lineCommentDelimiters Delimiters signaling the start of a line comment (ends at end-of-line)
 * @param blockCommentDelimiters Delimiters signaling the start and end of a block comment
 * @param escapeSequences Escape sequences which ignore the next character for parsing
 */
public record CommentExtractorSettings(List<EnvironmentDelimiter> noCommentEnvironments, List<String> lineCommentDelimiters,
        List<EnvironmentDelimiter> blockCommentDelimiters, List<String> escapeSequences) {
}
