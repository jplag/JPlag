package de.jplag.csharp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Generic unicode text reader, which will use BOM mark to identify the encoding to be used. If BOM is not found then
 * use a given default encoding. UTF-8 is used if: BOM mark is not found and defaultEnc is NULL. Usage pattern:
 *
 * <pre>
 * String defaultEnc = "UTF-16BE"; // or NULL to use system default
 * FileInputStream fis = new FileInputStream(file);
 * Reader in = new UnicodeReader(fis, defaultEnc);
 * 
 * Original pseudocode   : Thomas Weidenfeller
 * Implementation tweaked: Aki Nieminen
 *
 * http://www.unicode.org/unicode/faq/utf_bom.html
 * BOMs:
 * 00 00 FE FF    = UTF-32, big-endian
 * FF FE 00 00    = UTF-32, little-endian
 * FE FF          = UTF-16, big-endian
 * FF FE          = UTF-16, little-endian
 * EF BB BF       = UTF-8
 *
 * Win2k Notepad:
 * Unicode format = UTF-16LE
 * </pre>
 */
public class UnicodeReader extends Reader {
    private PushbackInputStream internalIn;

    private InputStreamReader internalIn2 = null;

    private Charset defaultEnc;

    private static final int BOM_SIZE = 4;

    public UnicodeReader(InputStream in, Charset defaultEnc) {
        internalIn = new PushbackInputStream(in, BOM_SIZE);
        this.defaultEnc = defaultEnc;
    }

    public Charset getDefaultEncoding() {
        return defaultEnc;
    }

    public String getEncoding() {
        if (internalIn2 == null)
            return null;
        return internalIn2.getEncoding();
    }

    /**
     * Read-ahead four bytes and check for BOM marks. Extra bytes are unread back to the stream, only BOM bytes are skipped.
     */
    private void init() throws IOException {
        if (internalIn2 != null)
            return;

        Charset encoding;
        byte bom[] = new byte[BOM_SIZE];
        int n, unread;
        n = internalIn.read(bom, 0, bom.length);

        if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
            encoding = StandardCharsets.UTF_8;
            unread = n - 3;
        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
            encoding = StandardCharsets.UTF_16BE;
            unread = n - 2;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
            encoding = StandardCharsets.UTF_16LE;
            unread = n - 2;
        } else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
            encoding = Charset.forName("UTF-32BE");
            unread = n - 4;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
            encoding = Charset.forName("UTF-32LE");
            unread = n - 4;
        } else {
            // Unicode BOM mark not found, unread all bytes
            encoding = defaultEnc;
            unread = n;
        }

        if (unread > 0)
            internalIn.unread(bom, (n - unread), unread);

        // Use given encoding
        if (encoding == null) {
            internalIn2 = new InputStreamReader(internalIn, StandardCharsets.UTF_8);
        } else {
            internalIn2 = new InputStreamReader(internalIn, encoding);
        }
    }

    @Override
    public void close() throws IOException {
        init();
        internalIn2.close();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        init();
        return internalIn2.read(cbuf, off, len);
    }
}