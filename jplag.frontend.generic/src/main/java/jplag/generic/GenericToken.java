package jplag.generic;

import org.json.JSONObject;

public abstract class GenericToken extends jplag.Token implements GenericTokenConstants {
    private static final long serialVersionUID = -383581430479870696L;
    static Integer amountOfTokens = null;
    private int line, column, length;
    static JSONObject nameMapping = null;

    public GenericToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

//    abstract protected String getCommand();
//
//    public static String type2string(int type) {
//        try {
//            if (nameMapping == null) {
//                Tuple3<Integer, String, String> res = CommandExecutor.execute(getCommand(), "", "MAPPING");
//                nameMapping = new JSONObject(new JSONTokener(res.getB()));
//            }
//            return nameMapping.getString(String.valueOf(type));
//        } catch (Exception e) {
//            return (Integer.valueOf(type)).toString();
//
//        }
//    }
//
//    public static int numberOfTokens() {
//        if (amountOfTokens == null) {
//            try {
//                Tuple3<Integer, String, String> res = CommandExecutor.execute(getCommand(), "", "AMOUNT");
//                amountOfTokens = Integer.parseInt(res.getB(), 10);
//            } catch (Exception e) {
//                return 1000;  // Simply do something
//            }
//        }
//        return amountOfTokens;
//    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}