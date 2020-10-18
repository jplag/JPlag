package jplag;

import jplag.options.JPlagOptions;

public class CLI {

  public static void main(String[] args) {

    try {
      JPlagOptions options = JPlagOptions.fromArgs(args);
      JPlag program = new JPlag(options);

      System.out.println("JPlag initialized");
      program.run();
    } catch (ExitException ex) {
      System.out.println("Error: " + ex.getReport());
      System.exit(1);
    }

  }
}
