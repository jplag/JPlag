package jplag;

/** The tokenlist */ // TODO PB: The name 'Structure' is very generic and should be changed to something more descriptive.
public class Structure implements TokenConstants { // TODO TS: How about TokenList?
    public Token[] tokens = new Token[0]; // TODO TS: An array list would allow us to remove the code in ensureCapacity()
    Table table = null;
    int hash_length = -1;

    private int numberOfTokens;

    public Structure() {
        tokens = new Token[400];
        numberOfTokens = 0;
    }

    public final int size() {
        return numberOfTokens;
    }

   private final void ensureCapacity(int minCapacity) {
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
}
