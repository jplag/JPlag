package jplag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//import java.util.zip.*;

/** The tokenlist */
public class Structure implements TokenConstants {
	public Token[] tokens = new Token[0];
	Table table = null;
	int hash_length = -1;

	int files; // number of END_FILE tokens
	private int anzahl;

	//private final int increment = 300;

	public Structure() {
		tokens = new Token[400];
		files = anzahl = 0;
	}

	public final int size() {
		return anzahl;
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
			System.arraycopy(oldTokens, 0, tokens, 0, anzahl);
		}
	}

	public final void addToken(Token token) {
		ensureCapacity(anzahl + 1);
		if (anzahl > 0 && tokens[anzahl - 1].file.equals(token.file))
			token.file = tokens[anzahl - 1].file; // To save memory ...
		if ((anzahl > 0) && (token.getLine() < tokens[anzahl - 1].getLine()) && (token.file.equals(tokens[anzahl - 1].file)))
			token.setLine(tokens[anzahl - 1].getLine());
		// just to make sure

		tokens[anzahl++] = token;
		if (token.type == FILE_END)
			files++;
	}

	public final String toString() {
		StringBuffer buf = new StringBuffer();

		try {
			for (int i = 0; i < anzahl; i++) {
				String s = tokens[i].toString();
				buf.append(i);
				buf.append("\t");
				buf.append(s);
				if (i < anzahl - 1) {
					buf.append("\n");
				}
			}
		} catch (OutOfMemoryError e) {
			return "Tokenlist to large for output: " + (anzahl) + " Tokens";
		}
		return buf.toString();
	}

	public void save(File file) {

		try {
			ObjectOutputStream p = new ObjectOutputStream(/* new GZIPOutputStream */(new FileOutputStream(file)));

			p.writeInt(anzahl);
			p.writeInt(hash_length);
			p.writeInt(files);

			for (int i = 0; i < anzahl; i++)
				p.writeObject(tokens[i]);
			p.flush();
			p.close();
		} catch (IOException e) {
			System.out.println("Error writing file: " + file.toString());
		}
	}

	/* returns "true" when successful */
	public boolean load(File file) {
		try {
			ObjectInputStream p = new ObjectInputStream(/* new GZIPInputStream */(new FileInputStream(file)));

			int newAnzahl = p.readInt();
			hash_length = p.readInt();
			files = p.readInt();
			ensureCapacity(newAnzahl);
			anzahl = newAnzahl;
			for (int i = 0; i < anzahl; i++) {
				tokens[i] = (Token) p.readObject();
				// special case for text tokens:
				// 	if (tokens[i] instanceof jplag.text.TextToken) {
				// 	  jplag.text.TextToken token = (jplag.text.TextToken)tokens[i];
				// 	  jplag.text.TextToken.put(token.getText(), token.type);
				// 	}
			}
			p.close();
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
