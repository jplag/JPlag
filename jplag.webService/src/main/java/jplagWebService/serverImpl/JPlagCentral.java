/*
 * Created on 01.03.2005
 */

package jplagWebService.serverImpl;

import java.io.File;
import java.util.Date;

import jplag.Program;
import jplag.options.CommandLineOptions;
import jplag.options.util.ZipUtil;
import jplagWebService.server.JPlagException;
import jplagWebService.server.UserInfo;
import jplagWebService.serverAccess.AccessStructure;
import jplagWebService.serverAccess.MailTemplateAdmin;
import jplagWebService.serverAccess.MemoryManager;
import jplagWebService.serverAccess.ResultAdmin;
import jplagWebService.serverAccess.StatusDecorator;
import jplagWebService.serverAccess.TransferManager;
import jplagWebService.serverAccess.UserAdmin;

/**
 * @author Moritz Kroll, Emeric Kwemou
 */
public class JPlagCentral extends Thread {
	/**
	 * The JPlagCentral singleton object
	 */
	private static JPlagCentral JPLAG_CENTRAL = null;

	/*
	 * Some constants
	 */
	public static final int READYQUEUE = ResultAdmin.SEARCH_ENTRIES;
	public static final int TERMINATEDQUEUE = ResultAdmin.SEARCH_RESULTS;
	public static final int ALLQUEUES = ResultAdmin.SEARCH_ALL;
	
	private static final int MAX_SUBMISSIONS_ON_SERVER=5;

	/**
	 * The currently processed submission structure
	 */
	protected static AccessStructure activeStruct = null;
	
	/**
	 * If set to true forces the main thread to be ended
	 */
	private static boolean doStopCentral = false;
	
	/**
	 * A singleton ResultAdministration object
	 */
    private static ResultAdmin resultAdmin = null;
    
    /**
     * A singleton UserAdmin object
     */
    private static UserAdmin userAdmin = null;
	
	/**
	 * A singleton MailTemplateAdmin object
	 */
	private static MailTemplateAdmin mailTemplateAdmin = null;
    
    /**
     * A singleton TransferManager object
     */
    private static TransferManager transferManager = null;
    
    private MemoryManager memoryManager;

    /**
     * The private constructor to be used to construct the singleton object.
     * Sets up the MemoryManager and ResultAdministration objects
     */
	private JPlagCentral() {
		memoryManager = MemoryManager.giveInstance();
        memoryManager.setManager_enabled(true);

		resultAdmin = new ResultAdmin(AccessStructure.getJPLAG_DIRECTORY());
		userAdmin = new UserAdmin(AccessStructure.getJPLAG_DIRECTORY());
		mailTemplateAdmin = new MailTemplateAdmin(
				AccessStructure.getJPLAG_DIRECTORY());
        transferManager = new TransferManager();
	}

	/**
 	 * @return A singleton object of JPlagCentral
	 */
	public static JPlagCentral getInstance() {
		if (JPLAG_CENTRAL == null) JPLAG_CENTRAL = new JPlagCentral();
		return JPLAG_CENTRAL;
	}
	
	public UserAdmin getUserAdmin() {
		return userAdmin;
	}
	
	public MailTemplateAdmin getMailTemplateAdmin() {
		return mailTemplateAdmin;
	}
    
    public TransferManager getTransferManager() {
        return transferManager;
    }

	public static synchronized AccessStructure search(int select,
			String submissionID)
    {
		if (activeStruct != null
				&& activeStruct.getSubmissionID().compareTo(submissionID) == 0)
		{
			activeStruct.getDecorator().setState(
					activeStruct.getOption().getState());
			activeStruct.getDecorator().setProgress(
					activeStruct.getOption().getProgress());
			return activeStruct;
		}
		return resultAdmin.getAccessStruct(submissionID,
				select);
	}

	public static synchronized void addToReadyQueue(AccessStructure struct) {
        struct.getDecorator().setState(StatusDecorator.WAITING_IN_QUEUE);
        struct.getDecorator().setProgress(resultAdmin.getNumEntries());
		resultAdmin.addEntry(struct);
	}

