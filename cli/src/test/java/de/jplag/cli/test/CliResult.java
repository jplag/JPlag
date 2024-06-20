package de.jplag.cli.test;

import de.jplag.options.JPlagOptions;

public record CliResult(JPlagOptions jPlagOptions, String targetPath) {
}
