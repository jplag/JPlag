package jplag.generic;

public class PhpParser extends GenericParser {
    static public String command = "php_parser_to_jplag";

    @Override
    protected GenericToken makeToken(int type, String file, int line, int column, int length) {
        return new PhpToken(type, file, line, column, length);
    }

    @Override
    protected String getCommandLineProgram() {
        return command;
    }
}