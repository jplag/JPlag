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

    parser.addArgument("rootDir")
        .help("The root-directory that contains all submissions");
    parser.addArgument("-l", "--language")
        //  Supported Languages: java19 (default), java 17, java15, java15dm, java12, java11, python3, c/c++, c#-1.2, char, text, scheme");
        .choices("java", "python3").setDefault("java")
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
    String language = ns.getString("language");

    try {
      JPlagOptions options = new JPlagOptions(
          rootDir,
          LanguageOption.JAVA_1_9
      );
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
