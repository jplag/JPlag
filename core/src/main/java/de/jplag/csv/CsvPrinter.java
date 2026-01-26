package de.jplag.csv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import de.jplag.util.FileUtils;

/**
 * Prints a csv according to the specification in
 * <a href="https://datatracker.ietf.org/doc/html/rfc4180#section-2">...</a>. If you need to deviate from this
 * definition slightly you can modify the line end and separator characters.
 * @param <T> is the type of the values stored in the CSV.
 */
public class CsvPrinter<T> {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final String DEFAULT_LINE_END = "\r\n"; // not System.lineSeparator(), because of csv specification
    private static final char LITERAL = '"';

    private final CsvDataMapper<T> dataSource;
    private final List<String[]> data;

    private char separator;
    private String lineEnd;

    /**
     * @param dataSource The data source used to map the given object to rows.
     */
    public CsvPrinter(CsvDataMapper<T> dataSource) {
        this.dataSource = dataSource;
        this.data = new ArrayList<>();

        this.separator = DEFAULT_SEPARATOR;
        this.lineEnd = DEFAULT_LINE_END;
    }

    /**
     * Adds a new row to this csv.
     * @param value the value to add.
     */
    public void addRow(T value) {
        this.data.add(this.dataSource.provideData(value));
    }

    /**
     * Adds multiple rows to this csv.
     * @param values The values to add.
     */
    public void addRows(Collection<T> values) {
        values.forEach(this::addRow);
    }

    /**
     * Changes the separator between cells.
     * @param separator The new separator.
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * Sets the string to separate lines with.
     * @param lineEnd the new line end.
     */
    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }

    /**
     * Prints this csv with all current data to a file.
     * @param file The file to write.
     * @throws IOException on io errors.
     */
    public void printToFile(File file) throws IOException {
        try (Writer writer = FileUtils.openFileWriter(file)) {
            this.printCsv(writer);
        }
    }

    /**
     * Provides a string-based representation of the table.
     * @return the representation.
     * @throws IOException if an errors occurs during conversion.
     */
    public String printToString() throws IOException {
        String csv;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (Writer writer = new OutputStreamWriter(outputStream)) {
                this.printCsv(writer);
            }

            csv = outputStream.toString();
        }

        return csv;
    }

    private void printCsv(Writer writer) throws IOException {
        this.writeTitleRow(writer);

        for (String[] datum : this.data) {
            this.printRow(writer, datum);
        }
    }

    private void writeTitleRow(Writer writer) throws IOException {
        Optional<String[]> titleRow = this.dataSource.getTitleRow();
        if (titleRow.isPresent()) {
            this.printRow(writer, titleRow.get());
        }
    }

    private void printRow(Writer writer, String[] data) throws IOException {
        Iterator<String> dataIterator = Arrays.stream(data).iterator();

        if (dataIterator.hasNext()) {
            printCell(writer, dataIterator.next());
        }

        while (dataIterator.hasNext()) {
            writer.write(this.separator);
            printCell(writer, dataIterator.next());
        }

        writer.write(this.lineEnd);
    }

    private void printCell(Writer writer, String cellValue) throws IOException {
        boolean literalsNeeded = cellValue.contains(String.valueOf(LITERAL));
        String actualValue = cellValue;
        if (literalsNeeded) {
            writer.write(LITERAL);
            actualValue = actualValue.replace("\"", "\"\"");
        }
        writer.write(actualValue);
        if (literalsNeeded) {
            writer.write(LITERAL);
        }
    }
}
