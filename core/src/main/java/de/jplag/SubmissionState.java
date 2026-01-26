package de.jplag;

/**
 * Reflects the state of a submission. At the beginning it is UNPARSED. After successful parsing it is VALID. In all
 * other cases it reflects why it is inavalid.
 */
public enum SubmissionState {
    VALID,
    NOTHING_TO_PARSE,
    CANNOT_PARSE,
    TOO_SMALL,
    UNPARSED;
}
