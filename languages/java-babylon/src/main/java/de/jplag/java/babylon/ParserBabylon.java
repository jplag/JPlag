package de.jplag.java.babylon;

import de.jplag.java.JavacAdapter;
import de.jplag.java.Parser;

public class ParserBabylon extends Parser {
    @Override
    protected JavacAdapter getJavacAdapter() {
        return new JavacAdapterBabylon();
    }
}
