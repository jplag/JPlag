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

public class CLI {

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("jplag").build().defaultHelp(true).description("JPlag - Detecting Software Plagiarism");

        String[] languageOptions = {"java_1_1", "java_1_2", "java_1_5", "java_1_5_dm", "java_1_7", "java_1_9", "python_3", "c_cpp", "c_sharp", "char",
                "text", "scheme"};
        String[] verbosityOptions = {"parser", "quiet", "long", "details"};

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

        Namespace ns = null;

        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        String rootDir = ns.getString("rootDir");
        String languageOption = ns.getString("l");
        String verbosityOption = ns.getString("v");
        String subDirName = ns.getString("S");
        String fileSuffixString = ns.getString("p");
        String[] fileSuffixes = new String[] {};
        if (fileSuffixString != null) {
            fileSuffixes = fileSuffixString.replaceAll("\\s+", "").split(",");
        }
        String exclusionFile = ns.getString("x");
        String reportDirName = ns.getString("r");
        float similarityThreshold = ns.getFloat("s");
        int minTokenMatch = ns.getInt("t");
        boolean useDebugParser = ns.getBoolean("d");

        LanguageOption language = LanguageOption.fromOption(languageOption);
        Verbosity verbosity = Verbosity.fromOption(verbosityOption);

        try {
            JPlagOptions options = new JPlagOptions(rootDir, language);
            options.setBaseCodeSubmissionName("base-code");
            options.setVerbosity(verbosity);
            options.setDebugParser(useDebugParser);
            options.setSubdirectoryName(subDirName);
            options.setFileSuffixes(fileSuffixes);
            options.setExclusionFileName(exclusionFile);
            options.setMinTokenMatch(minTokenMatch);
            options.setSimilarityThreshold(similarityThreshold);

            JPlag program = new JPlag(options);

            System.out.println("JPlag initialized");
            JPlagResult result = program.run();

            File reportDir = new File(reportDirName);
            Report report = new Report(reportDir);

            report.writeResult(result);
        } catch (ExitException ex) {
            System.out.println("Error: " + ex.getReport());
            System.exit(1);
        }
    }
}
