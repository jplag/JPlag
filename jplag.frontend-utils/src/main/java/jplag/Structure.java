package jplag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** The tokenlist */ // TODO PB: The name 'Structure' is very generic and should be changed to something more descriptive.
public class Structure implements TokenConstants {
    public Token[] tokens = new Token[0];
    Table table = null;
    int hash_length = -1;

    int files; // number of END_FILE tokens
    private int numberOfTokens;

    public Structure() {
        tokens = new Token[400];
        files = numberOfTokens = 0;
    }

    public final int size() {
        return numberOfTokens;
    }

    public final void ensureCapacity(int minCapacity) {
        int oldCapacity = tokens.length;
        if (minCapacity > oldCapacity) {
            Token[] oldTokens = tokens;
            int newCapacity = (2 * oldCapacity);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            tokens = new Token[newCapacity];
            System.arraycopy(oldTokens, 0, tokens, 0, numberOfTokens);
        }
    }

    public final void addToken(Token token) {
        ensureCapacity(numberOfTokens + 1);
        if (numberOfTokens > 0 && tokens[numberOfTokens - 1].file.equals(token.file))
            token.file = tokens[numberOfTokens - 1].file; // To save memory ...
        if ((numberOfTokens > 0) && (token.getLine() < tokens[numberOfTokens - 1].getLine()) && (token.file.equals(tokens[numberOfTokens - 1].file)))
            token.setLine(tokens[numberOfTokens - 1].getLine());
        // just to make sure

        tokens[numberOfTokens++] = token;
        if (token.type == FILE_END)
            files++;
    }

    @Override
    public final String toString() {
        StringBuffer buffer = new StringBuffer();

        try {
            for (int i = 0; i < numberOfTokens; i++) {
                buffer.append(i);
                buffer.append("\t");
                buffer.append(tokens[i].toString());
                if (i < numberOfTokens - 1) {
                    buffer.append("\n");
                }
            }
        } catch (OutOfMemoryError e) {
            return "Tokenlist to large for output: " + (numberOfTokens) + " Tokens";
        }
        return buffer.toString();
    }

    public void save(File file) {

        try {
            ObjectOutputStream input = new ObjectOutputStream((new FileOutputStream(file)));

            input.writeInt(numberOfTokens);
            input.writeInt(hash_length);
            input.writeInt(files);

            for (int i = 0; i < numberOfTokens; i++)
                input.writeObject(tokens[i]);
            input.flush();
            input.close();
        } catch (IOException e) {
            System.out.println("Error writing file: " + file.toString());
        }
    }

    /* returns "true" when successful */
    public boolean load(File file) {
        try {
            ObjectInputStream input = new ObjectInputStream((new FileInputStream(file)));

            int newNumberOfTokens = input.readInt();
            hash_length = input.readInt();
            files = input.readInt();
            ensureCapacity(newNumberOfTokens);
            numberOfTokens = newNumberOfTokens;
            for (int i = 0; i < numberOfTokens; i++) {
                tokens[i] = (Token) input.readObject();
            }
            input.close();
            table = null;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.toString());
            return false;
        } catch (IOException e) {
            System.out.println("Error reading file: " + file.toString() + " (" + e + ")");
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found in file: " + file.toString());
            return false;
        }
        return true;
    }
}
