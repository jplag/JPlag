package de.jplag.cli.test;

import org.slf4j.event.Level;

import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.options.JPlagOptions;

/**
 * Holds the result of CLI parsing and setup.
 * @param jPlagOptions parsed JPlag options
 * @param targetPath output directory or file path
 * @param logLevel selected logging level
 * @param inputHandler handler for user input during execution
 */
public record CliResult(JPlagOptions jPlagOptions, String targetPath, Level logLevel, CliInputHandler inputHandler) {
}
