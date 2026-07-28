package de.jplag.java;

import javax.tools.SimpleJavaFileObject;

import java.io.IOException;
import java.net.URI;

public class StringSourceFile extends  SimpleJavaFileObject {
    private String source;
    private String name;

    public StringSourceFile(String name, String source) { //TODO pass actual path
        super(URI.create("string:///" + name), Kind.SOURCE);

        this.name = name;
        this.source = source;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return source;
    }
}
