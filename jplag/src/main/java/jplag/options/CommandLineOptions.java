/*
 * Author  Emeric Kwemou on 30.01.2005
 */
package jplag.options;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import jplag.ExitException;
import jplag.Language;
import jplag.Program;

public class CommandLineOptions extends Options {
    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public CommandLineOptions(String[] args) throws jplag.ExitException {
        this(args, null);
    }

    public CommandLineOptions(String[] args, String cmdInString)
            throws jplag.ExitException {
        this.args = args;

        initialize(args);

        // @Changed by Moritz Kroll 26.02.2005
        // set to default language if not specified
        // changed by Emeric 22-03-05 commandLine set to be compatible with
        // the web service requirements

        if (languageName == null)
            languageName = languages[0];
        if (cmdInString != null)
            commandLine = cmdInString;
    }

    private void initialize(String[] args) throws jplag.ExitException {
        int i = 0;
        try {
            for (i = 0; i < args.length; i++)
                if (args[i].startsWith("-"))
                    i = scanOption(args, i);
                else
                    this.root_dir = args[i];
        }
		catch (NumberFormatException e) {
            throw new jplag.ExitException("Bad parameter for option '"
                    + args[i] + "': " + args[i + 1] + " is not a "
                    + "positive integer!", ExitException.BAD_PARAMETER);
        }
        if (args.length == 0) {
            usage();
        } else {
            for (i = 0; i < args.length; i++)
                this.commandLine += args[i] + " ";
        }

        System.gc();
    }

    private boolean scanOption(String arg) throws jplag.ExitException {
        if (arg.equals("-s")) {
            this.read_subdirs = true;
        } else if (arg.equals("-external")) { // hidden option!
            System.out.println("External search activated!");
            this.externalSearch = true;
        } else if (arg.equals("-skipparse")) { // hidden option!
            System.out.println("Skip parse activated!");
            this.skipParse = true;
        } else if (arg.equals("-diff")) { // hidden option!
            System.out.println("Diff-Report activated!");
            this.diff_report = true;
        } else if (arg.equals("-L")) { // hidden option!
            printAllLanguages();

        } else if (arg.startsWith("-v") && arg.length() > 2) {
            for (int i = 2; i < arg.length(); i++)
                switch (arg.charAt(i)) {
                case 'q':
                    this.verbose_quiet = true;
                    break;
                case 'l':
                    this.verbose_long = true;
                    break;
                case 'p':
                    this.verbose_parser = true;
                    break;
                case 'd':
                    this.verbose_details = true;
                    break;
                case 's': // hidden Option
                    this.language = new jplag.javax.Language(null);// WARNING!!!!!BOMB
                    this.min_token_match = this.language.min_token_match();
                    this.suffixes = this.language.suffixes();
                    this.verbose_quiet = true;
                    this.exp = true;
                    break;
                default:
                    return false;
                }
        } else
            return false;
        return true;
    }

    private boolean found1 = false;


