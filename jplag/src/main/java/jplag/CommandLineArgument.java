package jplag;

import static jplag.strategy.ComparisonMode.NORMAL;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jplag.options.LanguageOption;
import jplag.strategy.ComparisonMode;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;

/**
 * Command line arguments for the JPlag CLI. Each argument is defined through an enuemral.
 * @author Timur Saglam
 */
public enum CommandLineArgument {
    ROOT_DIRECTORY("rootDir", "The root-directory that contains all submissions"),
    LANGUAGE("-l", "Select the language to parse the submissions", LanguageOption.getDefault().getDisplayName(), LanguageOption.getAllDisplayNames()),
    BASE_CODE("-bc", "Name of the directory which contains the base code (common framework used in all submissions)"),
    VERBOSITY("-v", "Verbosity", "quiet", List.of("parser", "quiet", "long", "details")), // TODO SH: Replace verbosity when integrating a real logging library
    DEBUG("-d", "(Debug) parser. Non-parsable files will be stored", storeTrue()),
    SUBDIRECTORY("-S", "Look in directories <root-dir>/*/<dir> for programs"),
    SUFFIXES("-p", "comma-separated list of all filename suffixes that are included"),
    EXCLUDE_FILE("-x", "All files named in this file will be ignored in the comparison (line-separated list)"),
    MIN_TOKEN_MATCH("-t", "Tune the sensitivity of the comparison. A smaller <n> increases the sensitivity"),
    SIMILARITY_THRESHOLD("-m", "Match similarity threshold [0-100]: All matches above this threshold will be saved", 0f), // TODO TS deduplicate default values
    STORED_MATCHES("-n", "Maximum number of matches that will be saved. If set to -1 all matches will be saved", 30), 
    RESULT_FOLDER("-r", "Name of directory in which the comparison results will be stored", "result"),
    COMPARISON_MODE("-c", "Comparison mode used to compare the programs", NORMAL.getName(), ComparisonMode.allNames());

    private final String flag;
    private final String helptext;
    private final Optional<Object> defaultValue;
    private final Optional<Collection<String>> choices;
    private final Optional<ArgumentAction> action;

    private CommandLineArgument(String flag, String helpText, Optional<Object> defaultValue, Optional<Collection<String>> choices,
            Optional<ArgumentAction> action) {
        this.flag = flag;
        this.helptext = helpText;
        this.defaultValue = defaultValue;
        this.choices = choices;
        this.action = action;
    }

    private CommandLineArgument(String flag, String helpText, Object defaultValue, Collection<String> choices) {
        this(flag, helpText, Optional.of(defaultValue), Optional.of(choices), Optional.empty());
    }

    private CommandLineArgument(String flag, String helpText, Object defaultValue) {
        this(flag, helpText, Optional.of(defaultValue), Optional.empty(), Optional.empty());
    }

    private CommandLineArgument(String flag, String helpText) {
        this(flag, helpText, Optional.empty(), Optional.empty(), Optional.empty());
    }

    private CommandLineArgument(String flag, String helpText, ArgumentAction action) {
        this(flag, helpText, Optional.empty(), Optional.empty(), Optional.of(action));
    }

    /**
     * Parses the command line argument with a specific parser.
     * @param parser is that parser.
     */
    public void parseWith(ArgumentParser parser) {
        Argument argument = parser.addArgument(flag).help(helptext);
        choices.ifPresent(it -> argument.choices(it));
        defaultValue.ifPresent(it -> argument.setDefault(it));
        action.ifPresent(it -> argument.action(it));
    }

    /**
     * @return the flag name of the command line argument without leading dashes.
     */
    public String flag() {
        return flag.replace("-", "");
    }
}
