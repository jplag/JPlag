package jplagWebService.serverAccess;

import java.util.Date;
import java.util.Vector;

import java.io.*;

import jplagWebService.serverImpl.JPlagCentral;

public class MemoryManager extends Thread {
    private static final long ONEDAY = 86400000;
    private static final long MAX_SUB_SIZE = 104857600; // 100MB
    private static final long LOW_SUB_SIZE = 102400;    // 100kB
    private static final long MIN_TIME_ON_SERVER = 1;   // in days
    private static final long MAX_TIME_ON_SERVER = 7;   // in days
    
    /* 
     * constants for linear interpolation between MINIMAL_TIME_ON_SERVER
     * at MAX_SUB_SIZE and MAXIMAL_TIME_ON_SERVER at LOW_SUB_SIZE
     */
    
    private static final float a = 
        -(MAX_TIME_ON_SERVER-MIN_TIME_ON_SERVER) / (MAX_SUB_SIZE-LOW_SUB_SIZE);
    private static final float b = MIN_TIME_ON_SERVER - a * MAX_SUB_SIZE;

    private static MemoryManager instance = null;

    private static long LAST_LOADING_TIME = 0;

    /**
     * used to stop memory manager.
     */
    private boolean manager_enabled = false;

    /**
     * All submissions at this time.
     */
    private Vector<AccessStructure> submissions = new Vector<AccessStructure>();
    
    /**
     * Memory manager will react every SLEEP_TIME millisecond
     */
    private static final long SLEEP_TIME = 600000;// 10 min
 

    private MemoryManager() {
    }// cannot be instantiated out of this.

    /**
     * 
     * @return unique instance of the memory manager.
     */
    public static MemoryManager giveInstance() {
        if (instance == null)
            instance = new MemoryManager();
        return instance;

    }

    /**
     * @return Whether the submission is to be erased or not
     */
    private static boolean isErasable(AccessStructure struct) {
        // get size of struct
        String result_loc = struct.getResultPath();
        File res_file = new File(result_loc);
        long size = Math.max(res_file.length(), 1);
        
        /**
         * Check whether the file is bigger than the maximal one authorized
         */
        if(size>MAX_SUB_SIZE) return true;
        
        float time_diff = (System.currentTimeMillis()-struct.getDate())/ONEDAY;
        
        if (time_diff < MIN_TIME_ON_SERVER) return false;
        else if (time_diff >= MAX_TIME_ON_SERVER) return true;
        else if (time_diff >= a * size + b) return true;
        else return false;
    }

    /**
     * Mechanism to delete a submission from the Terminated Queue.
     * 
     * @param struct
     *            submission to be deleted
     */
    private static void manageDeletion(AccessStructure struct) {
        JPlagCentral.cancelSubmission(struct);

        System.out.println("[" + new Date() + "] MemoryManager: Submission \""
                + struct.getTitle() + "\" from user \""
                + struct.getUsername() + "\" with submissionID "
                + struct.getSubmissionID() + " saved on "
                + new Date(struct.getDate()) + " has been deleted");
    }
 
    /**
     * Used to actualize the vector containing all submissions
     */
    private void loadSubmissions() {
        submissions.clear();
        
        // Collect all usernames on the server
        String[] users = JPlagCentral.usersList();
        
        // Collect all submissions for each user
        for (int i = 0; i < users.length; i++) {
        	AccessStructure[] subm = JPlagCentral.listAccessStructures(users[i]);
            if (subm != null && subm.length != 0) {
                for (int j = 0; j < subm.length; j++) {
                    submissions.add(subm[j]);
                }
            }
        }
    }

    /**
     * MemoryManager main loop
     */
    public void run() {
    	System.out.println("MemoryManager running...");
    	try
    	{
    		while (manager_enabled) {
    			if ((System.currentTimeMillis() - LAST_LOADING_TIME) / ONEDAY
    					>= MIN_TIME_ON_SERVER)
    			{
    				loadSubmissions();
    				LAST_LOADING_TIME = System.currentTimeMillis();
    			}
   				for (int i = submissions.size()-1; i>=0 && manager_enabled; i--) {
   					AccessStructure struct = (AccessStructure) submissions.get(i);

   					if (isErasable(struct))
    				{	
    					manageDeletion(struct);
    					submissions.remove(i);
    				}
    			}
    			try {
    				Thread.sleep(SLEEP_TIME);
    			} catch (InterruptedException e) {
    				if(manager_enabled) e.printStackTrace();
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	System.out.println("MemoryManager stopped!");
    }

    public boolean isManager_enabled() {
        return manager_enabled;
    }
    
    /**
     * @param manager_enabled New MemoryManager state. Must be set to true
     *            before starting the memory manager.
     */
    public synchronized void setManager_enabled(boolean manager_enabled) {
        this.manager_enabled = manager_enabled;
    }
}
