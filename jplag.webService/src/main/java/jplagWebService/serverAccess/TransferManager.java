package jplagWebService.serverAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import jplagWebService.server.JPlagException;
import jplagWebService.server.StartResultDownloadData;
import jplagWebService.serverImpl.JPlagCentral;

public class TransferManager {
    private DelayQueue<TransferObject> uploadTimeoutQueue =
        new DelayQueue<TransferObject>();
    private DelayQueue<TransferObject> downloadTimeoutQueue =
        new DelayQueue<TransferObject>();
    
    private HashMap<String,TransferObject> userToUploadMap
        = new HashMap<String,TransferObject>();
    private HashMap<String,TransferObject> userToDownloadMap
    = new HashMap<String,TransferObject>();

    private boolean doStopTransferManager = false;
        
    private class TransferObject implements Delayed {
        private AccessStructure struct;
        private long timeoutTime;
        private File file;
        private int filesize;
        private int remainingBytes;
        
        /**
         * timeout is given in seconds
         */
        TransferObject(AccessStructure struct, File file, int filesize,
                long timeout) {
            this.struct = struct;
            this.file = file;
            this.filesize = filesize;
            remainingBytes = filesize;
            setTimeout(timeout);
        }

        public long getDelay(TimeUnit unit) {
            long n = timeoutTime - System.nanoTime();
            return unit.convert(n, TimeUnit.NANOSECONDS);
        }

        public int compareTo(Delayed o) {
            if(timeoutTime < ((TransferObject)o).timeoutTime)
                return -1;
            else if(timeoutTime > ((TransferObject)o).timeoutTime)
                return 1;
            else
                return 0;
        }
        
        public AccessStructure getStruct() {
            return struct;
        }
        
        public File getFile() {
            return file;
        }
        
        public int getRemainingBytes() {
            return remainingBytes;
        }
        
        /**
         * Writes the next part of the related file
         * @returns true, if this was the last part
         * @throws JPlagException If there is more data, than expected or
         *    writing to the file doesn't work
         */
        public boolean writeNextPart(byte[] data) throws JPlagException {
            if(remainingBytes<data.length) {
                throw new JPlagException("uploadException", "More data sent " +
                        "than expected!","");
            }
            try {
                FileOutputStream out = new FileOutputStream(file, true);
                out.write(data);
                out.close();
                remainingBytes -= data.length;
            }
            catch(IOException e) {
                e.printStackTrace();
                throw new JPlagException("uploadException", "Unable to save " +
                        "submission part on the server!",
                        "Server out of disk space??");
            }
            return remainingBytes==0;
        }
        
        /**
         * Reads the next part of the related file
         * @return an array containing the next part with the correct size
         * @throws JPlagException If the download has already been completed
         *    or there was an I/O error
         */
        public byte[] readNextPart() throws JPlagException {
            if(remainingBytes==0) {
                throw new JPlagException("downloadException",
                    "There's nothing left to be downloaded!","");
            }
            try {
                FileInputStream in = new FileInputStream(file);
                in.skip(filesize-remainingBytes);
                
                int partsize = remainingBytes;
                if(partsize>81920) partsize = 81920;
                
                byte[] data = new byte[partsize];
                in.read(data);
                in.close();
                remainingBytes -= partsize;
                return data;
            }
            catch(IOException e) {
                e.printStackTrace();
                throw new JPlagException("downloadException", "Unable to read" +
                        " submission part from server!","");
            }
        }
        
        public void setTimeout(long timeout) {
            timeoutTime = System.nanoTime() + timeout * 1000000000;
        }
    }
    
    /**
     * Checks, whether the user is already uploading a submission, and if not,
     * saves the first part of the entry file onto disk. If there are more
     * parts, the user becomes marked as uploading and the upload is added to
     * the upload timeout queue
     * @param struct AccessStructure for the according submission
     * @param filesize Total size of the zip file containing the submission
     * @param data First 80 kB part (or less, if file is less than 80 kB)
     * @throws JPlagException If user is already uploading, the file
     *      already exists or saving didn't work
     */
    public synchronized void startUpload(AccessStructure struct,
            int filesize, byte[] data) throws JPlagException
    {
        if(userToUploadMap.containsKey(struct.getUsername())) {
            throw new JPlagException("uploadException", "You are already " +
                    "uploading a submission!", "Only one submission may be " +
                    "uploaded by one user at a time");
        }
        File file = new File(struct.getEntryPath());
        
        if(file.exists()) {
            throw new JPlagException("uploadException", "File already exist!?",
                "Unable to create new file. Please tell the admins " +
                "about this!");
        }
        
        TransferObject obj = new TransferObject(struct, file, filesize, 120);
        if(!obj.writeNextPart(data)) {
            userToUploadMap.put(struct.getUsername(), obj);
            uploadTimeoutQueue.add(obj);
        }
        else JPlagCentral.addToReadyQueue(struct);
    }
    
    /**
     * Writes the next part of the upload identified by the username.
     * If it was the last part it unmarks the user and adds the submission
     * to the ready queue
     * @param username Username of user uploading the submission
     * @param data Next 80 kB part (or less if it is the last part)
     * @throws JPlagException If saving didn't work or upload doesn't exist
     */
    public synchronized void writeNextPart(String username, byte[] data)
            throws JPlagException
    {
        TransferObject obj = userToUploadMap.get(username);
        if(obj == null) {
            throw new JPlagException("uploadException", "No upload started " +
                    "or upload timed out!", "Restart the upload");
        }
        uploadTimeoutQueue.remove(obj);
        if(!obj.writeNextPart(data)) {
            obj.setTimeout(120);
            uploadTimeoutQueue.add(obj);
        }
        else {
            userToUploadMap.remove(username);
            JPlagCentral.addToReadyQueue(obj.getStruct());
        }
    }
    
