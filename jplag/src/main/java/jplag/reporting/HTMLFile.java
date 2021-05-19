package jplag.reporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class HTMLFile extends PrintWriter {

    private BufferedCounter bc;

    /**
     * Static factory method to instantiate an HTMLFile objects.
     */
    public static HTMLFile fromFile(File file) throws IOException {
        BufferedCounter bc = new BufferedCounter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

        HTMLFile htmlFile = new HTMLFile(bc);
        htmlFile.bc = bc;

        return htmlFile;
    }

    private HTMLFile(BufferedWriter writer) {
        super(writer);
    }

    public int bytesWritten() {
        return bc.bytesWritten();
    }
}
