package de.jplag.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import de.jplag.options.JPlagOptions;

public class HTMLFile extends PrintWriter {

    private BufferedCounter counter;

    /**
     * Static factory method to instantiate an HTMLFile objects.
     */
    public static HTMLFile fromFile(File file) throws IOException {
        BufferedCounter counter = new BufferedCounter(new OutputStreamWriter(new FileOutputStream(file), JPlagOptions.CHARSET));
        return new HTMLFile(counter);
    }

    private HTMLFile(BufferedCounter counter) {
        super(counter);
        this.counter = counter;
    }

    public int bytesWritten() {
        return counter.bytesWritten();
    }
}
