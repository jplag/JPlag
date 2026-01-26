package de.jplag;

import java.io.File;
import java.io.Serial;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * An exception to throw if any error occurred while parsing files in a language module.
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

    private ParsingException(String message) {
        super(message);
    }

    /**
     * Creates a new parsing exception which wraps the provided exceptions. If no exception to wrap is provided, null is
     * returned. If only one exception is provided, it is returned.
     * @param exceptions the collection of exceptions to wrap.
     * @return a new parsing exception wrapping the provided exceptions, <code>null</code> if no exceptions are provided, or
     * the provided exception if only one was provided.
     */
    public static ParsingException wrappingExceptions(Collection<ParsingException> exceptions) {
        return switch (exceptions.size()) {
            case 0 -> null;
            case 1 -> exceptions.iterator().next();
            default -> {
                String message = exceptions.stream().map(ParsingException::getMessage).collect(Collectors.joining("\n"));
                yield new ParsingException(message);
            }
        };
    }

    private static String constructMessage(File file, String reason) {
        StringBuilder messageBuilder = new StringBuilder();
        String fileName = file == null ? "<null>" : file.toString();
        if (reason == null || !reason.contains(fileName)) {
            messageBuilder.append("failed to parse '%s'".formatted(fileName));
        }
        if (reason != null && !reason.isBlank()) {
            messageBuilder.append(reason);
        }
        return messageBuilder.toString();
    }
}
