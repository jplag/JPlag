package jplagTutorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import java.rmi.RemoteException;
import com.sun.xml.rpc.client.ClientTransportException;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;

import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

import jplagTutorial.jplagClient.*;
import jplagTutorial.util.JPlagClientAccessHandler;
import jplagTutorial.util.ZipUtil;

public class ExampleClient {
    /*
     * Status constants
     */
    public static final int JPLAG_UPLOADING = 0;
    public static final int JPLAG_INQUEUE = 50;
    public static final int JPLAG_PARSING = 100;
    public static final int JPLAG_COMPARING = 200;
    public static final int JPLAG_GENRESULT = 230;
    public static final int JPLAG_PACKRESULT = 250;
    public static final int JPLAG_DONE = 300;
    public static final int JPLAG_ERROR = 400;
    
    /*
     * Login data
     */
    private String username = null;
    private String password = null;
    
    /**
     * Options for JPlag specified by the command line
     */
    private Option option = new Option();
    
    /**
     * Name of directory where the result pages will be stored
     */
    private String resultDirName = "result";
    
    /**
     * True, if the user wants to get a list of his submissions on the server
     */
    private boolean listSubmissions = false;
    
    /**
     * The number of the submission to be downloaded plus 1 or 0
     */
    private int downloadResultNumber = 0;
    
    /**
     * The number of the submission to be cancelled plus 1 or 0
     */
    private int cancelSubmissionNumber = 0;

    /**
     * Suffix array generated from the suffix option or the language info
     * suffix array
     */
    private String[] suffixes = null;

    /**
     * Filename filter used by collectInDir()
     */
    private FilenameFilter subdirFileFilter = null;
    
    /**
     * Current position of progress bar
     */
    private int progressPos;
    
    /**
     * Maximum position of progress bar
     */
    private int progressMax;
    
    /**
     * The stub for the JPlag Web Service
     */
    private JPlagTyp_Stub stub = null;
    
    /**
     * Helper function to easily evaluate web service related exceptions
     * @param e Exception thrown by a stub method
     */
    public static void checkException(Exception e) {
        if(e instanceof JPlagException) {
            JPlagException je = (JPlagException) e;
            System.out.println("JPlagException: " + je.getDescription()
                + "\n" + je.getRepair());
        }
        else if(e instanceof RemoteException) {
            RemoteException re = (RemoteException) e;
            Throwable cause = re.getCause();
            if(cause != null && cause instanceof ClientTransportException) {
                cause = ((JAXRPCExceptionBase) cause).getLinkedException();
                if(cause != null) {
                    System.out.println("Connection exception: "
                        + cause.getMessage());
                    return;
                }
            }
            System.out.println("Unexpected RemoteException: "
                + re.getMessage());
            re.printStackTrace();
        }
        else {
            System.out.println("Unexpected Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the JPlag stub, by installing an all-trusting trust manager
     * for the SSL connection to the server, instantiating a stub object and
     * setting username and password
     * @return True, if username and password have been set
     */
    private boolean initJPlagStub() {
        /*
         * Create a trust manager that does not validate certificate chains
         */
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs,
                        String authType) {}
                public void checkServerTrusted(X509Certificate[] certs,
                        String authType) {}
            }
        };
        
        /*
         * Install the all-trusting trust manager
         */
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("Warning: Unable to install all-trusting trust "
                + "manager! SSL connection may not work!");
        }
        
        /*
         * Get JPlag client stub 
         */
        stub = (JPlagTyp_Stub) (new JPlagService_Impl()
                .getJPlagServicePort());
                
        /*
         * Search for the JPlagClientAccessHandler in the handler chain
         */
        HandlerChain handlerchain = stub._getHandlerChain();
        Iterator handlers = handlerchain.iterator();
        JPlagClientAccessHandler accessHandler = null;
        while(handlers.hasNext()) {
            Handler handler = (Handler) handlers.next();
            if(handler instanceof JPlagClientAccessHandler) {
                accessHandler = (JPlagClientAccessHandler)handler;
                break;
            }
        }
        
        if(accessHandler == null) {
            System.out.println("Unable to find access handler! Cannot set "
                + "username and password!");
            return false;
        }
        