    // TODO: rename this function!
	public static synchronized void addToTerminatedQueue(AccessStructure struct)
	{
		if(struct==activeStruct) activeStruct=null;
	}

	public static synchronized void addToTerminatedQueue(AccessStructure struct,
			int state,int progress,String message)
    {
		struct.getDecorator().add(state,progress,message);
		if(struct==activeStruct) activeStruct=null;
	}
	
	private static synchronized AccessStructure getNextReadyEntry() {
		activeStruct = resultAdmin.getNextEntry();
		return activeStruct;
	}

	/**
	 * @return If the submissionID belongs to the activeStruct it returns
	 * 		   its state, otherwise it returns 0
	 */
	public static synchronized int getStateIfActive(String user,String subID) {
		if(activeStruct!=null && activeStruct.getSubmissionID().equals(subID)
				&& activeStruct.getUsername().equals(user))
			return activeStruct.getOption().getState();
		return 0;
	}
	
	/**
	 * Stops the JPlagCentral by setting some stop flags
	 */
	public synchronized void stopCentral() {
        doStopCentral = true;
		if(activeStruct!=null) activeStruct.getOption().forceProgramToStop();
		if(memoryManager==null)
		{
			// XXX: Sometimes stopCentral() is called several times??
			System.out.println("Memory manager has already been deleted!?");
			return;
		}
		memoryManager.setManager_enabled(false);
		userAdmin.stopUserAdmin();
        transferManager.stopTransferManager();
		
		// give threads time to complete their current work
		try { Thread.sleep(1000); } catch(Exception e) {}
		
		// if still alive, interrupt any sleep loops
		if(memoryManager.isAlive())	memoryManager.interrupt();
		if(userAdmin.isAlive()) userAdmin.interrupt();
        if(transferManager.isAlive()) transferManager.interrupt();
		
		memoryManager = null;
		userAdmin = null;
        transferManager = null;
	}

