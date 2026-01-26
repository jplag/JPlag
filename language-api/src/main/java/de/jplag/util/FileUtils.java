package de.jplag.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import de.jplag.ParsingException;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * Encapsulates various interactions with files to prevent issues with file encodings.
 */
public final class FileUtils {
    private static final Charset DEFAULT_OUTPUT_CHARSET = StandardCharsets.UTF_8;
    private static final char BYTE_ORDER_MARK = '\uFEFF';
    private static final int SINGLE_CHAR_BUFFER_SIZE = 10;

    private static Charset userSpecifiedCharset = null;

    private FileUtils() {
    }

    /**
     * Opens a file reader, guessing the charset from the content. Also, if the file is encoded in a UTF* encoding and a bom
     * exists, it is removed from the reader.
     * @param file The file to open for read
     * @param isSubmissionFile If true and a charset is set for submissions, that charset will be used always
     * @return The reader, configured with the best matching charset
     * @throws IOException If the file does not exist for is not readable
     */
    public static BufferedReader openFileReader(File file, boolean isSubmissionFile) throws IOException {
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        Charset charset = isSubmissionFile && userSpecifiedCharset != null ? userSpecifiedCharset : detectCharset(stream);
        BufferedReader reader = new BufferedReader(new FileReader(file, charset));
        removeBom(reader, charset);
        return reader;
    }

    /**
     * Opens a file reader, guessing the charset from the content. Also, if the file is encoded in a UTF* encoding and a bom
     * exists, it is removed from the reader.
     * @param file The file to open for read
     * @return The reader, configured with the best matching charset
     * @throws IOException If the file does not exist for is not readable
     */
    public static BufferedReader openFileReader(File file) throws IOException {
        return openFileReader(file, false);
    }

    /**
     * Reads the contents of a file into a single string.
     * @param file The file to read
     * @param isSubmissionFile If true and a charset is set for submissions, that charset will be used always
     * @return The files content as a string
     * @throws IOException If an IO error occurs
     * @see FileUtils#openFileReader(File)
     */
    public static String readFileContent(File file, boolean isSubmissionFile) throws IOException {
        try (BufferedReader reader = openFileReader(file, isSubmissionFile)) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    /**
     * Reads the contents of a file into a single string.
     * @param file The file to read
     * @return The files content as a string
     * @throws IOException If an IO error occurs
     * @see FileUtils#openFileReader(File)
     */
    public static String readFileContent(File file) throws IOException {
        return readFileContent(file, false);
    }

    /**
     * Removes the byte order mark from the beginning of the stream, if it exists and the charset is a UTF* charset. For
     * details see: <a href="https://en.wikipedia.org/wiki/Byte_order_mark">Wikipedia</a>.
     * @param reader The reader to remove the bom from
     * @throws IOException If an IO error occurs.
     */
    private static void removeBom(BufferedReader reader, Charset charset) throws IOException {
        if (charset.name().toUpperCase().startsWith("UTF")) {
            reader.mark(SINGLE_CHAR_BUFFER_SIZE);
            if (reader.read() != BYTE_ORDER_MARK) {
                reader.reset();
            }
        }
    }

    /**
     * Detects the charset of a file. Prefer using {@link #openFileReader(File)} or {@link #readFileContent(File)} if you
     * are only interested in the content.
     * @param file The file to detect
     * @return The most probable charset
     * @throws IOException If an IO error occurs
     */
    public static Charset detectCharset(File file) throws IOException {
        try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            return detectCharset(stream);
        }
    }

    /**
     * Detects the most probable charset over the whole set of files.
     * @param files The files to check
     * @param isSubmissionFile If true and a charset is set for submissions, that charset will be used always
     * @return The most probable charset
     * @throws ParsingException if reading the source files leads to an error.
     */
    public static Charset detectCharsetFromMultiple(Collection<File> files, boolean isSubmissionFile) throws ParsingException {
        if (isSubmissionFile && userSpecifiedCharset != null) {
            return userSpecifiedCharset;
        } else {
            return detectCharsetFromMultiple(files);
        }
    }

