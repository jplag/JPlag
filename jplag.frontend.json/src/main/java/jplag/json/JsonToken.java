package jplag.json;

public class JsonToken extends jplag.Token implements JsonTokenConstants {
    private int line, column, length;
    public static TokenStructure tokenStructure = new TokenStructure();

    private static int getSerial(String text, Parser parser) {
        text = text.toLowerCase();
        Integer obj = tokenStructure.table.get(text);
        if(obj == null) {
            obj = tokenStructure.serial;
            if(tokenStructure.serial == Integer.MAX_VALUE)
                parser.outOfSerials();
            else
                tokenStructure.serial++;
            tokenStructure.table.put(text, obj);
            if(tokenStructure.reverseMapping != null) {
                tokenStructure.reverseMapping = null;
            }
        }
        return obj;
    }

    public JsonToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    public JsonToken(String text, String file, int line, int column,
                     int length, Parser parser) {
        super(-1, file, line, column, length);
        this.type = getSerial(text, parser);
    }

    @Override
    public int numberOfTokens() {
        return tokenStructure.table.size();
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

    public static String type2string(int type) {
        switch (type) {
            case JsonTokenConstants.FILE_END:
                return "********";
            case JsonTokenConstants.TRUE:
                return "TRUE";
            case JsonTokenConstants.FALSE:
                return "FALSE";
            case JsonTokenConstants.NULL:
                return "NULL";
            case JsonTokenConstants.ARRAY_START:
                return "ARRAY_START";
            case JsonTokenConstants.ARRAY_END:
                return "ARRAY_END";
            case JsonTokenConstants.OBJECT_START:
                return "OBJECT_START";
            case JsonTokenConstants.OBJECT_END:
                return "OBJECT_END";
            case JsonTokenConstants.NUMBER:
                return "NUMBER";
            default:
                return "*DYNAMIC: " + type;
        }
    }
}