        /*
         * Initialize access handler
         */
        accessHandler.setUserPassObjects(username, password);
        
        return true;
    }

    /**
     * Accepts directories and files with one of the given suffixes
     */
    private class RecursiveFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            if(new File(dir, name).isDirectory()) return true;
            
            for(int i=0; i<suffixes.length; i++) {
                if(name.endsWith(suffixes[i]))
                    return true;
            }
            return false;
        }
    }
    
    /**
     * Only accepts files with one of the given suffixes
     */
    private class NonRecursiveFilenameFilter implements FilenameFilter {
        public boolean accept(@SuppressWarnings("unused") File dir,
                String name)
        {
            for(int i=0; i<suffixes.length; i++) {
                if(name.endsWith(suffixes[i]))
                    return true;
            }
            return false;
        }
    }
        
    /**
     * Collects all valid files inside a directory. If subdirFileFilter also
     * accepts directories, subdirectories are included in the search
     * @param colfiles Vector receiving the found files
     * @param dir The directory which will be searched
     */
    private void collectInDir(Vector<File> colfiles, File dir) {
        if(!dir.exists()) return;
        
        File[] files = dir.listFiles(subdirFileFilter);
        
        for(int i=0; i<files.length; i++) {
            if(files[i].isDirectory()) {
                collectInDir(colfiles, files[i]);
            }
            else colfiles.add(files[i]);
        }
    }
    
    /**
     * Collects all valid files according to the set options
     * @return A Vector object of all valid files
     */
    private Vector<File> collectFiles() {
        Vector<File> colfiles = new Vector<File>();
        
        File[] files = new File(option.getOriginalDir()).listFiles(
            new RecursiveFilenameFilter());
        
        if(files == null) {
            System.out.println("\"" + option.getOriginalDir()
                + "\" is not a directory or an I/O error occurred!");
            return null;
        }
        
        if(option.isReadSubdirs())
            subdirFileFilter = new RecursiveFilenameFilter();
        else
            subdirFileFilter = new NonRecursiveFilenameFilter();
        
        for(int i=0; i<files.length; i++) {
            if(files[i].isDirectory()) {
                if(option.getPathToFiles()!=null)
                    collectInDir(colfiles, new File(files[i],
                        option.getPathToFiles()));
                else
                    collectInDir(colfiles, files[i]);
            }
            else colfiles.add(files[i]);
        }
        
        if(colfiles.size() <= 1) {
            System.out.println("\"" + option.getOriginalDir()
                + "\" didn't contain at least two files\n"
                + "suitable for the specified options!");
            return null;
        }
        return colfiles;
    }
    
    /**
     * Creates a temporary zip file containing all files specified by the Option
     * object and sends it to the server in 80 kB parts
     * @return The submissionID string or null, if there was an error
     */
    private String sendSubmission() {
        Vector<File> submissionFiles = collectFiles();
        if(submissionFiles == null) return null;
        
        File zipfile = null;
        FileInputStream input = null;
        String submissionID = null;
        
        try {
            zipfile = File.createTempFile("jplagtmp",".zip");
            ZipUtil.zipFilesTo(submissionFiles, option.getOriginalDir(),
                zipfile);

            input = new FileInputStream(zipfile);
            
            int filesize = (int) zipfile.length();
            int sentsize = 0;
            int partsize = (filesize<81920) ? filesize : 81920;
            
            byte[] data = new byte[partsize];
            input.read(data);
            
            initProgressBar(filesize);
            
            StartSubmissionUploadParams params =
                new StartSubmissionUploadParams(option, filesize, data);
            
            submissionID = stub.startSubmissionUpload(params);
            
            sentsize += partsize;
            while(sentsize<filesize-partsize) {
                setProgressBarValue(sentsize);
                input.read(data);
                stub.continueSubmissionUpload(data);
                sentsize += partsize;
            }
            if(sentsize!=filesize) {   // transfer last part
                setProgressBarValue(sentsize);
                data = new byte[filesize-sentsize];
                input.read(data);
                stub.continueSubmissionUpload(data);
                sentsize = filesize;
            }
            setProgressBarValue(sentsize);
            input.close();
            zipfile.delete();
        }
        catch(Exception e) {
            System.out.println();
            checkException(e);
            if(input != null) {
                try { input.close(); } catch(Exception ex) {}
            }
            if(zipfile != null) zipfile.delete();
            return null;
        }
        return submissionID;
    }
    
    /**
     * Retrieves the status of the given submission
     * @param submissionID submission ID identifying the submission
     * @return The according status object or null on error
     */
    private Status getStatus(String submissionID) {
        Status status;
        try {
            status = stub.getStatus(submissionID);
        }
        catch(Exception e) {
            checkException(e);
            return null;
        }
        return status;
    }
    
    /**
     * Waits until either the submission has been finished or an error occurred
     * @param submissionID String identifying the submission
     * @return True, if the submission has been completed successfully
     */
    private boolean waitForResult(String submissionID) {
        Status status;
        try {
            while(true) {
                status = stub.getStatus(submissionID);
                
                /*
                 * Here you could print out more details about the status of
                 * the submission, but it's left out here... 
                 */
                
                if(status.getState() >= JPLAG_DONE) break;
                Thread.sleep(10000);    // wait 10 seconds
                System.out.print(".");  // tell user something's happening
            }
            if(status.getState() >= JPLAG_ERROR) {
                /*
                 * An error occurred: Print out error message and acknowledge
                 * error by cancelling the submission
                 */
                System.out.println("\nSome error occurred: "
                    + status.getReport());
                stub.cancelSubmission(submissionID);
                return false;
            }
        }
        catch(Exception e) {
            checkException(e);
            return false;
        }
        return true;
    }
    
    /**
     * Downloads and unzips the result
     * @param submissionID The submission id String
     * @return True on success
     */
    private boolean receiveResult(String submissionID) {
        File zipfile = null;
        FileOutputStream output = null;
        
        try {
            File resultDir = new File(resultDirName);
            if(!resultDir.exists()) resultDir.mkdirs();
            zipfile = File.createTempFile("jplagtmpresult",".zip");
            
            output = new FileOutputStream(zipfile);

            StartResultDownloadData srdd = stub.startResultDownload(
                submissionID);

            int filesize = srdd.getFilesize();
            int loadedsize = srdd.getData().length;

            initProgressBar(srdd.getFilesize());
            output.write(srdd.getData());
            setProgressBarValue(loadedsize);
            
            while(loadedsize<filesize) {
                byte[] data = stub.continueResultDownload(0);
                output.write(data);
                loadedsize += data.length;
                setProgressBarValue(loadedsize);
            }
            output.close();
            
            /*
             * Unzip result archive and delete the zip file
             */
            
            ZipUtil.unzip(zipfile, resultDir);
            zipfile.delete();
        }
        catch(Exception e) {
            if(output != null) {
                try { output.close(); } catch(Exception ex) {}
            }
            if(zipfile != null) zipfile.delete();
            checkException(e);
            return false;
        }            
            
        return true;
    }
    
    /**
     * Cancels the submission identified by submissionID
     * @param submissionID The submission id string
     * @return True on success
     */
    private boolean cancelSubmission(String submissionID) {
        try {
            stub.cancelSubmission(submissionID);
        }
        catch(Exception e) {
            checkException(e);
            return false;
        }
        return true;
    }
    
    /**
     * Checks the current options for validity using the information provided
     * by the ServerInfo object and fills remaining empty fields with defaults.
     * If "-l ?" or "-cl ?" is used, a list of valid languages respectively
     * country languages is printed and false is returned.
     * @param info ServerInfo object
     * @return True, if all options are legal
     */
    private boolean checkOptions(ServerInfo info) {
        LanguageInfo[] languages = info.getLanguageInfos();
        String[] countryLangs = info.getCountryLanguages();
        int i;
        
        if(option.getLanguage() == null) {
            i = 0;
            option.setLanguage(languages[0].getName());
            System.out.println("Using default language: "
                + languages[0].getName());
        }
        else {
            for(i=0; i<languages.length; i++) {
                if(option.getLanguage().equals(languages[i].getName())) break;
            }
            if(i==languages.length) {
                if(!option.getLanguage().equals("?"))
                    System.out.println("Unknown language: \""
                        + option.getLanguage() + "\"");
                System.out.println("\nAvailable languages:");
                for(i=0; i<languages.length; i++) {
                    System.out.println(" - \"" + languages[i].getName()
                        + "\"" + (i==0 ? " (default language)\n" : "\n")
                        + "   default minimum match length = "
                        + languages[i].getDefMinMatchLen()
                        + "\n   default suffixes: "
                        + arrayToString(languages[i].getSuffixes()));
                }
                return false;
            }
        }
        if(suffixes == null) {
            suffixes = languages[i].getSuffixes();
            System.out.println("Using default suffixes: "
                + arrayToString(suffixes));
        }
        
        if(option.getTitle() == null) 
            option.setTitle("submission-"
                + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        
        if(option.getCountryLang() == null)
            option.setCountryLang("en");
        else {
            for(i=0; i<countryLangs.length; i++) {
                if(option.getCountryLang().equals(countryLangs[i])) break;
            }
            if(i==countryLangs.length) {
                if(!option.getCountryLang().equals("?"))
                    System.out.println("Unknown country language: \""
                        + option.getCountryLang() + "\"");
                System.out.println("\nAvailable country languages:");
                for(i=0; i<countryLangs.length; i++) {
                    System.out.println(" - \"" + countryLangs[i]
                        + (i==0 ? "\" (default)" : "\""));
                }
                return false;
            }
        }
        return true;
    }
    
    /**
     * Parses the arguments and sets the appropriate attributes
     * @param args Array of argument strings
     * @return True, if no error was noticed like missing login data
     */
    private boolean parseArguments(String[] args) {
        boolean requestDetails = false;
        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-user") && i+1<args.length) {
                i++;
                username = args[i];
            }
            else if(args[i].equals("-pass") && i+1<args.length) {
                i++;
                password = args[i];
            }
            else if(args[i].equals("-l") && i+1<args.length) {
                i++;
                option.setLanguage(args[i]);
                if(args[i].equals("?")) requestDetails = true;
            }
            else if(args[i].equals("-cl") && i+1<args.length) {
                i++;
                option.setCountryLang(args[i]);
                if(args[i].equals("?")) requestDetails = true;
            }
            else if(args[i].equals("-s")) {
                option.setReadSubdirs(true);
            }
            else if(args[i].equals("-S") && i+1<args.length) {
                i++;
                option.setPathToFiles(args[i]);
            }
            else if(args[i].equals("-p") && i+1<args.length) {
                i++;
                suffixes = args[i].split(",");
                option.setSuffixes(suffixes);
            }
            else if(args[i].equals("-t") && i+1<args.length) {
                i++;
                try {
                    option.setMinimumMatchLength(Integer.parseInt(args[i]));
                }
                catch(NumberFormatException e) {
                    System.out.println("Illegal minimum match length: "
                        + args[i] + "\nMust be an integer!");
                    return false;
                }
            }
            else if(args[i].equals("-m") && i+1<args.length) {
                i++;
                option.setStoreMatches(args[i]);
            }
            else if(args[i].equals("-bc") && i+1<args.length) {
                i++;
                option.setBasecodeDir(args[i]);
            }
            else if(args[i].equals("-r") && i+1<args.length) {
                i++;
                resultDirName = args[i];
            }
            else if(args[i].equals("-title") && i+1<args.length) {
                i++;
                option.setTitle(args[i]);
            }
            else if(args[i].equals("-list")) {
                listSubmissions = true;
            }
            else if(args[i].equals("-download")) {
                if(i+1<args.length && Character.isDigit(args[i+1].charAt(0))) {
                    // isDigit is true => positive value
                    i++;
                    try {
                        downloadResultNumber = Integer.parseInt(args[i]);
                    }
                    catch(NumberFormatException e) {
                        System.out.println("Illegal download number: "
                            + args[i] + "\nMust be an positive integer!");
                        return false;
                    }
                }
                else downloadResultNumber = 1;
            }
            else if(args[i].equals("-cancel")) {
                if(i+1<args.length && Character.isDigit(args[i+1].charAt(0))) {
                    // isDigit is true => positive value
                    i++;
                    try {
                        cancelSubmissionNumber = Integer.parseInt(args[i]);
                    }
                    catch(NumberFormatException e) {
                        System.out.println("Illegal cancel number: "
                            + args[i] + "\nMust be an positive integer!");
                        return false;
                    }
                }
                else cancelSubmissionNumber = 1;
            }
            else if(args[i].startsWith("-")) {
                System.out.println("Unknown option: " + args[i]);
                return false;
            }
            else
            {
                if(option.getOriginalDir() != null) {
                    System.out.println("The rootdir has already been defined "
                        + "as \"" + option.getOriginalDir() + "\"!");
                    return false;
                }
                option.setOriginalDir(args[i]);
            }
        }
        boolean valid = true;
        if(username == null) {
            System.out.println("Username is missing!");
            valid = false;
        }
        if(password == null) {
            System.out.println("Password is missing!");
            valid = false;
        }
        if(!requestDetails && !listSubmissions && downloadResultNumber == 0
                && cancelSubmissionNumber == 0
                && option.getOriginalDir() == null) {
            System.out.println("You must specify either a \"root-dir\", the "
                + "\"-list\", the \"-download\"\nor the \"-cancel\" option!");
            valid = false;
        }
        return valid;
    }
    
    /**
     * Initializes a text progress bar
     */
    public void initProgressBar(int max) {
        progressMax = max;
        progressPos = 0;
        System.out.println(
            "0%----------+----------50%-----------+--------100%");
    }
    
    /**
     * Sets the current value of the progress bar updating the current position
     * The progress may only increase, not decrease!
     */
    public void setProgressBarValue(int val) {
        int pos = (val*50)/progressMax;
        if(pos <= progressPos) return;
        
        System.out.print("##################################################"
            .substring(progressPos, pos));
        progressPos = pos;
    }
    
    /**
     * Prints out how to use the program
     */
    public static void printUsage() {
        System.out.println(
            "\nUsage: ExampleClient [options] (<root-dir> | -list | -download [<n>]\n"
            + "                                | -cancel [<n>])\n"
            + "<root-dir> The directory which contains all programs\n"
            + "Options are:\n"
            + " -user <username>  Sets the username (required).\n"
            + " -pass <password>  Sets the password (required).\n"
            + " -l <language>     (Language) Programming language.\n"
            + "                   (\"-l ?\" for supported and default languages.\n"
            + "                   Also lists default suffixes and minimum match length)\n"
            + " -S <dir>          Look in directories <root-dir>/*/<dir> for programs.\n"
            + "                   (default: <root-dir>/*)\n"
            + " -s                (Subdirs) Look at files in subdirs, too. (default: disabled)\n"
            + " -p <suffixes>     <suffixes> is a comma-separated list of filename suffixes\n"
            + "                   to be included. (default: language specific)\n"
            + " -t <n>            (Token) Set the minimum match length in tokens.\n"
            + "                   A smaller <n> increases the sensitivity of the comparison.\n"
            + " -m <n>            (Matches) Number of matches that will be saved. (default:20)\n"
            + " -m <p>%           Saves all matches with more than <p>% average similitarity.\n"
            + " -bc <dir>         Name of the directory containing the basecode\n"
            + "                   (common framework).\n"
            + " -r <dir>          (Result) Name of directory where the result pages will\n"
            + "                   be stored. (default: result)\n"
            + " -title <title>    Title of this submission (default: submission-<date>)\n"
            + " -cl <locale>      (Country language) Language the result files will\n"
            + "                   be written in.\n"
            + "                   (\"-cl ?\" for supported country languages and default)\n"
            + " -list             Lists all submissions on the server belonging to the user.\n"
            + " -download [<n>]   Downloads the <n>-th submission from server.\n"
            + "                   The <n>-th submission must be \"done\".\n"
            + "                   All non required options except \"-r <dir>\" will be ignored.\n"
            + " -cancel [<n>]     Cancels the <n>-th submission on server.\n"
            + "                   All non required options will be ignored.\n");
    }
    
    /**
     * Concatenates the string representations of objects in an array
     * @param array Object array
     * @return Comma-separated list of string representations of those objects
     */
    private String arrayToString(Object[] array) {
        String str = "";
        for(int i=0; i<array.length; i++) {
            str += array[i].toString();
            if(i!=array.length-1) str += ",";
        }
        return str;
    }
    
    /**
     * The main routine
     * @param args Array of command line parameters
     */
    public void run(String[] args) {
        if(args.length == 0 || !parseArguments(args)) {
            printUsage();
            return;
        }
        
        if(!initJPlagStub()) {
            System.out.println("Unable to initialize JPlag stub!");
            return;
        }
        
        /*
         * Get a ServerInfo object
         */
        
        ServerInfo info;
        try {
            info = stub.getServerInfo();
        }
        catch(Exception e) {
            checkException(e);
            return;
        }
        
        /*
         * Check for submissions on server by looking at submissions field
         * in the ServerInfo object
         */ 
        
        Submission[] subs = info.getSubmissions();
        
        if(subs.length > 0) {
            System.out.println("\nSubmissions on server with states:\n");
            for(int i=0; i<subs.length; i++) {
                System.out.println(" " + (i+1) + ". \"" + subs[i].getTitle()
                    + "\" on " + subs[i].getDate());
                String stateString = "";
                switch(subs[i].getLastState()) {
                    case JPLAG_UPLOADING: // can not occur in this application
                        stateString = "uploading";
                        break;
                    case JPLAG_INQUEUE: stateString = "in queue"; break;
                    case JPLAG_PARSING: stateString = "parsing"; break;
                    case JPLAG_COMPARING: stateString = "comparing"; break;
                    case JPLAG_GENRESULT:
                        stateString = "generating result files";
                        break;
                    case JPLAG_PACKRESULT:
                        stateString = "packing result files";
                        break;
                    case JPLAG_DONE: stateString = "done"; break;
                    default: {
                        // an error occurred, so get more details
                        Status status = getStatus(subs[i].getSubmissionID());
                        if(status == null)
                            stateString = "unable to retrieve status";
                        else
                            stateString = status.getReport();
                    }
                }
                System.out.println("    (" + stateString + ")");
            }
            
            if(listSubmissions) return;
            
            /*
             * If "-download" is used, download the n-th submission
             */
            if(downloadResultNumber != 0) {
                if(downloadResultNumber > subs.length) {
                    System.out.println("Illegal download number!\n"
                        + "There are only " + subs.length + " submissions!");
                    return;
                }
                Submission sub = subs[downloadResultNumber-1];
                if(sub.getLastState() != 300) {
                    System.out.println("Illegal download number!\n"
                        + "You can only download results for successfully"
                        + " finished submissions!");
                    return;
                }
                System.out.print("Downloading \"" + sub.getTitle()
                    + "\"...");
                if(!receiveResult(sub.getSubmissionID())) return;
                System.out.println(" completed.\nThe result files are available"
                    + " in \"" + resultDirName + "\"");
                return;
            }
            
            /*
             * If "-cancel" is used, cancel the n-th submission
             */
            if(cancelSubmissionNumber != 0) {
                if(cancelSubmissionNumber > subs.length) {
                    System.out.println("Illegal cancel number!\n"
                        + "There are only " + subs.length + " submissions!");
                    return;
                }
                Submission sub = subs[cancelSubmissionNumber-1];
                System.out.print("Cancelling \"" + sub.getTitle()
                    + "\"...");
                if(!cancelSubmission(sub.getSubmissionID())) return;
                System.out.println(" completed.\n");
                return;
            }
        }
        else if(downloadResultNumber != 0 || cancelSubmissionNumber != 0
                || listSubmissions) {
            System.out.println("\nCurrently there are no submissions on the "
                + "server for this user!");
            return;
        }

        if(!checkOptions(info)) return;
        
        System.out.println("\nSending files...");
        String submissionID = sendSubmission();
        if(submissionID == null) return;
        
        System.out.print("\n\nWaiting for result...");
        if(!waitForResult(submissionID)) return;
        
        System.out.println(" result available.\n\nDownloading...");
        if(!receiveResult(submissionID)) return;
        
        System.out.println("\n\nThe result files are available in \""
            + resultDirName + "\"");
    }
    
    public static void main(String[] args) {
        new ExampleClient().run(args);
    }
}
