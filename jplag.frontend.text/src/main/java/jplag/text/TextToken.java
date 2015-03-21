package jplag.text;

//import java.util.*;
import java.io.IOException;

public class TextToken extends jplag.Token {
//    private Parser parser;

    public static int getSerial(String text, Parser parser) {
        text = text.toLowerCase();
        Integer obj = (Integer) parser.tokenStructure.table.get(text);
        if(obj == null) {
            obj = new Integer(parser.tokenStructure.serial);
            if(parser.tokenStructure.serial == Integer.MAX_VALUE)
                parser.outOfSerials();
            else
                parser.tokenStructure.serial++;
            parser.tokenStructure.table.put(text, obj);
            // System.out.println(text+" -> \t"+obj);
            if(parser.tokenStructure.reverseMapping != null)
                parser.tokenStructure.reverseMapping = null;
        }
        return obj.intValue();
    }

    // throw away this method soon:

    public static String type2string(int i, TokenStructure tokenStructure) {
        if(tokenStructure.reverseMapping == null)
            tokenStructure.createReverseMapping();
        return tokenStructure.reverseMapping[i];
    }

    // ///////////////////// END OF STATIC MEMBERS

    private int line, column, length;
    private String text;

    public TextToken(int type, String file, Parser parser) {
        super(type, file, -1, -1, -1);

//        this.parser = parser;
    }

    public TextToken(String text, String file, int line, int column,
            int length, Parser parser) {
        super(-1, file, line, column, length);
        this.type = getSerial(text, parser);
        this.text = text.toLowerCase();

        // System.out.println("Constructor called!\n");
//        this.parser = parser;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getText() {
        return this.text;
    }

    // private void writeObject(java.io.ObjectOutputStream out) throws
    // IOException {
    // out.defaultWriteObject();
    // out.writeObject(null);
    // }

/*    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        if(text != null)
            this.parser.tokenStructure.table.put(text, new Integer(type));
    }*/

    public static int numberOfTokens(TokenStructure tokenStructure) {
        return tokenStructure.table.size();
    }
}
