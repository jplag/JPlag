package de.jplag.options;

public enum Verbosity {
    QUIET,
    LONG;

    public static Verbosity fromOption(String optionValue) {
        switch (optionValue) {
            case "quiet":
                return QUIET;
            case "long":
                return LONG;
            default:
                return QUIET;
        }
    }
}
