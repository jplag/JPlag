package jplag;

import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

import java.io.File;

import jplag.options.LanguageOption;
import jplag.options.Verbosity;
import jplag.reporting.Report;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command line interface class, allows using via command line.
 * @see CLI#main(String[])
 */
public class CLI {
    
    private static final String DESCRIPTION = "JPlag - Detecting Software Plagiarism";
    private static final String PROGRAM_NAME = "jplag";
    private static final String[] verbosityOptions = {"parser", "quiet", "long", "details"};
    private static final String[] languageOptions = new String[] {"java_1_1", "java_1_2", "java_1_5", "java_1_5_dm", "java_1_7", "java_1_9",
            "python_3", "c_cpp", "c_sharp", "char", "text", "scheme"};
    
    private final ArgumentParser parser;

    /**
     * Main class for using JPlag via the CLI.
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
        parser.addArgument("-l").choices(languageOptions).setDefault("java_1_9").help("Select the language to parse the submissions");
        parser.addArgument("-bc").help("Name of the directory which contains the base code (common framework)");
        parser.addArgument("-v").choices(verbosityOptions).setDefault("quiet").help("Verbosity");
        parser.addArgument("-d").help("(Debug) parser. Non-parsable files will be stored").action(storeTrue());
        parser.addArgument("-S").help("Look in directories <root-dir>/*/<dir> for programs");
        parser.addArgument("-p").help("comma-separated list of all filename suffixes that are included");
        parser.addArgument("-x").help("All files named in <file> will be ignored");
        parser.addArgument("-t").help("Tune the sensitivity of the comparison. A smaller <n> increases the sensitivity");
        parser.addArgument("-s").help("Similarity Threshold: all matches above this threshold will be saved");
        parser.addArgument("-r").setDefault("result").help("Name of directory in which the web pages will be stored");
    }

    /**
     * Parses an array of argument strings.
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
     * @param namespace encapsulates the parsed arguments in a {@link Namespace} format.
     * @return the newly built options.F
     */
    public JPlagOptions buildOptionsFromArguments(Namespace namespace) {
        String fileSuffixString = namespace.getString("p");
        String[] fileSuffixes = new String[] {};
        if (fileSuffixString != null) {
            fileSuffixes = fileSuffixString.replaceAll("\\s+", "").split(",");
        }
        LanguageOption language = LanguageOption.fromOption(namespace.getString("l"));
        Verbosity verbosity = Verbosity.fromOption(namespace.getString("v"));

        JPlagOptions options = new JPlagOptions(namespace.getString("rootDir"), language);
        options.setBaseCodeSubmissionName("base-code");
        options.setVerbosity(verbosity);
        options.setDebugParser(namespace.getBoolean("d"));
        options.setSubdirectoryName(namespace.getString("S"));
        options.setFileSuffixes(fileSuffixes);
        options.setExclusionFileName(namespace.getString("x"));
        options.setMinTokenMatch(namespace.getInt("t"));
        options.setSimilarityThreshold(namespace.getFloat("s"));
        return options;
    }
}