    private int scanOption(String[] args, int i)
		throws NumberFormatException, jplag.ExitException
    {
        String arg = args[i];
        if (arg.equals("-S") && i + 1 < args.length) {
            sub_dir = args[i + 1];
            i++;
        } else if (arg.equals("-o") && i + 1 < args.length) {
            output_file = args[i + 1];
            i++;
        } else if (arg.equals("-bc") && i + 1 < args.length) {
			// Will be validated later as root_dir is not set yet
            useBasecode = true;
            basecode = args[i + 1];
            i++;
        } else if (arg.equals("-d") && i + 1 < args.length) {
            // original directory - when used in the server environment.
            debugParser = true;
            original_dir = args[i + 1];
            i++;
        } else if (arg.equals("--") && i + 1 < args.length) {
            root_dir = args[i + 1];
            i++;
        } else if (arg.equals("-x") && i + 1 < args.length) {
            exclude_file = args[i + 1];
            i++;
        } else if (arg.equals("-clang") && i + 1 < args.length) {
            countryTag = args[i+1];
            countryTag.toLowerCase();
            i++;
        } else if (arg.equals("-i") && i + 1 < args.length) {
            include_file = args[i + 1];
            i++;
        } else if (arg.equals("-t") && i + 1 < args.length) {
            min_token_match = Integer.parseInt(args[i + 1]);
            if (min_token_match < 1) {
                throw new jplag.ExitException(
                    "Illegal value: Minimum token length is less or " +
                    "equal zero!",ExitException.BAD_SENSITIVITY_OF_COMPARISON);
            }
            min_token_match_set = true;
            i++;
        } else if (arg.equals("-m") && i + 1 < args.length) {
            String tmp = args[i + 1];
                    int index;
            if ((index = tmp.indexOf("%")) != -1) {
                store_percent = true;
                tmp = tmp.substring(0, index);
            }
            if ((store_matches = Integer.parseInt(tmp)) < 0)
                throw new NumberFormatException();
            if (store_matches > MAX_RESULT_PAIRS)
                store_matches = MAX_RESULT_PAIRS;
            i++;
        } else if (arg.equals("-r") && i + 1 < args.length) {
            result_dir = args[i + 1];
            i++;
        } else if (arg.equals("-l") && i + 1 < args.length) {
			// Will be validated later when the language routines are chosen
            languageIsFound = true;
            languageName = args[i + 1].toLowerCase();
            i++;
        } else if (arg.equals("-p") && i + 1 < args.length) {
            String suffixstr = args[i + 1];          
            if (suffixstr.equals("")) {
            	i++;
            }
            else {
                Vector<String> vsuffies = new Vector<String>();
                StringTokenizer st = new StringTokenizer(suffixstr, ",");
                while (st.hasMoreTokens()) {
    				suffixstr = st.nextToken();
    				suffixstr.trim();
                    if (suffixstr.equals(""))
                        continue;
                    vsuffies.addElement(suffixstr);
                }
                suffixes = new String[vsuffies.size()];
                vsuffies.copyInto(suffixes);
                suffixes_set = true;
                i++;
            }
        } else if (arg.equals("-f") && i + 1 < args.length && this.exp) {
            this.filter = new jplag.filter.Filter(args[i + 1]);
            this.filtername = args[i + 1];
            i++; // EXPERIMENT!!
        } else if (arg.equals("-filter") && i + 1 < args.length) {
            System.out.println("Filter activated!");
            jplag.text.Parser parser = new jplag.text.Parser();
            // This Parser object doesn't have its "program" attribute
			// initialized but the initializeFilter method doesn't use it anyway
            try {
                parser.initializeFilter(args[i + 1]);
            } catch (java.io.FileNotFoundException e) {
                throw new jplag.ExitException("Filter file not found!",
						ExitException.BAD_PARAMETER);
            }
            i++;
        } else if (arg.equals("-compmode") && i + 1 < args.length) {
            comparisonMode = Integer.parseInt(args[i + 1]);
            if (comparisonMode < COMPMODE_NORMAL || comparisonMode > COMPMODE_REVISION)
                throw new jplag.ExitException("Illegal comparison mode: \"" + comparisonMode + "\"");
            i++;
        } else if (arg.equals("-compare") && i + 1 < args.length) {
            if ((this.compare = Integer.parseInt(args[i + 1])) < 0)
                throw new NumberFormatException();
            System.out.println("Special comparison activated. Parameter: "
                    + this.compare);
            i++;
        } else if (arg.equals("-clustertype") && i + 1 < args.length) {
            this.clustering = true;
            String tmp = args[i + 1].toLowerCase();
            if (tmp.equals("min"))
                this.clusterType = MIN_CLUSTER;
            else if (tmp.equals("max"))
                this.clusterType = MAX_CLUSTER;
            else if (tmp.equals("avr"))
                this.clusterType = AVR_CLUSTER;
            else 
                throw new jplag.ExitException("Illegal clustertype: \"" + tmp
                        + "\"\nAvailable types are 'min', 'max' and 'avr'!");
			
            System.out.println("Clustering activated; type: "
                    + args[i + 1].toUpperCase());
            i++;
        } else if (arg.equals("-threshold") && i + 1 < args.length) {
            if (args[i + 1].equals("")) {
                throw new jplag.ExitException("Threshold-list is empty!");
            }
            try {
                int number = 0;
                String help;
                StringTokenizer st = new StringTokenizer(args[i + 1], ",");
                while (st.hasMoreTokens()) {
                    help = st.nextToken();
                    help.trim();
                    if (help.equals(""))
                        continue;
                    if (Float.parseFloat(help) < 0)
                        throw new NumberFormatException();
                    number++;
                }
                if (number == 0) {
                    throw new jplag.ExitException("No threshold given!");
                }
                this.threshold = new float[number];
                st = new StringTokenizer(args[i + 1], ",");
                number = 0;
                while (st.hasMoreTokens()) {
                    help = st.nextToken();
                    help.trim();
                    if (help.equals(""))
                        continue;
                    this.threshold[number++] = Float.parseFloat(help);
                }
            } catch (NoSuchElementException e) {
                throw new jplag.ExitException(
                        "Error parsing '-threshold' option!");
            }
            System.out.print("Thresholds: ");
            for (int x = 0; x < this.threshold.length; x++)
                System.out.print(this.threshold[x] + " ");
            System.out.println();
            i++;
        } else if (arg.equals("-themewords") && i + 1 < args.length) {
            if (args[i + 1].equals("")) {
                throw new jplag.ExitException("Themeword-list is empty!");
            }
            try {
                int number = 0;
                String help;
                StringTokenizer st = new StringTokenizer(args[i + 1], ",");
                while (st.hasMoreTokens()) {
                    help = st.nextToken();
                    help.trim();
                    if (help.equals(""))
                        continue;
                    if (Integer.parseInt(help) < 0)
                        throw new NumberFormatException();
                    number++;
                }
                if (number == 0) {
                    throw new jplag.ExitException("No themeword given!");
                }
                this.themewords = new int[number];
                st = new StringTokenizer(args[i + 1], ",");
                number = 0;
                while (st.hasMoreTokens()) {
                    help = st.nextToken();
                    help.trim();
                    if (help.equals(""))
                        continue;
                    this.themewords[number++] = Integer.parseInt(help);
                }
            } catch (NoSuchElementException e) {
                throw new jplag.ExitException(
                        "Error parsing '-themewords' option!");
            }
            System.out.print("Themewords: ");
            for (int x = 0; x < this.themewords.length; x++)
                System.out.print(this.themewords[x] + " ");
            System.out.println();
            i++;
        } else if (arg.equals("-title") && i+1 < args.length) {
            if (args[i + 1].equals("")) {
                throw new jplag.ExitException("Title is empty!");
            }
            this.title = args[i + 1];
            i++;
        } else if (arg.equals("-c") && i + 2 < args.length){
            this.fileListMode = true;
            while (i + 1 < args.length){
                this.fileList.add(args[i + 1]);
                i++;
            }
        } else if (!scanOption(arg))
            throw new jplag.ExitException("Unknown option: " + arg,
                    ExitException.BAD_PARAMETER);

        if (!languageIsFound && i >= args.length - 2)
            throw new jplag.ExitException("No language found...",
                    ExitException.BAD_LANGUAGE_ERROR);

        return i;
    }

