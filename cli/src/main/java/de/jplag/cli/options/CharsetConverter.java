package de.jplag.cli.options;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import de.jplag.cli.CliException;

import picocli.CommandLine;

/**
 * Converts the string from the cli to a charset.
 */
public class CharsetConverter implements CommandLine.ITypeConverter<Charset> {
    @Override
    public Charset convert(String value) throws Exception {
        try {
            return Charset.forName(value);
        } catch (UnsupportedCharsetException | IllegalCharsetNameException e) {
            throw new CliException("Invalid charset name: " + value);
        }
    }
}
