package jplag;

import java.io.File;
import jplag.options.LanguageOption;
import jplag.reporting.Report;

public class CLI {

  public static void main(String[] args) {
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
