package de.jplag.cpp;

import java.io.IOException;
import java.io.InputStream;

/**
 * This stream adds a newline to the end of a file. This is a proxy.
 */
public class NewlineStream extends InputStream {
    private int endOfFile = 0;
    private final InputStream stream;

    public NewlineStream(InputStream stream) {
        super();
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        int result;
        switch (endOfFile) {
            case 0 -> {
                result = stream.read();
                if (result == -1) {
                    result = 13;
                    endOfFile = 1;
                }
            }
            case 1 -> {
                result = 10;
                endOfFile = 2;
            }
            default -> result = -1;
        }
        return result;
    }
}
