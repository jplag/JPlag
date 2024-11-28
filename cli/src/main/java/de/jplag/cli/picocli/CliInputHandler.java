package de.jplag.cli.picocli;

import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST_HEADING;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_DESCRIPTION_HEADING;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_OPTION_LIST;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_SYNOPSIS;

import java.io.File;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.cli.CliException;
import de.jplag.cli.options.CliOptions;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

/**
 * Handles the parsing of the command line arguments.
 */
public class CliInputHandler {
    private static final String OPTION_LIST_HEADING = "Parameter descriptions: ";

    private static final String UNKNOWN_LANGUAGE_EXCEPTION = "Language %s does not exists. Available languages are: %s";
    private static final String IMPOSSIBLE_EXCEPTION = "This should not have happened."
            + " Please create an issue on github (https://github.com/jplag/JPlag/issues) with the entire output.";

    private static final String[] DESCRIPTIONS = {"Detecting Software Plagiarism", "Software-Archaeological Playground", "Since 1996",
            "Scientifically Published", "Maintained by SDQ", "RIP Structure and Table", "What else?", "You have been warned!", "Since Java 1.0",
            "More Abstract than Tree", "Students Nightmare", "No, changing variable names does not work...", "The tech is out there!",
            "Developed by plagiarism experts.", "State of the Art Obfuscation Resilience", "www.helmholtz.software/software/jplag"};
    private static final String DESCRIPTION_PATTERN = "%nJPlag - %s%n%s%n%n";
    private static final String CREDITS = "Created by IPD Tichy, Guido Malpohl, and others. Maintained by Timur Saglam and Sebastian Hahner. Logo by Sandro Koch.";

    private static final String PARAMETER_SHORT_PREFIX = "  -";
    private static final String PARAMETER_SHORT_ADDITIONAL_INDENT = "    ";

    private static final Random RANDOM = new SecureRandom();

    private final String[] args;
    private final CliOptions options;
    private final CommandLine commandLine;

    private ParseResult parseResult;

    /**
     * Creates a new handler. Before using it you need to call {@link #parse()}
     * @param args The arguments.
     */
    public CliInputHandler(String[] args) {
        this.args = args;
        this.options = new CliOptions();
        this.commandLine = buildCommandLine();
    }

    private CommandLine buildCommandLine() {
        CommandLine cli = new CommandLine(this.options).setCaseInsensitiveEnumValuesAllowed(true);
        cli.setHelpFactory(new HelpFactory());

        cli.getHelpSectionMap().put(SECTION_KEY_OPTION_LIST, help -> help.optionList().lines().map(it -> {
            if (it.startsWith(PARAMETER_SHORT_PREFIX)) {
                return PARAMETER_SHORT_ADDITIONAL_INDENT + it;
            }
            return it;
        }).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator());
        cli.getHelpSectionMap().put(SECTION_KEY_COMMAND_LIST_HEADING, help -> "Languages:" + System.lineSeparator());

        buildSubcommands().forEach(cli::addSubcommand);

        cli.getHelpSectionMap().put(SECTION_KEY_SYNOPSIS, help -> help.synopsis(help.synopsisHeadingLength()) + generateDescription());
        cli.getHelpSectionMap().put(SECTION_KEY_DESCRIPTION_HEADING, help -> OPTION_LIST_HEADING);
        cli.setAllowSubcommandsAsOptionParameters(true);

        return cli;
    }

    private List<CommandLine.Model.CommandSpec> buildSubcommands() {
        return LanguageLoader.getAllAvailableLanguages().values().stream().map(language -> {
            CommandLine.Model.CommandSpec command = CommandLine.Model.CommandSpec.create().name(language.getIdentifier());

            for (LanguageOption<?> option : language.getOptions().getOptionsAsList()) {
                command.addOption(CommandLine.Model.OptionSpec.builder(option.getNameAsUnixParameter()).type(option.getType().getJavaType())
                        .description(option.getDescription()).build());
            }
            command.mixinStandardHelpOptions(true);
            command.addPositional(
                    CommandLine.Model.PositionalParamSpec.builder().type(List.class).auxiliaryTypes(File.class).hidden(true).required(false).build());

            return command;
        }).toList();
    }

    /**
     * Parses the cli parameters and prints the usage help if requested.
     * @return true, if the usage help has been requested. In this case the program should stop.
     * @throws CliException If something went wrong during parsing.
     */
    public boolean parse() throws CliException {
        try {
            this.parseResult = this.commandLine.parseArgs(args);
            if (this.parseResult.isUsageHelpRequested()
                    || (this.parseResult.subcommand() != null && this.parseResult.subcommand().isUsageHelpRequested())) {
                commandLine.getExecutionStrategy().execute(this.parseResult);
                return true;
            }
        } catch (CommandLine.ParameterException e) {
            if (e.getArgSpec() != null && e.getArgSpec().isOption()
                    && Arrays.asList(((CommandLine.Model.OptionSpec) e.getArgSpec()).names()).contains("-l")) {
                throw new CliException(String.format(UNKNOWN_LANGUAGE_EXCEPTION, e.getValue(),
                        String.join(", ", LanguageLoader.getAllAvailableLanguageIdentifiers())));
            }
            throw new CliException("Error during parsing", e);
        } catch (CommandLine.PicocliException e) {
            throw new CliException("Error during parsing", e);
        }
        return false;
    }

    /**
     * If {@link #parse()} has not been called yet, this will be empty, otherwise it will be a valid object.
     * @return The parsed cli options.
     */
    public CliOptions getCliOptions() {
        return options;
    }

    /**
     * Resolves the language selected by the cli arguments.
     * @return The selected language
     * @throws CliException In the event the language cannot be resolved. Should not happen under normal circumstances.
     */
    public Language getSelectedLanguage() throws CliException {
        if (this.parseResult.subcommand() == null) {
            return this.options.language;
        }

        ParseResult subcommand = this.parseResult.subcommand();

        Language language = LanguageLoader.getLanguage(subcommand.commandSpec().name()).orElseThrow(() -> new CliException(IMPOSSIBLE_EXCEPTION));

        LanguageOptions languageOptions = language.getOptions();
        languageOptions.getOptionsAsList().forEach(option -> {
            if (subcommand.hasMatchedOption(option.getNameAsUnixParameter())) {
                option.setValue(subcommand.matchedOptionValue(option.getNameAsUnixParameter(), null));
            }
        });
        return language;
    }

    /**
     * @return The submission directories configured for the subcommand, if one has been given.
     */
    public List<File> getSubcommandSubmissionDirectories() {
        if (this.parseResult.subcommand() != null && this.parseResult.subcommand().hasMatchedPositional(0)) {
            return this.parseResult.subcommand().matchedPositional(0).getValue();
        }
        return Collections.emptyList();
    }

    private String generateDescription() {
        var randomDescription = DESCRIPTIONS[RANDOM.nextInt(DESCRIPTIONS.length)];
        return String.format(DESCRIPTION_PATTERN, randomDescription, CREDITS);
    }
}
