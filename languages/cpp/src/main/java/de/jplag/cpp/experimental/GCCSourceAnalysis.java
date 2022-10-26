package de.jplag.cpp.experimental;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses GCC to find unused variables and saves their location. The scanner can then check if a token belongs to an
 * unused variable
 */
public class GCCSourceAnalysis implements SourceAnalysis {

    public static final String COMPILE_COMMAND = "gcc -Wall -fsyntax-only %s";
    private Map<String, List<Integer>> linesToDelete = new HashMap<>();

    private final Logger logger;

    public GCCSourceAnalysis() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public boolean isTokenIgnored(de.jplag.cpp.Token token, File file) {
        String fileName = file.getName();
        if (linesToDelete.containsKey(fileName)) {
            var ignoredLineNumbers = linesToDelete.get(fileName);
            return ignoredLineNumbers.contains(token.beginLine);
        }
        return false;
    }

    public void findUnusedVariableLines(Set<File> files) throws InterruptedException {
        linesToDelete = new HashMap<>();

        for (File file : files) {
            try {
                Runtime runtime = Runtime.getRuntime();
                Process gcc = runtime.exec(COMPILE_COMMAND.formatted(file.getAbsolutePath()));
                gcc.waitFor();

                // gcc prints compiler warnings to the error stream, not the standard stream
                BufferedReader stdError = new BufferedReader(new InputStreamReader(gcc.getErrorStream()));

                String line;
                while ((line = stdError.readLine()) != null) {
                    processOutputLine(line);
                }
            } catch (IOException e) {
                // error during compilation, skip this submission
                logger.warn("Failed to compile file {}", file.getAbsolutePath());
            }
        }
    }

    private void processOutputLine(String line) {
        // example output:
        // sourceFile.c:151:8: warning: unused variable 't' [-Wunused-variable]
        if (!line.contains("unused variable")) {
            return;
        }

        // contains [sourceFile, line, column, (warning|error), description]
        var lineSplit = line.split(":");

        String fileName = new File(lineSplit[0]).getName();

        int lineNumber = Integer.parseInt(lineSplit[1]);

        linesToDelete.computeIfAbsent(fileName, key -> new ArrayList<>()).add(lineNumber);
    }
}