    /**
     * Detects the most probable charset over the whole set of files.
     * @param files The files to check
     * @return The most probable charset
     * @throws ParsingException if reading the source files leads to an error.
     */
    public static Charset detectCharsetFromMultiple(Collection<File> files) throws ParsingException {
        Map<String, List<Integer>> charsetValues = new HashMap<>();

        List<CharsetMatch[]> matchData = new ArrayList<>();
        for (File file : files) {
            try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
                matchData.add(detectAllCharsets(stream));
            } catch (IOException e) {
                throw new ParsingException(file, e);
            }
        }

        for (CharsetMatch[] matches : matchData) {
            Set<String> remaining = new HashSet<>(Set.of(CharsetDetector.getAllDetectableCharsets()));
            for (CharsetMatch match : matches) {
                charsetValues.putIfAbsent(match.getName(), new ArrayList<>());
                charsetValues.get(match.getName()).add(match.getConfidence());
                remaining.remove(match.getName());
            }
            remaining.forEach(it -> {
                charsetValues.putIfAbsent(it, new ArrayList<>());
                charsetValues.get(it).add(0);
            });
        }

        AtomicReference<Charset> mostProbable = new AtomicReference<>(StandardCharsets.UTF_8);
        AtomicReference<Double> mostProbableConfidence = new AtomicReference<>(0.0);
        charsetValues.forEach((charset, confidenceValues) -> {
            double average = confidenceValues.stream().mapToInt(it -> it).average().orElse(0);
            if (confidenceValues.stream().anyMatch(it -> it == 0)) {
                average = 0;
            }
            if (average > mostProbableConfidence.get()) {
                mostProbable.set(Charset.forName(charset));
                mostProbableConfidence.set(average);
            }
        });

        return mostProbable.get();
    }

    private static Charset detectCharset(InputStream stream) throws IOException {
        CharsetDetector charsetDetector = new CharsetDetector();

        charsetDetector.setText(stream);

        CharsetMatch match = charsetDetector.detect();
        return Charset.forName(match.getName());
    }

    private static CharsetMatch[] detectAllCharsets(InputStream stream) throws IOException {
        CharsetDetector charsetDetector = new CharsetDetector();

        charsetDetector.setText(stream);

        return charsetDetector.detectAll();
    }

    /**
     * Opens a file writer, using the default charset for JPlag.
     * @param file The file to write
     * @return The file writer, configured with the default charset
     * @throws IOException If the file does not exist or is not writable
     */
    public static Writer openFileWriter(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file, DEFAULT_OUTPUT_CHARSET));
    }

    /**
     * Writes the given content into the given file using the default charset.
     * @param file The file
     * @param content The content
     * @throws IOException If any error occurs
     */
    public static void write(File file, String content) throws IOException {
        Writer writer = openFileWriter(file);
        writer.write(content);
        writer.close();
    }

    /**
     * Checks if the given file can be written to. If the file does not exist checks if it can be created.
     * @param file The file to check
     * @return true, if the file can be written to
     */
    public static boolean checkWritable(File file) {
        if (file.exists()) {
            return file.canWrite();
        }
        return checkParentWritable(file);
    }

    /**
     * Checks if the parent file can be written to.
     * @param file The file to check
     * @return true, if the parent can be written to
     */
    public static boolean checkParentWritable(File file) {
        return file.getAbsoluteFile().getParentFile().canWrite();
    }

    /**
     * Overrides the charset detection with a specified charset.
     * @param userSpecifiedCharset is the overriding charset.
     */
    public static void setOverrideSubmissionCharset(Charset userSpecifiedCharset) {
        FileUtils.userSpecifiedCharset = userSpecifiedCharset;
    }
}
