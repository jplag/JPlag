package de.jplag.java_cpg.evaluation;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This tests <a href="https://pmd.github.io/">PMD</a> to compare it to our results.
 * @author GitHub Copilot (Claude Sonnet 4.5)
 * @version 1.0
 */
public class PmdTest {

    @Test
    @Disabled("Only for testing the setup and PMD integration, not a real test")
    void singleTest() throws Exception {
        // File file = new File("src/test/resources/java/ai/deadCode5");
        // File file = new File("src/test/resources/java/aiGenerated/claude/Project10.java");
        File file = new File("src/test/resources/java/progpedia/00000022/ACCEPTED/00022_00003/infra_estrutura.java");
        int deadLines = getPmdDeadLinesInFile(file);
        // File virtFile = runPmdForFile(file);
        // assertNotNull(virtFile);
        System.out.println("Dead lines: " + deadLines);
    }

    /**
     * Runs PMD on the given file or directory and counts the number of lines that PMD considers dead code.
     * @param file The Java file or directory to analyze with PMD.
     * @return The number of lines that PMD considers dead code.
     * @throws IOException If there is an error running PMD or processing files.
     */
    public static int getPmdDeadLinesInFile(@NotNull File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("File does not exist: " + file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            // Find the actual Java file in the directory that has violations
            File virtFile = runPmdForFile(file);

            // Count total lines in all Java files in the directory
            int totalOriginalLines = 0;
            List<File> javaFiles = findJavaFiles(file);
            for (File javaFile : javaFiles) {
                totalOriginalLines += Files.readAllLines(javaFile.toPath()).size();
            }

            int virtualLines = Files.readAllLines(virtFile.toPath()).size();
            return totalOriginalLines - virtualLines;
        } else {
            File virtFile = runPmdForFile(file);
            List<String> originalLines = Files.readAllLines(file.toPath());
            List<String> virtualLines = Files.readAllLines(virtFile.toPath());
            return originalLines.size() - virtualLines.size();
        }
    }

