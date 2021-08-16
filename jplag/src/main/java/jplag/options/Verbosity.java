package jplag.options;

public enum Verbosity {
    QUIET,
    LONG;

    public static Verbosity fromOption(String optionName) {
        switch (optionName) {
        case "quiet":
            return QUIET;
        case "long":
            return LONG;
        default:
            return QUIET;
        }
    }
}
