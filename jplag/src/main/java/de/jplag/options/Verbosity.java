package de.jplag.options;

public enum Verbosity {
    QUIET,
    LONG;

    public static Verbosity fromOption(String optionValue) {
        for (Verbosity verbosity : Verbosity.values())
            if (verbosity.name().equals(optionValue))
                return verbosity;
        return QUIET;
    }
}
