package de.jplag.java.babylon;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;
import de.jplag.semantics.CodeSemantics;
import jdk.incubator.code.Op;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class ParserBabylon extends Parser {
    @Override
    protected JavacAdapter getJavacAdapter() {
        return new JavacAdapterBabylon();
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        lastLocation = new Op.Location(1, 1);
        deferredTokens.clear();
        List<Token> tokens = super.parse(files);
        drainDeferredTokens(lastLocation);
        return tokens;
    }

    private final List<DeferredToken> deferredTokens = new ArrayList<>();
    private Op.Location lastLocation;

    @Override
    public void add(Token token) {
        drainDeferredTokens(new Op.Location(token.getStartLine(), token.getStartColumn()));
        super.add(token);
    }

    private void drainDeferredTokens(@Nullable Op.Location nextLocation) {
        var copy = List.copyOf(deferredTokens);
        deferredTokens.clear();
        for (DeferredToken deferredToken : copy) {
            Op.Location startLocation = Objects.requireNonNullElse(deferredToken.location, lastLocation);
            Op.Location endLocation;
            int length;
            if (nextLocation == null || startLocation.line() != nextLocation.line() || startLocation.column() == nextLocation.column()) {
                endLocation = new Op.Location(startLocation.line(), startLocation.column() + 1);
                length = 1;
            } else {
                endLocation = new Op.Location(nextLocation.line(), nextLocation.column() - 1);
                length = endLocation.column() - startLocation.column();
            }
            super.add(new Token(
                    deferredToken.type, deferredToken.file,
                    startLocation.line(), startLocation.column(),
                    endLocation.line(), endLocation.column(),
                    length,
                    deferredToken.semantics
            ));
        }
    }

    public void add(TokenType type, File file, int startLine, int startColumn, int endLine, int endColumn, int length,
                    CodeSemantics semantics) {
        add(new Token(
                type, file,
                startLine, startColumn,
                endLine, endColumn,
                length,
                semantics
        ));
    }

    public void add(TokenType type, File file, @Nullable Op.Location location, CodeSemantics semantics) {
        if (location == null) {
            location = lastLocation;
        } else {
            drainDeferredTokens(location);
        }
        deferredTokens.add(new DeferredToken(type, file, semantics, location));
        lastLocation = location;
    }

    public void add(TokenType type, File file, CodeSemantics semantics) {
        add(type, file, null, semantics);
    }

    private record DeferredToken(
            TokenType type,
            File file,
            CodeSemantics semantics,
            @Nullable Op.Location location
    ) {}
}
