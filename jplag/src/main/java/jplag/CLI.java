package jplag;

import jplag.options.LanguageOption;

public class CLI {

  public static void main(String[] args) {

    try {
      JPlagOptions options = JPlagOptions.fromArgs(args);
      options.setLanguageOption(LanguageOption.JAVA_1_9);
      options.setRootDirName("/Users/philippbauch/Develop/jplag-test");
      options.setBaseCode("base-code");

      JPlag program = new JPlag(options);

      System.out.println("JPlag initialized");
      JPlagResult run = program.run();
      System.out.println(run);
    } catch (ExitException ex) {
      System.out.println("Error: " + ex.getReport());
      System.exit(1);
    }

  }
}
