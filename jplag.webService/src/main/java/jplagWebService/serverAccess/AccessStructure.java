package jplagWebService.serverAccess;

/**
 * Author Emeric Kwemou, Moritz Kroll
 */

//import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import jplag.options.CommandLineOptions;
import jplag.options.util.ZipUtil;
import jplagWebService.server.JPlagException;
import jplagWebService.server.Status;
import jplagWebService.serverImpl.JPlagCentral;
import jplagWebService.serverImpl.JPlagTypImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AccessStructure {
	public static final int MAX_UNZIPPED_SIZE = 60 * 1024 * 1024;
    
    /*
     * Directory settings
     */

    private static String JPLAG_DIRECTORY = null;
    private static String JPLAG_ENTRIES_DIRECTORY = null;
    private static String JPLAG_RESULTS_DIRECTORY = null;

    /*
     * Some defaults
     */

    private static final int DEFAULT_STORABLE = 20;
    private static final int MAX_STORABLE = 50;
    private static final int MIN_STORABLE = 10;

    private static final String PARSERLOG = "parser-log.txt";

    /*
     * Class attributes
     */

    private String title = null;
    private String username = null;

    private String submissionID = "Not initialised!";

    private long date = 0;

    private String commandLineInString = "";

    private long lastStatusRequest = 0; // System.currentTimeMillis();

    private jplag.options.CommandLineOptions options = null;

    private StatusDecorator dec;

    /**
     * Constructs a new AccessStructure object for an Option
     */
    public AccessStructure(String user,
            jplagWebService.server.Option usr_option) throws JPlagException {
        dec = new StatusDecorator(new Status());
        submissionID = JPlagCentral.getNextSubmissionID();
        commandLineInString = generateCMD(usr_option);
        username = user;
        title = usr_option.getTitle();
        date = System.currentTimeMillis();

        generateStructure(usr_option, commandLineInString);
    }

    /**
     * Constructs a new AccessStructure object for an Option and a MimeMultipart
     */
    public AccessStructure(String user, jplagWebService.server.Option usr_option,
            MimeMultipart inputZipFile) throws JPlagException {
        this(user, usr_option);
        saveZipFile(inputZipFile);
    }

    /**
     * Protected constructor to be used to recreate an AccessStructure out of an
     * XML tag
     */
    protected AccessStructure(String submissionid, CommandLineOptions copts,
            String username, String title, long date, int laststate,
            String report) {
        dec = new StatusDecorator(new Status(laststate, 0, report));
        this.username = username;
        this.title = title;
        this.submissionID = submissionid;
        this.date = date;
        options = copts;
    }

    /**
     * Ensure that temporary directories for entries and results exist
     */
    public static void ensureExistence() {
        JPLAG_DIRECTORY = System.getProperty("jplag_home");
        if (JPLAG_DIRECTORY == null) {
            System.out.println("jplag_home property not set!!");
            return;
        }
        System.out.println("jplag_home = " + JPLAG_DIRECTORY);
        JPLAG_ENTRIES_DIRECTORY = JPLAG_DIRECTORY + File.separator + "entries";
        JPLAG_RESULTS_DIRECTORY = JPLAG_DIRECTORY + File.separator + "results";
        File f = new File(JPLAG_ENTRIES_DIRECTORY);
        f.mkdir();
        f = new File(JPLAG_RESULTS_DIRECTORY);
        f.mkdir();
    }

    /**
     * @return Command line options to be displayed in the result file
     */
    private static String generateCMD(jplagWebService.server.Option opt) {
        String result = "path_to_files=" + opt.getPathToFiles()
                + " min_token_length=" + opt.getMinimumMatchLength()
                + " basecode_dir=" + opt.getBasecodeDir()
                + " read_subdirs=" + opt.isReadSubdirs()
                + " clustertype=" + opt.getClustertype()
                + " store_matches=" + opt.getStoreMatches()
                + " suffixes=";
        for(int i=0; i<opt.getSuffixes().length; i++)
        {
            result += opt.getSuffixes()[i]
                + ((i==opt.getSuffixes().length-1) ? "" : ",");
        }
        result += " language=" + opt.getLanguage() + " title=" + opt.getTitle();

        return result;
    }
	
	/**
	 * @return True, if the given string is not a null or empty string.
	 * 		   False, otherwise
	 */
	private static boolean isSet(String str) {
		return str!=null && str.length()!=0;
	}

    /**
     * This method is used to translate a jplag.server.Option object to a
     * jplag.options.CommandLineOptions object
     * 
     * @param usr_opt
     * @param argsInString
     * @throws JPlagException, if the options are not valid
     */
    private void generateStructure(jplagWebService.server.Option usr_opt,
            String argsInString) throws JPlagException {

        Vector<String> vec = new Vector<String>();
        vec.add("JPlag");

        // Verbose parser and verbose quiet (don't fill server logs)
		
		vec.add("-vp");
        vec.add("-vq");
		
        if (usr_opt.isReadSubdirs())
            vec.add("-s");

        if (isSet(usr_opt.getPathToFiles())) {
            // Search with pattern <rootdir>/*/<pathToFiles>
            vec.add("-S");
            vec.add(usr_opt.getPathToFiles());
        }

        vec.add("-m");
        String tmpmatch = usr_opt.getStoreMatches();
        if (!isSet(tmpmatch)) {
            vec.add(DEFAULT_STORABLE + "");
        } else {
            int index = tmpmatch.indexOf("%");
            try {
                if (index != -1) {
                    tmpmatch = tmpmatch.substring(0, index);
                    int percent = Integer.parseInt(tmpmatch);
                    if (percent > 100) {
                        throw new JPlagException("optionsException",
                                "Illegal store matches option specified!",
                                "There can't be a similarity bigger than 100%!");
                    }
                    tmpmatch += "%";
                } else {
                    int nummatches = Integer.parseInt(tmpmatch);
                    if (nummatches > MAX_STORABLE)
                        nummatches = MAX_STORABLE;
                    if (nummatches < MIN_STORABLE)
                        nummatches = MIN_STORABLE;
                    tmpmatch = nummatches + "";
                }
            }
			catch (NumberFormatException ex) {
                throw new JPlagException("optionsException",
                        "Illegal store matches option specified!",
                        "Please check the usage function for correct "
                                + "store_matches format");
            }
            vec.add(tmpmatch);
        }
        
        // Comparison mode
        vec.add("-compmode");
        if(usr_opt.getComparisonMode() == null)
            vec.add(jplag.options.Options.COMPMODE_NORMAL + "");
        else
            vec.add(usr_opt.getComparisonMode().toString());

		// Language the output should "speak"

        vec.add("-clang");
        vec.add(usr_opt.getCountryLang());

        // Suffixes

        if (usr_opt.getSuffixes().length != 0) {
            vec.add("-p");
            
            String str = "";
            String[] suffixes = usr_opt.getSuffixes();
            for(int i=0; i<suffixes.length; i++) {
                str += suffixes[i];
                if(i != suffixes.length-1) str += ",";
            }

            vec.add(str);
        }

        // Use base code directory?

        if (isSet(usr_opt.getBasecodeDir())) {
            vec.add("-bc");
            vec.add(usr_opt.getBasecodeDir());
        }

        // Clustering
		
        if (isSet(usr_opt.getClustertype())) {
            vec.add("-clustertype");
            vec.add(usr_opt.getClustertype());
        }

        // Language

        String language = usr_opt.getLanguage();
        int languagenum;

		// Check whether this language name exists
        for (languagenum = 0; languagenum < JPlagTypImpl.languageInfos.length; languagenum++) {
			if (JPlagTypImpl.languageInfos[languagenum].getName().equals(language)) {
				vec.add("-l");
                vec.add(language);
                break;
            }
        }
        if (languagenum == JPlagTypImpl.languageInfos.length) {
            System.out.println("Wrong language specified: " + language);
            throw new JPlagException(
					"optionsException",
                    "Illegal language specified!",
                    "Use getServerInfo to see which languages are supported " +
                    "and how they are spelled!");
        }

        // Detecting sensitivity
        // TODO: Should there be a minimal minimum match length being accepted??
		
		int mintoklen = usr_opt.getMinimumMatchLength();
        if (mintoklen <= 0)
            mintoklen = JPlagTypImpl.languageInfos[languagenum].getDefMinMatchLen();
		
        vec.add("-t");
		vec.add(mintoklen + "");

        // Result dir
        String submissiondir = submissionID + username;

        String result_dir = JPLAG_RESULTS_DIRECTORY + File.separator
        					+ submissiondir;
        vec.add("-r");
        vec.add(result_dir);

        // Output dir

        vec.add("-o");
        File file12s = new File(result_dir);
        if (!(file12s.exists()))
            file12s.mkdir();
        String tmp11st = result_dir + File.separator + PARSERLOG;
        vec.add(tmp11st);

		// Original dir
		
		vec.add("-d");
		vec.add(usr_opt.getOriginalDir());
		
		// Title
		
		vec.add("-title");
		vec.add(usr_opt.getTitle());
		
        // Root_dir
        
        vec.add(JPLAG_ENTRIES_DIRECTORY + File.separator + submissiondir);
        
        /**
         * All command line options have now been set and a first option
         * validation has been done
         */
        try {
            String[] args = new String[vec.size()];
            for (int i = 0; i < args.length; i++)
                args[i] = vec.elementAt(i);

            options = new jplag.options.CommandLineOptions(args, argsInString);
            getDecorator().add(options.getState(), options.getProgress(), "");
        }
		catch (jplag.ExitException e) {
            throw new JPlagException("invalidOptionsException", e.getReport(),
                "Check your options");
        }
    }

    /**
     * @return The path (including the filename) to the result zip file
     */
    public String getEntryPath() {
        return JPLAG_ENTRIES_DIRECTORY + File.separator + getSubmissionID()
                + getUsername() + ".zip";
    }
    
    /**
     * Saves the zip file inside the entries directory
     */
    public void saveZipFile(MimeMultipart inputZipFile) {
        try {
            MimeBodyPart bdp = (MimeBodyPart) inputZipFile.getBodyPart(0);
            DataHandler dh = bdp.getDataHandler();
            File part = new File(getEntryPath());
            FileOutputStream fos = new FileOutputStream(part);
            dh.writeTo(fos);
            fos.close();
            System.gc();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes a whole directory including subdirectories
     * 
     * XXX: Recursive routine! Beware stack overflows!?
     */
    public static void deleteDir(File dir) {
        if (!dir.isDirectory()) {
            System.out.println("Util::deleteDir(): " + dir
                    + " is not a directory!");
            return;
        }

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                deleteDir(files[i]);
            else
                files[i].delete();
        }
        dir.delete();
    }

    /**
     * Deletes the submission input zip file and the unpacked contents
     * 
     * @return True, if server allowed to delete the files
     */
    public boolean deleteEntryFiles() {
        String tmp2 = getSubmissionID() + getUsername();
        File zipfile = new File(JPLAG_ENTRIES_DIRECTORY + File.separator + tmp2
                + ".zip");
        File entrydir = new File(JPLAG_ENTRIES_DIRECTORY + File.separator
                + tmp2);

        try {
            zipfile.delete();
            if(entrydir.exists()) deleteDir(entrydir);
        } catch (SecurityException ex) {
            System.out.println("Not allowed to delete entry files!");
            return false;
        }
        return true;
    }

    /**
     * Deletes the submission result zip file
     * 
     * @return True, if server allowed to delete the files
     */
    public boolean deleteResultFiles() {
        /*
         * if (!resultAdmin.deleteResult(struct.getUsername(), struct
         * .getSubmissionID())) return false;
         */

        String tmp2 = getSubmissionID() + getUsername();
        File zipfile = new File(JPLAG_RESULTS_DIRECTORY + File.separator + tmp2
                + ".zip");
        File resultdir = new File(JPLAG_RESULTS_DIRECTORY + File.separator
                + tmp2);

        try {
            zipfile.delete();
            if (resultdir.exists())
                deleteDir(resultdir);
        } catch (SecurityException ex) {
            System.out.println("Not allowed to delete zipped result file!");
            return false;
        }
        return true;
    }

    /**
     * Deletes all files from this submission
     * 
     * @return True, if server allowed to delete the files
     */
    public boolean delete() {
        if (getState() < 300)
            return deleteEntryFiles();
        else
            return deleteResultFiles();
    }

    /**
     * @return A MimeMultipart object containing the zipped result files
     */
    public MimeMultipart getResult() {

        File file = new File(JPLAG_RESULTS_DIRECTORY + File.separator
                + submissionID + getUsername() + ".zip");

        MimeMultipart mmp = new MimeMultipart();

        FileDataSource fds1 = new FileDataSource(file);
        MimetypesFileTypeMap mftp = new MimetypesFileTypeMap();
        mftp.addMimeTypes("multipart/zip zip ZIP");
        fds1.setFileTypeMap(mftp);

        MimeBodyPart mbp = new MimeBodyPart();

        try {
            mbp.setDataHandler(new DataHandler(fds1));
            mbp.setFileName(file.getName());

            mmp.addBodyPart(mbp);
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        return mmp;
    }

    /**
     * @return The path (including the filename) to the result zip file
     */
    public String getResultPath() {
		return JPLAG_RESULTS_DIRECTORY + File.separator + getSubmissionID() + getUsername() + ".zip";
    }

    /**
     * Unzips the submission input file into a <submissionID><username>folder
     * inside the entries directory
     */
    public void unzipEntry() throws jplag.ExitException {
        String tmp2 = getSubmissionID() + getUsername();
		File part = new File(JPLAG_ENTRIES_DIRECTORY + File.separator + tmp2 + ".zip");

        int totalsize = ZipUtil.unzip(part, JPLAG_ENTRIES_DIRECTORY, tmp2);
        if(totalsize > MAX_UNZIPPED_SIZE)
            throw new jplag.ExitException("Submission too big! It may be "
                + (MAX_UNZIPPED_SIZE / 1024) + " kB at maximum, but it is "
                + (totalsize / 1024) + " kB!");
        
        File unzipped = new File(JPLAG_ENTRIES_DIRECTORY + File.separator + tmp2);

        // Searching root dir
        File[] files = unzipped.listFiles();

		String root_dir = (files.length == 1) ? files[0].getPath() : unzipped.getPath();
        files = null;
        getOption().root_dir = root_dir;
        System.gc();
    }

    public static String getJPLAG_DIRECTORY() {
        return JPLAG_DIRECTORY;
    }

    public StatusDecorator getDecorator() {
        return this.dec;
    }

    /**
     * Simpler version of setLastStatusRequest(long lastStatusRequest) The
     * parameter is always System.currentTimeMillis();
     */
    public void setLastStatusRequest() {
        setLastStatusRequest(System.currentTimeMillis());
    }

    /**
     * @param lastStatusRequest:
     *            The last time user has performed a getStatus request. This
     *            method is used to prohibit getStatus flooding
     */
    public synchronized void setLastStatusRequest(long lastStatusRequest) {
        this.lastStatusRequest = lastStatusRequest;
    }

    /**
     * @return Last time of status request
     */
    public long getLastStatusRequest() {
        return lastStatusRequest;
    }

    public int getState() {
        return dec.getState();
    }

    public jplag.options.CommandLineOptions getOption() {
        return this.options;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(String subid) {
        submissionID = subid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCMDInString() {
        return this.commandLineInString;
    }

    /**
     * @param doc
     *            The document for which the new element is to be created
     * @return An "entry" element to be stored in the user submission database
     */
    public Element toXMLEntryElement(Document doc) {
        Element entry = doc.createElement("entry");

        entry.setAttribute("id", getSubmissionID());
        entry.setAttribute("username", getUsername());
        entry.setAttribute("title", getTitle());
        entry.setAttribute("date", Long.toString(getDate()));
        entry.setAttribute("commandLine", getCMDInString());

        jplag.options.CommandLineOptions copt = getOption();
        String[] args = copt.getArgs();

        entry.setAttribute("numargs", args.length + "");
        for (int i = 0; i < args.length; i++) {
            entry.setAttribute("arg_" + i, args[i]);
        }

        return entry;
    }

    /**
     * @param doc
     *            The document for which the new element is to be created
     * @return A "result" element to be stored in the user submission database
     */
    public Element toXMLResultElement(Document doc) {
        Element submission = doc.createElement("result");

        submission.setAttribute("id", getSubmissionID());
        submission.setAttribute("title", getTitle());
        submission.setAttribute("date", Long.toString(date));
        submission.setAttribute("laststate", getState() + "");
        if(dec.getReport().length()>0)
        	submission.setAttribute("report", dec.getReport());

        return submission;
    }

    /**
     * @param entry
     *            Entry element to be parsed
     * @return AccessStructure object being usable in JPlagCentral.run()
     */
    public static AccessStructure fromXMLEntryElement(Element entry) {
        String subID = entry.getAttribute("id");
        String username = entry.getAttribute("username");
        String title = entry.getAttribute("title");
        long date;
        try {
            date = Long.parseLong(entry.getAttribute("date"));
        } catch (NumberFormatException ex) {
            System.out.println("jplag.server.serverAccess.AccessStructure:"
                    + " NumberFormatException during date=parseInt("
                    + entry.getAttribute("date") + ")");
            date = 0;
        }
        int numargs;
        try {
            numargs = Integer.parseInt(entry.getAttribute("numargs"));
        } catch (NumberFormatException ex) {
            System.out.println("jplag.server.serverAccess.AccessStructure:"
                    + " NumberFormatException during numargs=parseInt("
                    + entry.getAttribute("numargs") + ")");
            numargs = 0;
        }
        String[] args = new String[numargs];
        String commandLine = entry.getAttribute("commandLine");
        for (int i = 0; i < args.length; i++) {
            args[i] = entry.getAttribute("arg_" + i);
        }
        jplag.options.CommandLineOptions copt;
        try {
            copt = new jplag.options.CommandLineOptions(args, commandLine);
            //Added Emeric Kwemou 22-03-05. I just conserve the commandline in
            // a string
        } catch (jplag.ExitException ex) {
            System.out.println("ExitException caught in getNextEntry()");
            ex.printStackTrace();
            return null;
        }

        return new AccessStructure(subID, copt, username, title, date,
            StatusDecorator.WAITING_IN_QUEUE, "");
    }

    /**
     * @param submission
     *            Result element to be parsed
     * @return AccessStructure object for informational use
     */
    public static AccessStructure fromXMLResultElement(Element submission) {
        Element user = (Element) submission.getParentNode();

        String subID = submission.getAttribute("id");
        String username = user.getAttribute("username");
        String title = submission.getAttribute("title");
        long date;
        try {
            date = Long.parseLong(submission.getAttribute("date"));
        } catch (NumberFormatException ex) {
            System.out.println("jplag.server.serverAccess.AccessStructure:"
                    + " NumberFormatException during date=parseInt("
                    + submission.getAttribute("date") + ")");
            date = 0;
        }

        int laststate;
        try {
            laststate = Integer.parseInt(submission.getAttribute("laststate"));
        } catch (NumberFormatException ex) {
            System.out.println("jplag.server.serverAccess.AccessStructure:"
                    + " NumberFormatException during laststate=parseInt("
                    + submission.getAttribute("laststate") + ")");
            laststate = 499;
        }
        
        String report = submission.getAttribute("report");

        return new AccessStructure(subID, null, username, title, date,
                laststate, report);
    }
}