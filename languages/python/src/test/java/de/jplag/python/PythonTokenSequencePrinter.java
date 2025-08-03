package de.jplag.python;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import de.jplag.Token;
import de.jplag.treesitter.TreeSitterTraversal;

import io.github.treesitter.jtreesitter.InputEncoding;
import io.github.treesitter.jtreesitter.Language;
import io.github.treesitter.jtreesitter.Parser;
import io.github.treesitter.jtreesitter.Tree;

/**
 * Utility class for printing token sequences from Python test resources. Useful for debugging and generating expected
 * token sequences for tests.
 */
public final class PythonTokenSequencePrinter {

    /**
     * Prints the token sequence for a specific test file.
     * @param fileName the name of the test file (e.g., "BasicTest.py")
     * @param directory temporary directory to create the file in
     * @throws IOException if the file cannot be read or parsed
     */
    public static void printTokenSequence(String fileName, Path directory) throws IOException {
        String resourcePath = "/de/jplag/python/" + fileName;
        try (InputStream inputStream = PythonTokenSequencePrinter.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Could not find resource: " + resourcePath);
            }

            String pythonCode = new String(inputStream.readAllBytes());
            File testFile = directory.resolve(fileName).toFile();
            Files.writeString(testFile.toPath(), pythonCode);

            Language language = new Language(TreeSitterPython.language());
            try (Parser parser = new Parser(language)) {
                Tree tree = parser.parse(pythonCode, InputEncoding.UTF_8).orElseThrow(() -> new RuntimeException("Failed to parse " + fileName));

                PythonTokenCollector collector = new PythonTokenCollector(testFile, pythonCode);
                TreeSitterTraversal.traverse(tree.getRootNode(), collector);
                List<Token> tokens = collector.getTokens();

                // Print all tokens in order
                System.out.println("Token Sequence for " + fileName + ":");
                for (int i = 0; i < tokens.size(); i++) {
                    Token token = tokens.get(i);
                    String tokenInformation = String.format("%s (line %d, col %d, len %d)", token.getType(), token.getLine(), token.getColumn(),
                            token.getLength());
                    System.out.println(tokenInformation);
                }
            }
        }
    }

    public static void main(String[] args) {
        String fileName = args[0];
        try {
            Path folderPath = Files.createTempDirectory(String.format("python-token-printer-%s", fileName));
            printTokenSequence(fileName, folderPath);
        } catch (IOException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.exit(1);
        }
    }
}
