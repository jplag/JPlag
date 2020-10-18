package jplag;

import jplag.options.JPlagOptions;

public class JPlag {

  public static void main(String[] args) {

    try {
      JPlagOptions options = JPlagOptions.fromArgs(args);
      Program program = new Program(options);

      System.out.println("JPlag initialized");
      program.run();
    } catch (ExitException ex) {
      System.out.println("Error: " + ex.getReport());
      System.exit(1);
    }

  }
}
