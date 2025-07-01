package de.jplag.cli.options;

import java.nio.charset.Charset;

import picocli.CommandLine;

public class CharsetConverter implements CommandLine.ITypeConverter<Charset> {
    @Override
    public Charset convert(String value) throws Exception {
        return Charset.forName(value);
    }
}
