package jplag.ipython;

import jplag.Token;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IPythonToken extends jplag.Token implements IPythonTokenConstants {
    private static final long serialVersionUID = -383581430479870696L;
    private static int nextOffset = IPythonTokenConstants.FILE_END + 1;
    private static Map<String, Integer> langOffsetMap = new Hashtable<>();
    private int line, column, length;
    private static int nextStaticOffset = Integer.MAX_VALUE / 2;
    private static Map<Integer, Integer> stringOffsetMap = new Hashtable<>();

    public IPythonToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @NotNull
    public static IPythonToken createToken(String content, String file, int line) {
        int hash = content.hashCode();
        if (!stringOffsetMap.containsKey(hash)) {
            stringOffsetMap.put(hash, nextStaticOffset++);
        }
        return new IPythonToken(stringOffsetMap.get(hash),
                file,
                line,
                0,
                0);
    }

    @NotNull
    public static IPythonToken createToken(jplag.Token tok, String language, int lineOffset) {
        if (!langOffsetMap.containsKey(language)) {
            langOffsetMap.put(language, nextOffset);
            nextOffset += tok.numberOfTokens();
        }

        return new IPythonToken(
                tok.type + langOffsetMap.get(language),
                tok.file,
                tok.getLine() - 1 + lineOffset,
                tok.getColumn(),
                tok.getLength());
    }

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

    static public int getNumberOfTokens() {
        return IPythonToken.nextOffset;
    }

    static public String type2string(int type) {
        if (type >= Integer.MAX_VALUE / 2) {
            return "STRING: " + type;
        }
        ArrayList<Map.Entry<String, Integer>> arr = new ArrayList<>(
                IPythonToken.langOffsetMap.entrySet()
        );
        Collections.sort(arr, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> t1, Map.Entry<String, Integer> t2) {
                return t1.getValue() - t2.getValue();
            }
        });

        Map.Entry<String, Integer> last = null;
        for (Map.Entry<String, Integer> entry : arr) {
            if (entry.getValue() >= type) {
                if (last == null) {
                    return "FILE_END";
                }
                return last.getKey() + " " + type;
            }
            last = entry;
        }
        return last == null ? "UNKNOWN" : last.getKey() + " " + type;
    }

    @Override
    public int numberOfTokens() {
        return Integer.MAX_VALUE;
    }
}