package de.jplag.cpp2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;

/**
 * Tests asserting the extraction of nontrivial tokens. As the ANTLR grammar requires some workarounds to have the token
 * extraction similar to the Java language module, these tests covers the extraction of such tokens.
 */
class TokenExtractionTest {


}