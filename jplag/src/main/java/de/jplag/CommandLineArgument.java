package de.jplag;

import static de.jplag.options.JPlagOptions.DEFAULT_COMPARISON_MODE;
import static de.jplag.options.JPlagOptions.DEFAULT_SIMILARITY_THRESHOLD;
import static de.jplag.options.JPlagOptions.DEFAULT_SHOWN_COMPARISONS;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.jplag.options.LanguageOption;
import de.jplag.strategy.ComparisonMode;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command line arguments for the JPlag CLI. Each argument is defined through an enumeral.
 * @author Timur Saglam
 */
public enum CommandLineArgument {
    ROOT_DIRECTORY("rootDir", String.class, "The root-directory that contains all submissions"),
    LANGUAGE("-l", String.class, "Select the language to parse the submissions", LanguageOption.getDefault().getDisplayName(), LanguageOption.getAllDisplayNames()),
    BASE_CODE("-bc", String.class, "Name of the subdirectory of the root directory which contains the base code (common framework used in all submissions)"),
    VERBOSITY("-v", String.class, "Verbosity", "quiet", List.of("parser", "quiet", "long", "details")), // TODO SH: Replace verbosity when integrating a real logging library
    DEBUG("-d", Boolean.class, "(Debug) parser. Non-parsable files will be stored"),
    SUBDIRECTORY("-S", String.class, "Look in directories <root-dir>/*/<dir> for programs"),
    SUFFIXES("-p", String.class, "comma-separated list of all filename suffixes that are included"),
    EXCLUDE_FILE("-x", String.class, "All files named in this file will be ignored in the comparison (line-separated list)"),
    MIN_TOKEN_MATCH("-t", Integer.class, "Tunes the comparison sensitivity by adjusting the minimum token required to be counted as matching section. A smaller <n> increases the sensitivity but might lead to more false-positves"),
    SIMILARITY_THRESHOLD("-m", Float.class, "Match similarity threshold [0-100]: All matches above this threshold will be saved", DEFAULT_SIMILARITY_THRESHOLD),
    SHOWN_COMPARISONS("-n", Integer.class, "The maximum number of comparisons that will be shown in the generated report, if set to -1 all comparisons will be shown", DEFAULT_SHOWN_COMPARISONS),
    RESULT_FOLDER("-r", String.class, "Name of directory in which the comparison results will be stored", "result"),
    COMPARISON_MODE("-c", String.class, "Comparison mode used to compare the programs", DEFAULT_COMPARISON_MODE.getName(), ComparisonMode.allNames());

    private final String flag;
    private final String helptext;
    private final Optional<Object> defaultValue;
    private final Optional<Collection<String>> choices;
    private final Class<?> type;

    private CommandLineArgument(String flag, Class<?> type, String helpText) {
        this(flag, type, helpText, Optional.empty(), Optional.empty());
    }

    private CommandLineArgument(String flag, Class<?> type, String helpText, Object defaultValue) {
        this(flag, type, helpText, Optional.of(defaultValue), Optional.empty());
    }

    private CommandLineArgument(String flag, Class<?> type, String helpText, Object defaultValue, Collection<String> choices) {
        this(flag, type, helpText, Optional.of(defaultValue), Optional.of(choices));
    }

    private CommandLineArgument(String flag, Class<?> type, String helpText, Optional<Object> defaultValue, Optional<Collection<String>> choices) {
        this.flag = flag;
        this.helptext = helpText;
        this.type = type;
        this.defaultValue = defaultValue;
        this.choices = choices;
    }
    
    /**
     * @return the flag name of the command line argument.
     */
    public String flag() {
        return flag;
    }

    /**
     * @return the flag name of the command line argument without leading dashes.
     */
    public String flagWithoutDash() {
        return flag.replace("-", "");
    }

    /**
     * Returns the value of this argument. Convenience method for {@link Namespace#get(String)} and
     * {@link CommandLineArgument#flagWithoutDash()}.
     * @param <T> is the argument type.
     * @param namespace stores a value for the argument.
     * @return the argument value.
     */
    public <T> T getFrom(Namespace namespace) {
        return namespace.get(flagWithoutDash());
    }

    /**
     * Parses the command line argument with a specific parser.
     * @param parser is that parser.
     */
    public void parseWith(ArgumentParser parser) {
        Argument argument = parser.addArgument(flag).help(helptext);
        choices.ifPresent(it -> argument.choices(it));
        defaultValue.ifPresent(it -> argument.setDefault(it));
        argument.type(type);
        if (type == Boolean.class) {
            argument.action(storeTrue());
        }
    }
}
