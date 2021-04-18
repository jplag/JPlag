package jplag;

import java.io.File;
import jplag.options.LanguageOption;
import jplag.reporting.Report;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class CLI {

  public static void main(String[] args) {
    ArgumentParser parser = ArgumentParsers.newFor("jplag").build()
        .defaultHelp(true)
        .description("JPlag - Detecting Software Plagiarism");

    String[] languageOptions = {"java_1_1", "java_1_2", "java_1_5", "java_1_5_dm", "java_1_7", "java_1_9", "python_3", "c_cpp", "c_sharp", "char", "text", "scheme"};

    parser.addArgument("rootDir")
        .help("The root-directory that contains all submissions");
    parser.addArgument("-l", "--language")
        .choices(languageOptions).setDefault("java_1_9")
        .help("Select the language to parse the submissions");
    parser.addArgument("-b", "--baseCode")
        .help("Name of the directory which contains the base code (common framework)");

    Namespace ns = null;

    try {
      ns = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    }

    String rootDir = ns.getString("rootDir");
    String languageOption = ns.getString("language");

    LanguageOption language = LanguageOption.fromOption(languageOption);

    try {
      JPlagOptions options = new JPlagOptions(rootDir, language);
      options.setBaseCodeSubmissionName("base-code");

      JPlag program = new JPlag(options);

      System.out.println("JPlag initialized");
      JPlagResult result = program.run();

      File reportDir = new File("result");
      Report report = new Report(reportDir);

      report.writeResult(result);
    } catch (ExitException ex) {
      System.out.println("Error: " + ex.getReport());
      System.exit(1);
    }
  }
}