	/**
	 * Main loop processing the submissions
	 */
	public void run() {
		memoryManager.start();
		userAdmin.start();
		transferManager.start();
		System.out.println("[" + new Date() + "] JPlagCentral started");

		try {
			Program program = null;
			while (!doStopCentral) {
				AccessStructure struct = getNextReadyEntry();
				if (struct != null) {
					CommandLineOptions opt = struct.getOption();

					try {
						System.out.println("[" + new Date() + "] New project " + "found: " + opt.root_dir);
						struct.unzipEntry();

						program = new Program(opt);
						program.run();

						File result = program.get_jplagResult();

						ZipUtil.zip(result, result.getParentFile().getPath());
						System.out.println("[" + new Date() + "] Project done, " + "saved to " + result.getParentFile().getPath());

						addToTerminatedQueue(struct, StatusDecorator.COMPARE_SOURCE_DONE, 100, "");
						struct.setDate(System.currentTimeMillis());
						resultAdmin.addResult(struct);

						program.closeWriter();
						program = null;
						System.gc();
						try {
							AccessStructure.deleteDir(result);
						} catch (SecurityException ex) {
							System.out.println("Not allowed to delete results!");
						}
						struct.deleteEntryFiles();
					} catch (OutOfMemoryError e) {
						// Do panic garbage collection (doing anything else
						// would cause another OutOfMemoryError)
						program = null;
						System.gc();

						System.out.println("OutOfMemoryError!!!");
						e.printStackTrace(); // though there's nothing anyway

						struct.getDecorator().add(jplag.ExitException.UNKNOWN_ERROR_OCCURRED, -1,
								"Project terminated because of an OutOfMemoryError!");
						addToTerminatedQueue(struct);
						struct.setDate(System.currentTimeMillis());
						resultAdmin.addResult(struct);
					} catch (Throwable e) {
						if (e instanceof jplag.ExitException) {
							jplag.ExitException ex = (jplag.ExitException) e;
							System.out.println("[" + new Date() + "] " + "ExitException occurred: State=" + ex.getState() + " Report="
									+ ex.getReport());
							struct.getDecorator().add(ex);
						} else if (struct.getOption().isForceStop()) {
							struct.getDecorator().add(jplag.options.Options.SUBMISSION_ABORTED, -1, "Submission aborted!");

							System.out.println("Force stop exception occurred!");
							e.printStackTrace();
						} else {
							struct.getDecorator().add(jplag.ExitException.UNKNOWN_ERROR_OCCURRED, -1,
									"Project terminated with errors: " + e.getMessage());

							System.out.println("Exception occurred!");
							e.printStackTrace();
						}
						addToTerminatedQueue(struct);

						/*
						 * If exception was thrown because of an error, move
						 * this submission into the result pool. Otherwise when
						 * the server is going to be stopped the submission
						 * should stay in the entry queue to be processed the
						 * next time the server starts
						 */
						if (!struct.getOption().isForceStop()) {
							struct.setDate(System.currentTimeMillis());
							resultAdmin.addResult(struct);
						}
						if (program != null) {
							program.closeWriter();
							program = null;
						}
						System.gc();
					}
				} else {
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Throwable t) {
			System.out.println("Unknown exception caught!!! " + t.getMessage());
			t.printStackTrace();

			if (activeStruct != null) {
				try {
					activeStruct.getDecorator().add(jplag.ExitException.UNKNOWN_ERROR_OCCURRED, -1,
							"Unknown exception caught!!! " + t.getMessage());
				} catch (Throwable t2) {
					System.out.println("Setting activeStruct's error message " + "caused another exception! " + t2.getMessage());
					t2.printStackTrace();
				}
			}
		}
		System.out.println("[" + new Date() + "] JPlagCentral stopped");
	}

	/**
	 * @param struct Submission to be canceled (may be in entry queue, in the
	 * 				 processing or an result)
	 * @return True, if everything went fine
	 */
    public static boolean cancelSubmission(AccessStructure struct){
        if(struct==activeStruct) activeStruct.getOption().forceProgramToStop();
        resultAdmin.deleteSubmission(struct);
        return struct.delete();        
    }
    
    protected static jplagWebService.server.Submission[] listSubmissions(String username){
        return resultAdmin.listSubmissions(username);
    }
    
    public static AccessStructure[] listAccessStructures(String username){
        return resultAdmin.listAccessStructures(username);
    }
    
    public static String getNextSubmissionID(){
        return resultAdmin.getNextSubmissionID();
    }
    public static String[] usersList(){
        return resultAdmin.usersList();
    }
    
    public static void checkQuota(String username) throws JPlagException {
    	int numentries=resultAdmin.getNumUserEntries(username);
    	int numresults=resultAdmin.getNumUserResults(username);
		if(numentries>=MAX_SUBMISSIONS_ON_SERVER)
		{
			throw new JPlagException("quotaException",
				"Too many waiting submissions ("+numentries+")!",
				"Please wait for a submission to be finished or cancel one.");
		}
		if(numresults>=MAX_SUBMISSIONS_ON_SERVER)
		{
			throw new JPlagException("quotaException",
				"Too many finished submissions ("+numresults+")!",
				"Please download and delete a finished submission.");
		}
		if(numentries+numresults>=MAX_SUBMISSIONS_ON_SERVER)
		{
			throw new JPlagException("quotaException",
				"Too many waiting and finished submissions ("+numentries+"+"
				+ numresults+")!","Please download and delete a finished "
				+ "submission, wait for a submission to be finished or cancel "
				+ "a waiting submission.");
		}
    }
    
	public static UserInfo getUserInfo(String username) {
    	int numentries=resultAdmin.getNumUserEntries(username);
    	int numresults=resultAdmin.getNumUserResults(username);
		int leftSlots=MAX_SUBMISSIONS_ON_SERVER-numentries-numresults;
		return userAdmin.getUserInfo(username,leftSlots);
	}
}
