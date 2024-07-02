package de.jplag;

/**
 * Exception indicating a critical error within the parser itself. This error is not associated with a specific
 * submission but signifies a broader issue. When this exception is thrown, it means that the parser cannot continue
 * parsing.
 */
public class CriticalParsingException extends ParsingException {

    private static final long serialVersionUID = -637253490714738666L; // generated

    /**
     * Constructs a new exception indicating a critical parsing exception.
     * @param reason the reason the parsing failed. A null value is permitted.)
     */
    public CriticalParsingException(String reason) {
        super(null, reason);
    }
}
