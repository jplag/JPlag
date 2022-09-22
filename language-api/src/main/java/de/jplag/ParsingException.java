package de.jplag;

import java.io.File;
import java.io.Serial;

/**
 * An exception to throw if any error occurred while parsing files in a language frontend.
 */
public class ParsingException extends Exception {
    @Serial
    private static final long serialVersionUID = 4385949762027596330L; // generated

    /**
     * Constructs a new exception indicating a parsing exception in the given file without a specific reason.
     * @param file the file in which a parsing error occurred. (A null value is permitted, and indicates that the file is
     * nonexistent or unknown.)
     */
    public ParsingException(File file) {
        this(file, (String) null);
    }

    /**
     * Constructs a new exception indicating a parsing exception in the given file with the given reason.
     * @param file the file in which a parsing error occurred. (A null value is permitted, and indicates that the file is
     * nonexistent or unknown.)
     * @param reason the reason the parsing failed. A null value is permitted.)
     */
    public ParsingException(File file, String reason) {
        super(constructMessage(file, reason));
    }

    /**
     * Constructs a new exception indicating a parsing exception in the given file with the given cause.
     * @param file the file in which a parsing error occurred. (A null value is permitted, and indicates that the file is
     * nonexistent or unknown.)
     * @param cause the cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParsingException(File file, Throwable cause) {
        this(file, null, cause);
    }

    /**
     * Constructs a new exception indicating a parsing exception in the given file with the given reason and cause.
     * @param file the file in which a parsing error occurred. (A null value is permitted, and indicates that the file is
     * nonexistent or unknown.)
     * @param reason the reason the parsing failed. A null value is permitted.)
     * @param cause the cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParsingException(File file, String reason, Throwable cause) {
        super(constructMessage(file, reason), cause);
    }

    private static String constructMessage(File file, String reason) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("failed to parse '%s'".formatted(file));
        if (!reason.isEmpty() && !reason.isBlank()) {
            messageBuilder.append(" with reason: %s".formatted(reason));
        }
        return messageBuilder.toString();
    }
}