    private static @NotNull List<File> findJavaFiles(@NotNull File directory) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    /**
     * Runs PMD on the given file or directory and returns a virtual file with violation lines removed.
     * @param file The Java file or directory to analyze with PMD.
     * @return A virtual file with PMD violation lines removed.
     * @throws IOException If there is an error running PMD or processing files.
     */
    public static @NotNull File runPmdForFile(@NotNull File file) throws IOException {
        String pmdPath = System.getProperty("user.home") + "/pmd-bin-7.21.0/bin/pmd";
        String folder = file.getAbsolutePath();
        ProcessBuilder processBuilder = new ProcessBuilder(pmdPath, "check", "-d", folder, "-R", "src/test/resources/pmdDeadCodeRules.xml", "-f",
                "xml");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Read the output as XML
        StringBuilder xmlOutput = new StringBuilder();
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!(line.trim().startsWith("[WARN]"))) {
                    xmlOutput.append(line).append("\n");
                }
            }
        }
        Document doc;
        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {    // PMD returns 0 if no violations, 4 if violations found, 1 for errors
                throw new RuntimeException("PMD command failed with exit code: " + exitCode);
            }
            System.out.println("PMD exited with code: " + exitCode);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlOutput.toString())));
            System.out.println("\n=== PMD XML Report Successfully Parsed ===");
        } catch (ParserConfigurationException | InterruptedException | SAXException e) {
            throw new IOException("Failed to parse PMD XML output: " + e + "| Output was:\n" + xmlOutput);
        }

        // Extract violations
        List<ViolationInfo> violationsList = extractViolations(doc);
        System.out.println("Number of violations found: " + violationsList.size());

        // Create virtual files with violations removed
        return createVirtualFilesWithViolationsRemoved(violationsList, file);
    }

    /**
     * Represents information about a PMD violation.
     * @author GitHub Copilot (Claude Sonnet 4.5)
     */
    static class ViolationInfo {
        private String filePath;
        private int beginLine;
        private int endLine;
        private int beginColumn;
        private int endColumn;
        private String rule;
        private String message;

        @Override
        public String toString() {
            return String.format("Violation[%s:%d-%d, rule=%s, message=%s]", filePath, beginLine, endLine, rule, message);
        }
    }

    /**
     * Extracts violation information from the PMD XML document.
     */
    private static @NotNull List<ViolationInfo> extractViolations(@NotNull Document doc) {
        List<ViolationInfo> violations = new ArrayList<>();
        NodeList fileNodes = doc.getElementsByTagName("file");

        for (int i = 0; i < fileNodes.getLength(); i++) {
            Element fileElement = (Element) fileNodes.item(i);
            String filePath = fileElement.getAttribute("name");

            NodeList violationNodes = fileElement.getElementsByTagName("violation");
            for (int j = 0; j < violationNodes.getLength(); j++) {
                Element violationElement = (Element) violationNodes.item(j);

                ViolationInfo info = new ViolationInfo();
                info.filePath = filePath;
                info.beginLine = Integer.parseInt(violationElement.getAttribute("beginline"));
                info.endLine = Integer.parseInt(violationElement.getAttribute("endline"));
                info.beginColumn = Integer.parseInt(violationElement.getAttribute("begincolumn"));
                info.endColumn = Integer.parseInt(violationElement.getAttribute("endcolumn"));
                info.rule = violationElement.getAttribute("rule");
                info.message = violationElement.getTextContent().trim();

                violations.add(info);
                System.out.println("  - " + info);
            }
        }
        return violations;
    }

    /**
     * Creates a virtual file with violation lines removed.
     */
    private static @NotNull File createVirtualFilesWithViolationsRemoved(@NotNull List<ViolationInfo> violations, @NotNull File originalFile)
            throws IOException {
        // Group violations by file
        Map<String, List<ViolationInfo>> violationsByFile = new HashMap<>();
        for (ViolationInfo violation : violations) {
            violationsByFile.computeIfAbsent(violation.filePath, _ -> new ArrayList<>()).add(violation);
        }
        assert violationsByFile.size() == 1 || violationsByFile.isEmpty() : "Expected violations for exactly one file, but found: "
                + violationsByFile.keySet();

        String originalFilePath;
        List<ViolationInfo> fileViolations;
        if (violationsByFile.size() == 1) {
            Map.Entry<String, List<ViolationInfo>> entry = violationsByFile.entrySet().iterator().next();
            originalFilePath = entry.getKey();
            fileViolations = entry.getValue();
        } else {
            // Handle empty case
            originalFilePath = originalFile.getAbsolutePath();
            fileViolations = new ArrayList<>();
        }

        // Sort violations by line number in descending order to remove from bottom to top
        fileViolations.sort((v1, v2) -> Integer.compare(v2.beginLine, v1.beginLine));

        // Read the original file
        Path originalPath = Path.of(originalFilePath);
        if (!Files.exists(originalPath)) {
            throw new IOException("File not found: " + originalFilePath);
        }

        List<String> lines = Files.readAllLines(originalPath);

        // Collect line numbers to remove (convert to set for efficient lookup)
        List<String> virtualFileLines = getStrings(fileViolations, lines);

        File tempFile = File.createTempFile("jplag_temp_", ".java");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), virtualFileLines);
        return tempFile;
    }

    private static @NotNull List<String> getStrings(@NotNull List<ViolationInfo> fileViolations, List<String> lines) {
        Set<Integer> linesToRemove = new HashSet<>();
        for (ViolationInfo violation : fileViolations) {
            for (int line = violation.beginLine; line <= violation.endLine; line++) {
                linesToRemove.add(line);
            }
        }
        // Create virtual file content with violations removed
        List<String> virtualFileLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1; // 1-based line number
            if (!linesToRemove.contains(lineNumber)) {
                virtualFileLines.add(lines.get(i));
            }
        }
        return virtualFileLines;
    }

}
