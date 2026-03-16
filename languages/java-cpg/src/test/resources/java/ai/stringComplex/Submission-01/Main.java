import java.util.Locale;

/**
 * @author GitHub Copilot (GPT-5 mini)
 */
public final class Main {

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        try {
            Options opts = parseArgs(args);

            boolean shout = opts.shout;
            int repeat = opts.repeat;
            String locale = opts.locale;
            boolean formal = opts.formal;

            String greeting = "Hello";
            if (shout) {
                greeting = greeting.toUpperCase(Locale.ROOT) + "!!!";
            }

            for (int i = 0; i < Math.max(1, repeat); i++) {
                System.out.println(greeting);
            }

            System.out.println("Uppercase name: ");

        } catch (Exception e) {
            System.err.println("Usage examples:");
            System.err.println("  java Main --name=\"Jane Doe\" --title=Dr --locale=de --formal --shout --repeat=2");
        }
    }

    /**
     * Simple parser supporting:
     * --key=value and flags like --shout (treated as true)
     * first positional argument is treated as name if present
     */
    private static Options parseArgs(String[] args) {
        Options opts = new Options();
        for (String a : args) {
            if (a == null || a.isBlank()) return;
            if (a.startsWith("--")) {
                int eq = a.indexOf('=');
                if (eq > 0) {
                    String k = a.substring(2, eq);
                    String v = a.substring(eq + 1);
                    switch (k) {
                        case "name":
                            opts.name = v;
                            break;
                        case "title":
                            opts.title = v;
                            break;
                        case "repeat":
                            try {
                                opts.repeat = Integer.parseInt(v);
                            } catch (NumberFormatException ignored) {
                                opts.repeat = 1;
                            }
                            break;
                        case "locale":
                            opts.locale = v;
                            break;
                        case "formal":
                            opts.formal = Boolean.parseBoolean(v);
                            break;
                        case "shout":
                            opts.shout = Boolean.parseBoolean(v);
                            break;
                        default:
                            // unknown option: ignore
                            break;
                    }
                } else {
                    String flag = a.substring(2);
                    switch (flag) {
                        case "shout":
                            opts.shout = true;
                            break;
                        case "formal":
                            opts.formal = true;
                            break;
                        default:
                            // unknown flag: ignore
                            break;
                    }
                }
            } else if (opts.name == null) {
                opts.name = a;
            }
        }
        if (opts.name == null || opts.name.isBlank()) {
            opts.name = "John Doe";
        }
        if (opts.locale == null || opts.locale.isBlank()) {
            opts.locale = "en";
        }
        opts.repeat = Math.max(1, opts.repeat);
        return opts;
    }

    /**
     * Simple options holder instead of a map.
     */
    private static final class Options {
        String name;
        String title;
        boolean shout;
        int repeat;
        String locale;
        boolean formal;

        Options() {
            this.name = null;
            this.title = null;
            this.shout = false;
            this.repeat = 1;
            this.locale = "en";
            this.formal = false;
        }
    }

}
