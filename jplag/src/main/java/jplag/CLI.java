package jplag;

import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

import java.io.File;

import jplag.options.JPlagOptions;
import jplag.options.LanguageOption;
import jplag.options.Verbosity;
import jplag.reporting.Report;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command line interface class, allows using via command line.
 *
 * @see CLI#main(String[])
 */
public class CLI {

    private static final String DESCRIPTION = "JPlag - Detecting Software Plagiarism";
    private static final String PROGRAM_NAME = "jplag";

    // TODO SH: Replace verbosity when integrating a real logging library
    private static final String[] verbosityOptions = {"parser", "quiet", "long", "details"};

    private final ArgumentParser parser;

    /**
     * Main class for using JPlag via the CLI.
     *
     * @param args are the CLI arguments that will be passed to JPlag.
     */
    public static void main(String[] args) {
        try {
            CLI cli = new CLI();
            Namespace arguments = cli.parseArguments(args);
            JPlagOptions options = cli.buildOptionsFromArguments(arguments);
            JPlag program = new JPlag(options);
            System.out.println("JPlag initialized");
            JPlagResult result = program.run();
            File reportDir = new File(arguments.getString("r"));
            Report report = new Report(reportDir);
            report.writeResult(result);
        } catch (ExitException exception) {
            System.out.println("Error: " + exception.getReport());
            System.exit(1);
        }
    }

    /**
     * Creates the command line interface and initializes the argument parser.
     */
    public CLI() {
        parser = ArgumentParsers.newFor(PROGRAM_NAME).build().defaultHelp(true).description(DESCRIPTION);

        parser.addArgument("rootDir").help("The root-directory that contains all submissions");
        parser.addArgument("-l")
                .choices(LanguageOption.getAllDisplayNames())
                .setDefault(LanguageOption.getDefault().getDisplayName())
                .help("Select the language to parse the submissions");
        parser.addArgument("-bc").help("Name of the directory which contains the base code (common framework)");
        parser.addArgument("-v").choices(verbosityOptions).setDefault("quiet").help("Verbosity");
        parser.addArgument("-d").help("(Debug) parser. Non-parsable files will be stored").action(storeTrue());
        parser.addArgument("-S").help("Look in directories <root-dir>/*/<dir> for programs");
        parser.addArgument("-p").help("comma-separated list of all filename suffixes that are included");
        parser.addArgument("-x").help("All files named in this file will be ignored in the comparison (line-separated list)");
        parser.addArgument("-t").help("Tune the sensitivity of the comparison. A smaller <n> increases the sensitivity");
        parser.addArgument("-s").setDefault(0f).help("Similarity Threshold [0-100]: all matches above this threshold will be saved");
        parser.addArgument("-r").setDefault("result").help("Name of directory in which the comparison results will be stored");
    }

    /**
     * Parses an array of argument strings.
     *
     * @param args is the array to parse.
     * @return the parsed arguments in a {@link Namespace} format.
     */
    public Namespace parseArguments(String[] args) {
        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        return null;
    }

    /**
     * Builds a options instance from parsed arguments.
     *
     * @param namespace encapsulates the parsed arguments in a {@link Namespace} format.
     * @return the newly built options.F
     */
    public JPlagOptions buildOptionsFromArguments(Namespace namespace) {
        String fileSuffixString = namespace.getString("p");
        String[] fileSuffixes = new String[]{};
        if (fileSuffixString != null) {
            fileSuffixes = fileSuffixString.replaceAll("\\s+", "").split(",");
        }
        LanguageOption language = LanguageOption.fromDisplayName(namespace.getString("l"));
        Verbosity verbosity = Verbosity.fromOption(namespace.getString("v"));

        JPlagOptions options = new JPlagOptions(namespace.getString("rootDir"), language);
        options.setBaseCodeSubmissionName(namespace.getString("bc"));
        options.setVerbosity(verbosity);
        options.setDebugParser(namespace.getBoolean("d"));
        options.setSubdirectoryName(namespace.getString("S"));
        options.setFileSuffixes(fileSuffixes);
        options.setExclusionFileName(namespace.getString("x"));

        String minTokenMatch = namespace.getString("t");
        if (minTokenMatch != null) {
            try {
                options.setMinTokenMatch(Integer.parseInt(minTokenMatch));
            } catch (NumberFormatException e) {
                System.out.println("Illegal comparison sensitivity. Ignoring input and taking language default value.");
            }
        }

        String similarityThreshold = namespace.getString("s");
        if (similarityThreshold != null) {
            try {
                options.setSimilarityThreshold(Float.parseFloat(similarityThreshold));
            } catch (NumberFormatException e) {
                System.out.println("Illegal similarity threshold. Taking 0 as default value.");
                options.setSimilarityThreshold(0);
            }
        }

        return options;
    }
}
