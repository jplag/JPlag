package jplag.options;

public enum Verbosity { // TODO TS: These levels are not used consistently.
    PARSER,
    QUIET,
    LONG;

    public static Verbosity fromOption(String optionName) {
        switch (optionName) {
        case "parser":
            return PARSER;
        case "quiet":
            return QUIET;
        case "long":
            return LONG;
        default:
            return QUIET;
        }
    }
}
