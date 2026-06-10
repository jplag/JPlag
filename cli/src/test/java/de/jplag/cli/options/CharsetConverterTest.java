package de.jplag.cli.options;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.jplag.cli.CliException;

class CharsetConverterTest {
    private CharsetConverter converter = new CharsetConverter();

    @Test
    void testUtf8() throws Exception {
        assertNotNull(converter.convert("utf-8"));
    }

    @Test
    void testASCII() throws Exception {
        assertNotNull(converter.convert("ascii"));
    }

    @Test
    void testExceptionOnInvalid() {
        assertThrows(CliException.class, () -> {
            converter.convert("utf-9");
        });
    }
}