	public void initializeSecondStep(Program program) throws jplag.ExitException {

		for (int j = 0; j < languages.length - 1; j += 2)
			if (languageName.equals(languages[j]))
				try {
					Constructor<?>[] languageConstructors = Class.forName(languages[j + 1]).getDeclaredConstructors();
					Constructor<?> cons = languageConstructors[0];
					Object[] ob = { program };
					// All Language have to have a program as Constructor
					// Parameter
					// ->public Language(ProgramI prog)
					Language tmp = (Language) cons.newInstance(ob);
					this.language = tmp;
					System.out.println("Language accepted: " + tmp.name() + "\nCommand line: " + this.commandLine);
					found1 = true;
				} catch (ClassNotFoundException e) {
					System.out.println(e.getMessage());
				} catch (IllegalAccessException e) {
					System.out.println(e.getMessage());
				} catch (InstantiationException e) {
					System.out.println(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					throw new jplag.ExitException("Illegal value: Language instantiation failed", ExitException.BAD_LANGUAGE_ERROR);
				}
		if (!found1) {
			throw new jplag.ExitException("Illegal value: Language instantiation failed: Unknown language \"" + languageName + "\"",
					ExitException.BAD_LANGUAGE_ERROR);
		}

		// defaults
		if (!min_token_match_set)
			this.min_token_match = this.language.min_token_match();
		if (!suffixes_set)
			this.suffixes = this.language.suffixes();
		checkBasecodeOption();
	}
	
    /**
     * This method checks whether the basecode directory value is valid
     */
    private void checkBasecodeOption() throws jplag.ExitException {
        if (useBasecode) {
            if (basecode == null || basecode.equals("")) {
				throw new ExitException("Basecode option used but none " +
						"specified!",ExitException.BAD_PARAMETER);
            }
            String baseC = root_dir + File.separator + basecode;
            if (!(new File(root_dir)).exists()) {
				throw new ExitException("Root directory \"" + root_dir
						+ "\" doesn't exist!",ExitException.BAD_PARAMETER);
            }
            File f = new File(baseC);
            if (!f.exists()) {	// Basecode dir doesn't exist.
				throw new ExitException("Basecode directory \"" + baseC
						+ "\" doesn't exist!",ExitException.BAD_PARAMETER);
            }
            if(sub_dir != null && sub_dir.length()!=0) {
            	f = new File(baseC, sub_dir);
            	if(!f.exists()) {
            		throw new ExitException("Basecode directory doesn't contain"
            				+ " the subdirectory \"" + sub_dir + "\"!",
            				ExitException.BAD_PARAMETER);
            	}
            }
            System.out.println("Basecode directory \"" + baseC
            		+ "\" will be used");
        }
    }

}
