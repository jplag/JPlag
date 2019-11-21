package jplag;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

abstract public class StreamParser extends Parser {
    public Structure struct = new Structure();

    @NotNull
    public abstract Token getEndOfFileToken(String file);

    public jplag.Structure parse(File dir, String[] files) {
        struct = new Structure();
        errors = 0;
        for (String file : files) {
            getProgram().print(null, "Parsing file " + file + "\n");
            if (!this.parseFile(dir, file)) {
                errors++;
            }
            System.gc();//Emeric
            struct.addToken(this.getEndOfFileToken(file));
        }
        this.parseEnd();
        return struct;
    }

    public boolean parseFile(File dir, String file) {
        BufferedInputStream fis = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(new File(dir, file)));

            this.parseStream(fis, new TokenAdder(file) {
                @Nullable
                @Override
                public Token getLast() {
                    return struct.tokens[struct.size() - 1];
                }

                @Override
                public void addToken(@NotNull Token token) {
                    struct.addToken(token);
                }
            });
        } catch (Exception e) {
            getProgram().addError("Parsing Error in '" + file + "':\n" + e.getMessage() + "\n");
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public abstract boolean parseStream(@NotNull InputStream stream, @NotNull final TokenAdder adder) throws IOException;
}
