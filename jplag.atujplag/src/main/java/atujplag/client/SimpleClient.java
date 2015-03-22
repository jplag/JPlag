package atujplag.client;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

import jplagWsClient.jplagClient.JPlagException;
import jplagWsClient.jplagClient.JPlagService_Impl;
import jplagWsClient.jplagClient.JPlagTyp_Stub;
import jplagWsClient.jplagClient.Option;
import jplagWsClient.jplagClient.ServerInfo;
import jplagWsClient.jplagClient.StartSubmissionUploadParams;
import jplagWsClient.jplagClient.Status;
import jplagWsClient.jplagClient.UpdateUserInfoParams;
import jplagWsClient.util.JPlagClientAccessHandler;
import atujplag.ATUJPLAG;
import atujplag.util.Messages;
import atujplag.util.SubmissionManager;
import atujplag.util.TagParser;
import atujplag.util.ZipUtil;
import atujplag.view.JPlagCreator;

/**
 * @author Emeric Kwemou 19-03-05
 *  
 */
public class SimpleClient extends Client {
	private Vector<SubmissionManager> submissions = null;
    
	/**
	 * @param clientName
	 *            the name given to the project to be compared with jplag
	 */
	public SimpleClient(String clientName, ATUJPLAG atujplag) {
		super(clientName, atujplag);
		this.status = new Status(-10, 0, ""); //$NON-NLS-1$
	}

	public SimpleClient(ATUJPLAG atujplag) {
		super(atujplag);
        getJPlagStub();
        if(accessHandler == null) {
            System.out.println("Didn't find access handler!"); //$NON-NLS-1$
            System.exit(2);     // TODO: hmmm.... exit?
        }
	}
	
    protected JPlagTyp_Stub getJPlagStub() {
        if (stub == null) {
            stub = (JPlagTyp_Stub) (new JPlagService_Impl()
                    .getJPlagServicePort());

            HandlerChain handlerchain = stub._getHandlerChain();
			@SuppressWarnings("unchecked")
			Iterator<Handler> handlers = handlerchain.iterator();
            while (handlers.hasNext()) {
				Handler handler = handlers.next();
                if (handler instanceof JPlagClientAccessHandler) {
                    accessHandler = ((JPlagClientAccessHandler) handler);
                    break;
                }
            }
        }
        if (accessHandler != null) {
            accessHandler.setUserPassObjects(atujplag.getUsername(),
                    atujplag.getPassword());
        }
        return stub;
    }

    public boolean compareSource() {
		if (isForceStop())
			return false;
		
        this.setState(Messages.getString("SimpleClient.Packing_files"), //$NON-NLS-1$
            "", 0,Client.PACKING, true); //$NON-NLS-1$

        Vector<File> submissionFiles = collectValidSubmission();
        
		if (isForceStop() || stub == null && getJPlagStub() == null) {
			return false;
		}
		
		// Preparing file
		File zippedfile = null;
		try {
			zippedfile = new File(atujplag.getResultLocation()
				+ File.separator + "atujplag_tmp_zipped_entry_" //$NON-NLS-1$
				+ this.clientName + ".zip"); //$NON-NLS-1$
			
            ZipUtil.zipFilesTo(submissionFiles, submissionDirectory, zippedfile);

            if (isForceStop()) {
                zippedfile.delete();
                return false;
            }
            
            FileInputStream input = new FileInputStream(zippedfile);
            
            int filesize = (int) zippedfile.length();
            
            setState(Messages.getString(
                "SimpleClient.Sending_files_to_JPlag_server"), //$NON-NLS-1$
                "(0 kB / " + (filesize+1023)/1024 //$NON-NLS-1$
                + " kB)", 0, Client.SENDING, true); //$NON-NLS-1$
            
            int sentsize = 0;
            int partsize = (filesize<81920) ? filesize : 81920;
            byte[] data = new byte[partsize];
            input.read(data);
            
            this.internalState = Client.SENDING;
            submissionID = stub.startSubmissionUpload(
                new StartSubmissionUploadParams(options, filesize, data));
            sentsize += partsize;
            while(sentsize<filesize-partsize && !isForceStop()) {
                setTransferProgress(sentsize, filesize);
                input.read(data);
                stub.continueSubmissionUpload(data);
                sentsize += partsize;
            }
            if(sentsize!=filesize && !isForceStop()) {   // transfer last part
                setTransferProgress(sentsize, filesize);
                data = new byte[filesize-sentsize];
                input.read(data);
                stub.continueSubmissionUpload(data);
            }
            input.close();
            setTransferProgress(filesize, filesize);
			
			zippedfile.delete();
			
			System.gc();
			if(isForceStop()) {
				return false;
			}
			this.setState(Messages.getString(
                "SimpleClient.Sending_files_to_JPlag_server_OK"), //$NON-NLS-1$
				"", 100, Client.SENDING, true); //$NON-NLS-1$
		} catch (JPlagException e) {
			this.setState(TagParser.parse(Messages.getString(
                    "SimpleClient.Sending_files_{1_DESC}_ERROR"), //$NON-NLS-1$
					new String[]{e.getDescription() + "\n" + e.getRepair()}), //$NON-NLS-1$
				e.getMessage(), 100, internalState, false);
			
			if(zippedfile != null) zippedfile.delete();
			
			return false;
		} catch (Exception e) {
			this.setState(TagParser.parse(Messages.getString(
                    "SimpleClient.Sending_files_{1_DESC}_ERROR"), //$NON-NLS-1$
					new String[]{e.getMessage()}),
				TagParser.parse(Messages.getString(
                    "SimpleClient.Caused_by_{1_CAUSE}"), //$NON-NLS-1$
					new String[]{e.getCause()==null ? Messages.getString(
						"SimpleClient.UNKNOWN") : e.getCause().toString()}), //$NON-NLS-1$
				100, internalState, false);
            
            e.printStackTrace();
			
			if(zippedfile != null) zippedfile.delete();
			
			return false;
		}
		return true;
	}

