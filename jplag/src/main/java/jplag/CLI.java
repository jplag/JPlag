package jplag;

import jplag.options.Language;

public class CLI {

  public static void main(String[] args) {

    try {
      JPlagOptions options = JPlagOptions.fromArgs(args);
      options.setLanguage(Language.JAVA_1_9);
      options.setRootDir("/Users/philippbauch/Develop/jplag-test");
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
