package jplag.reporting;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/*
 * This class counts the number of printed characters.
 */
public class BufferedCounter extends BufferedWriter {

    private int count;

    public BufferedCounter(Writer out) {
        super(out);
        count = 0;
    }

    public BufferedCounter(Writer out, int sz) {
        super(out, sz);
        count = 0;
    }

    @Override
    public void write(int c) throws IOException {
        super.write(c);
        count++;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        super.write(cbuf, off, len);
        count += len;
    }

    @Override
    public void write(String s, int off, int len) throws IOException {
        super.write(s, off, len);
        count += len;
    }

    @Override
    public void newLine() throws IOException {
        super.newLine();
        count++;
    }

    public int bytesWritten() {
        return count;
    }
}
