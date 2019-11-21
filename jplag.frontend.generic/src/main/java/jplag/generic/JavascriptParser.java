package jplag.generic;

public class JavascriptParser extends GenericParser {
    public static String command = "javascript_to_jplag_parser";

    @Override
    protected GenericToken makeToken(int type, String file, int line, int column, int length) {
        return new JavascriptToken(type, file, line, column, length);
    }

    @Override
    protected String getCommandLineProgram() {
        return command;
    }
}
