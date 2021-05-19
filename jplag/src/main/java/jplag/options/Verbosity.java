package jplag.options;

public enum Verbosity {
    PARSER,
    QUIET,
    LONG,
    DETAILS;

    public static Verbosity fromOption(String optionName) {
        switch (optionName) {
        case "parser":
            return PARSER;
        case "quiet":
            return QUIET;
        case "long":
            return LONG;
        case "details":
            return DETAILS;
        default:
            return QUIET;
        }
    }
}