    /**
     * Cancels the upload with the given submissionID (if provided) for the
     * given user.
     * If the user doesn't have an upload or the submissionID doesn't match,
     * false is returned.
     */
    public synchronized boolean cancelUpload(String username,
            String submissionID)
    {
        TransferObject obj = userToUploadMap.get(username);
        if(obj == null || submissionID!=null && 
                !obj.getStruct().getSubmissionID().equals(submissionID)) {
            return false;
        }
        uploadTimeoutQueue.remove(obj);
        userToUploadMap.remove(obj.getStruct().getUsername());
        obj.getFile().delete();
        return true;
    }

    /**
     * Checks, whether the user is already downloading a submission, and if not,
     * loads the first part of the result file from disk. If there are more
     * parts, the user becomes marked as downloading and the download is added
     * to the download timeout queue
     * @param struct AccessStructure for the according submission
     * @return The filesize and the first part of the entry file
     * @throws JPlagException If user is already downloading, the file
     *      doesn't exists or loading didn't work
     */
    public synchronized StartResultDownloadData startDownload(
            AccessStructure struct) throws JPlagException
    {
        if(userToDownloadMap.containsKey(struct.getUsername())) {
            throw new JPlagException("downloadException", "You are already " +
                    "downloading a submission!", "Only one submission may be " +
                    "downloaded by one user at a time");
        }
        File file = new File(struct.getResultPath());
        
        if(!file.exists()) {
            System.out.println("startDownload: \"" + struct.getResultPath()
                + "\" doesn't exist!");
            throw new JPlagException("downloadException",
                "File doesn't exist!?", "Unable to find result file!");
        }
        
        int filesize = (int) file.length();
        
        TransferObject obj = new TransferObject(struct, file, filesize, 120);
        byte[] data = obj.readNextPart();
        if(obj.getRemainingBytes()>0) {
            userToDownloadMap.put(struct.getUsername(), obj);
            downloadTimeoutQueue.add(obj);
        }
        else JPlagCentral.cancelSubmission(struct);
        return new StartResultDownloadData(filesize, data);
    }

    /**
     * Reads the next part of the download identified by the username.
     * If it was the last part it unmarks the user and deletes the entry file
     * @param username Username of user downloading the result
     * @return data Next 80 kB part (or less if it is the last part)
     * @throws JPlagException If loading didn't work or download doesn't exist
     */
    public synchronized byte[] readNextPart(String username)
            throws JPlagException
    {
        TransferObject obj = userToDownloadMap.get(username);
        if(obj == null) {
            throw new JPlagException("downloadException",
                "No download started or download timed out!",
                "Restart the download");
        }
        downloadTimeoutQueue.remove(obj);
        byte[] data = obj.readNextPart();
        if(obj.getRemainingBytes()!=0) {
            obj.setTimeout(120);
            downloadTimeoutQueue.add(obj);
        }
        else {
            userToDownloadMap.remove(username);
            JPlagCentral.cancelSubmission(obj.getStruct());
        }
        return data;
    }

    /**
     * Cancels the download with the given submissionID (if provided) for the
     * given user and deletes the all files related to the submission.
     * If the user doesn't have a download or the submissionID doesn't match,
     * false is returned.
     */
    public synchronized boolean cancelDownload(String username,
            String submissionID)
    {
        TransferObject obj = userToDownloadMap.get(username);
        if(obj == null || submissionID!=null && 
                !obj.getStruct().getSubmissionID().equals(submissionID)) {
            return false;
        }
        downloadTimeoutQueue.remove(obj);
        userToDownloadMap.remove(obj.getStruct().getUsername());
        JPlagCentral.cancelSubmission(obj.getStruct());
        return true;
    }
    
    /**
     * Cancels a transfer for the user with the given submissionID if existing.
     * @return true, if the transfer got cancelled
     */
    public boolean cancelTransfer(String username, String submissionID) {
        if(cancelUpload(username, submissionID)) return true;
        return cancelDownload(username, submissionID);
    }

    public void stopTransferManager() {
        doStopTransferManager = true;
    }
    
    /**
     * Waits for the next uploaded transfer object to timeout and removes it
     */
    private Thread uploadThread = new Thread() {
        public void run() {
            try {
                while(!doStopTransferManager) {
                    TransferObject obj = null;
                    try {
                        obj = uploadTimeoutQueue.take();
                    }
                    catch(InterruptedException ex) {
                        continue;
                    }
                    // Upload timed out, so delete already uploaded part
                    synchronized(this) {
                        obj.getFile().delete();
                        userToUploadMap.remove(obj.getStruct().getUsername());
                    }
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Upload TransferManager stopped!");
        }
    };

    /**
     * Waits for the next downloaded transfer object to timeout and removes it
     */
    private Thread downloadThread = new Thread() {
        public void run() {
            try {
                while(!doStopTransferManager) {
                    TransferObject obj = null;
                    try {
                        obj = downloadTimeoutQueue.take();
                    }
                    catch(InterruptedException ex) {
                        continue;
                    }
                    // Download timed out, so cancel download
                    synchronized(this) {
                        userToDownloadMap.remove(obj.getStruct().getUsername());
                    }
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Download TransferManager stopped!");
        }
    };
    
    public void start() {
        uploadThread.start();
        downloadThread.start();
    }
    
    public boolean isAlive() {
        return uploadThread.isAlive() || downloadThread.isAlive();
    }
    
    public void interrupt() {
        uploadThread.interrupt();
        downloadThread.interrupt();
    }
}
