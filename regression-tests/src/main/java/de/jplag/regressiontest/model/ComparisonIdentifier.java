package de.jplag.regressiontest.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Identifier for a comparison. The order of the names does not matter.
 * @param firstName The first name
 * @param secondName The second name
 */
public record ComparisonIdentifier(String firstName, String secondName) {
    private static final String INVALID_LINE_ERROR_MESSAGE = "Comparison identifier file (%s) has an invalid line: %s";

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComparisonIdentifier other)) {
            return false;
        }

        return firstName.equals(other.firstName) && secondName.equals(other.secondName)
                || secondName.equals(other.firstName) && firstName.equals(other.secondName);
    }

    @Override
    public int hashCode() {
        return firstName.hashCode() + secondName.hashCode();
    }

    /**
     * Loads the identifiers stored in a csv (semicolon separated) file.
     * @param file The file to load
     * @return The comparisons in the file
     */
    public static Set<ComparisonIdentifier> loadIdentifiersFromFile(Path file, String delimiter) throws IOException {
        try (Scanner scanner = new Scanner(file)) {
            Set<ComparisonIdentifier> identifiers = new HashSet<>();
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(delimiter);
                if (parts.length != 2) {
                    throw new IllegalStateException(String.format(INVALID_LINE_ERROR_MESSAGE, file.toRealPath(), String.join(delimiter, parts)));
                }
                identifiers.add(new ComparisonIdentifier(parts[0], parts[1]));
            }
            return identifiers;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(String.format("Comparisons could not be loaded for %s.", file.getFileName()), e);
        }
    }

    @Override
    public String toString() {
        return firstName + " - " + secondName;
    }
}
