package de.jplag.cli.test;

import org.slf4j.event.Level;

import de.jplag.options.JPlagOptions;

public record CliResult(JPlagOptions jPlagOptions, String targetPath, Level logLevel) {
}
