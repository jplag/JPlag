package jplag;

import java.io.File;
import jplag.options.LanguageOption;
import jplag.reporting.Report;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;

public class CLI {

  public static void main(String[] args) {
    ArgumentParser parser = ArgumentParsers.newFor("prog").build()
        .description("Process some integers.");
    parser.addArgument("integers")
        .metavar("N")
        .type(Integer.class)
        .nargs("+")
        .help("an integer for the accumulator");
    parser.addArgument("--sum")
        .dest("accumulate")
        .help("sum the integers (default: find the max)");

    try {
      JPlagOptions options = new JPlagOptions(
          "/Users/philippbauch/Develop/jplag-test",
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