	public boolean getResult() {
		return execGetResult();
	}

	public static boolean updateUserInfo(ATUJPLAG atujplag,
			UpdateUserInfoParams params, Component parent) {
		try {
			SimpleClient tmp = new SimpleClient(atujplag);
			if (tmp.stub != null) {
				tmp.stub.updateUserInfo(params);
                return true;
            }
            return false;
		} catch (JPlagException e) {
			JPlagCreator.showError(parent,
				Messages.getString("SimpleClient.JPlag_exception"), //$NON-NLS-1$
				e.getDescription() + "\n" + e.getRepair()); //$NON-NLS-1$

			e.printStackTrace();
			return false;
		} catch (Exception e) {
			JPlagCreator.showError(parent,
				Messages.getString("SimpleClient.JPlag_error"), //$NON-NLS-1$
				e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static ServerInfo serverInfo(ATUJPLAG atujplag, Component parent) {
		SimpleClient tmp = new SimpleClient(atujplag);
		try {
			if (tmp.stub != null)
				return tmp.stub.getServerInfo();
			//TODO To be implemented in next version (??)
			else {
				JPlagCreator.showError(parent,
                    Messages.getString("SimpleClient.JPlag_error"), //$NON-NLS-1$
					Messages.getString(
                        "SimpleClient.Client_stub_not_initialized")); //$NON-NLS-1$
				return null;
			}
		} catch (JPlagException e) {
			JPlagCreator.showError(parent, 
                Messages.getString("SimpleClient.JPlag_error"), //$NON-NLS-1$
				TagParser.parse(Messages.getString(
					"SimpleClient.JPlag_server_access_{1_DESC}_{2_DETAIL}_ERROR"), //$NON-NLS-1$
					new String[]{e.getDescription(), e.getRepair()}));
			return null;
		} catch (Exception e) {
			JPlagCreator.showError(parent, Messages.getString(
                "SimpleClient.JPlag_error"), e.getMessage()); //$NON-NLS-1$
			e.printStackTrace();
			return null;
		}
	}

	public boolean cancelSubmission(Component parent) {
		this.stub = getJPlagStub();
		return super.cancelSubmission(parent);
	}
    
    private Vector<File> collectValidSubmission() {
        Vector<File> fileVector = new Vector<File>();
        for(int i=0; i<submissions.size(); i++) {
            SubmissionManager sub = submissions.get(i);
            if (sub.isValid())
                sub.collectFiles(fileVector);
        }
        return fileVector;
    }

	public void setSubmissions(Vector<SubmissionManager> submissions) {
		this.submissions = submissions;
	}
    
    public static String startSubmissionUpload(ATUJPLAG atujplag, Option params,
            int filesize, byte[] data) {
        SimpleClient client = new SimpleClient(atujplag);
        try {
            String str = client.stub.startSubmissionUpload(
                new StartSubmissionUploadParams(params,filesize,data));
            return str;
        }
        catch(JPlagException e) {
            return "JPlagException: "+e.getMessage()+"\n"+e.getRepair();
        }
        catch(Exception e) {
            e.printStackTrace();
            return "Exception: "+e.getMessage();
        }
    }
}