package jplag;


import jplag.options.CommandLineOptions;

public class JPlag {
	public static void main(String[] args) {
		if (args.length == 0)
			CommandLineOptions.usage();
		else {
            try {
                CommandLineOptions options = new CommandLineOptions(args, null);
                Program program = new Program(options);

                System.out.println("initialize ok");
                program.run();
            }
            catch(ExitException ex) {
                System.out.println("Error: "+ex.getReport());
                System.exit(1);
            }
		}
	}
}
