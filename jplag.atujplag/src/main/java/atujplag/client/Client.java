package atujplag.client;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jplagWsClient.jplagClient.JPlagException;
import jplagWsClient.jplagClient.JPlagTyp_Stub;
import jplagWsClient.jplagClient.Option;
import jplagWsClient.jplagClient.ServerInfo;
import jplagWsClient.jplagClient.StartResultDownloadData;
import jplagWsClient.jplagClient.Status;
import jplagWsClient.util.JPlagClientAccessHandler;
import atujplag.ATUJPLAG;
import atujplag.util.Messages;
import atujplag.util.TagParser;
import atujplag.util.ZipUtil;
import atujplag.view.InfoPanel;
import atujplag.view.JPlagCreator;

public abstract class Client {
	protected ServerInfo serverInfo = null;
	protected ATUJPLAG atujplag = null;

	/**
	 * START this are related to the internal state of the comparison in JPLAG
	 * SERVICE you will extend it with all other internal state of JPLAG SERVICE
	 * to have a better description of the comparison --Tchao Emeric
	 */
    public static final int SERVICE_UPLOADING = 0;
	public static final int SERVICE_WAITING_IN_QUEUE = 50;
	public static final int SERVICE_PARSING = 100;
    public static final int SERVICE_COMPARING = 200;
	public static final int SERVICE_COMPARE_SOURCE_DONE = 300;
	public static final int SERVICE_ERROR = 400;
	public static final int SERVICE_BAD_LANGUAGE_ERROR = 401;
	public static final int SERVICE_NOT_ENOUGH_SUBMISSIONS_ERROR = 402;
	public static final int SERVICE_BAD_PARAMETER = 403;
	public static final int SERVICE_BAD_SENSITIVITY_OF_COMPARISON = 404;

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	protected JPlagClientAccessHandler accessHandler = null;

    // TODO: Why is there an addition client name field inspite of Option.title?
	protected String clientName;

	protected String index_html = null;
    protected String index_html_enc = null;     // for URL encoded version

    protected InfoPanel gui = null;
    
	private int state = -1;
	private String message = ""; //$NON-NLS-1$
	private String details = ""; //$NON-NLS-1$
	private int progress = 0;
	private boolean errorOccurred = false;

	protected boolean is_for_general_purpose = false;

	protected Boolean modus = new Boolean(false);

	protected Option options = new Option();

	protected String parserlog;

	protected String submissionDirectory = null;

	protected Status status = null;

	protected JPlagTyp_Stub stub = null;

	protected String submissionID = null;

	protected boolean forceStop = false;

	public final static int ERROR_MESSAGE = -100;
	public static final int PACKING = 0;
	public static final int SENDING = 1;
	public static final int WAITING = 2;
	public static final int PARSING = 3;
	public static final int COMPARING = 4;
	public static final int LOADING = 5;
    public static final int END = 6;
    public static final int CANCELLING = 7;
    public static final int STOPPED = 8;

	protected int internalState = 0;

	public Client(String clientName, ATUJPLAG atujplag) {
		this.atujplag = atujplag;
		if (clientName.length() > 25) {
			String tmp = clientName.substring(0, 24);
			this.clientName = tmp;
		} else
			this.clientName = clientName;

		initSSL();
	}
	
	public Client(ATUJPLAG atujplag) {//String username, String password) {
		this.atujplag = atujplag;
		this.is_for_general_purpose = true;
		initSSL();
	}

	protected boolean execGetStatus() {
		if(isForceStop())
			return false;
		try {
			status = stub.getStatus(this.submissionID);
		} catch (JPlagException e) {
			e.printStackTrace();
			setState(TagParser.parse(
					Messages.getString("Client.Error_occurred_{1_DESC}"), //$NON-NLS-1$
					new String[] {e.getDescription()}),
				TagParser.parse(Messages.getString(
					"Client.Caused_by_{1_CAUSE}"), //$NON-NLS-1$
					new String[] {String.valueOf(e.getCause())}),
				0, internalState, false);

			return false;
		} catch (Exception e) {
			e.printStackTrace();
			setState(TagParser.parse(
					Messages.getString("Client.Error_occurred_{1_DESC}"), //$NON-NLS-1$
					new String[] {e.getMessage()}),
				TagParser.parse(
					Messages.getString("Client.Caused_by_{1_CAUSE}"), //$NON-NLS-1$
					new String[] {String.valueOf(e.getCause())}),
				0, internalState, false);

			return false;
		}
		if(isForceStop())
			return false;
        
        String stateStr = stateInterpreter();
		if(status.getState() < SERVICE_ERROR) {
			setState(stateStr, "", status.getProgress(), //$NON-NLS-1$
					internalState, true);
            return true;
        }
		else {
			setState(stateStr, "", 0, internalState, false); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Sends a zipped file and all necessary options to the JPlag web service
	 */
	public abstract boolean compareSource();

	public boolean cancelSubmission(Component parent) {
		try {
			if (this.getSubmissionID() != null) {
				this.stub.cancelSubmission(this.submissionID);
                return true;
            }
            return false;
		} catch (RemoteException e) {
			JPlagCreator.showError(parent,
				Messages.getString("Client.JPlag_remote_exception"), //$NON-NLS-1$
				e.getMessage());

			e.printStackTrace();
            return false;
		} catch (JPlagException e) {
			JPlagCreator.showError(parent,"JPlag exception", //$NON-NLS-1$
                e.getDescription() + "\n" + e.getRepair()); //$NON-NLS-1$

            return false;
		}
	}

	/**
	 * @return the status of this client without invoking the RPC getStatus
	 *  
	 */
	public Status get_getStatusResult() {
		//this.stateInterpreter();
		return this.status;
	}

	/**
	 * @return name of this clinet instance
	 */
	public String getClientName() {
		return clientName;
	}
    
    public String getIndex_html() {
        return index_html;
    }

    public String getEncodedIndex_html() {
        return index_html_enc;
    }

	protected abstract JPlagTyp_Stub getJPlagStub();

	protected boolean execGetResult() {
		File tmp = null;
		if (isForceStop())
			return false;
		setState(Messages.getString("Client.Loading_results"), //$NON-NLS-1$
                "(0 kB / ? kB)", 0, Client.LOADING, true); //$NON-NLS-1$

		try {
            // Extract part
            File resultdir = new File(atujplag.getResultLocation()
                    + File.separator + this.clientName);
           
            if (!resultdir.exists())
                resultdir.mkdir();
            else {
                //overwrite
                File[] files = resultdir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    ATUJPLAG.delete(files[i]);
                }
            }
            tmp = new File(atujplag.getResultLocation()+ File.separator
                    + this.clientName + ".zip"); //$NON-NLS-1$
            
            FileOutputStream output = new FileOutputStream(tmp);

            StartResultDownloadData srdd = stub.startResultDownload(
                submissionID);
            
            if(!isForceStop())
                output.write(srdd.getData());

            int filesize = srdd.getFilesize();
            int loadedsize = srdd.getData().length;
            
            setTransferProgress(loadedsize, filesize);
            
            while(loadedsize<filesize && !isForceStop()) {
                byte[] data = stub.continueResultDownload(0);
                output.write(data);
                loadedsize += data.length;
                setTransferProgress(loadedsize, filesize);
            }
            output.close();
            
            if(isForceStop()) {
                System.gc();
                tmp.delete();
                return false;
            }
            
			// unzip part
			File f = new File(atujplag.getResultLocation(), clientName);
            ZipUtil.unzip(tmp, f);
			System.gc();
			tmp.delete();	// TODO: Why does this only work after a gc?
            
			this.index_html = f.getPath() + File.separator + "index.html"; //$NON-NLS-1$
            this.index_html_enc = ATUJPLAG.encodePathForURL(
                        atujplag.getResultLocation()) + File.separator
                    + ATUJPLAG.encodeForURL(clientName) + File.separator
                    + "index.html"; //$NON-NLS-1$
			this.parserlog = f.getPath() + File.separator + "parser-log.txt"; //$NON-NLS-1$
            
			setState(Messages.getString("Client.Operation_successful") //$NON-NLS-1$
					+ "<br>" //$NON-NLS-1$
					+ Messages.getString("Client.Result_location") //$NON-NLS-1$
					+ ":<br><a href=\"" + this.index_html_enc //$NON-NLS-1$
					+ "\">" + this.index_html + "</a>", //$NON-NLS-1$ //$NON-NLS-2$
					"", 100, END, true); //$NON-NLS-1$
		} catch (JPlagException e) {
            Throwable cause = e.getCause();
			setState(TagParser.parse(
					Messages.getString("Client.Loading_results_error_{1_DESC}"), //$NON-NLS-1$
					new String[] {e.getDescription() + "\n" + e.getRepair()}), //$NON-NLS-1$
				TagParser.parse(
					Messages.getString("Client.Caused_by_{1_CAUSE}"), //$NON-NLS-1$
                    new String[] {(cause!=null)?cause.toString():"unknown"}),   
				100, internalState, false);
			return false;
		} catch (Exception e) {
            Throwable cause = e.getCause();
			setState(TagParser.parse(
					Messages.getString("Client.Loading_results_error_{1_DESC}"), //$NON-NLS-1$
					new String[] {e.getMessage()}),
				TagParser.parse(
					Messages.getString("Client.Caused_by_{1_CAUSE}"), //$NON-NLS-1$
					new String[] {(cause!=null)?cause.toString():"unknown"}),	
				100, internalState, false);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @return true if the operation was sucessfull
	 */
	public abstract boolean getResult();

	/**
	 * @return name of the submitted file
	 */
	public String getSubmissionDirectory() {
		return this.submissionDirectory;
	}

	/**
	 * client ask for the status of the current operation
	 */
	public boolean getStatus() {
		return execGetStatus();
	}

	/**
	 * @return submission ID of the client instance ;
	 */
	public String getSubmissionID() {
		return submissionID;
	}

	/**
	 * Author Moritz Kroll
	 *  
	 */
	private void initSSL() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param clientName
	 *            Client's name of the client
	 */
	public void setClientName(String clientName) {
		this.options.setTitle(clientName);
		this.clientName = clientName;
	}

	public String stateInterpreter() {
		if (this.status != null) {
			String war = ""; //$NON-NLS-1$
			int tmp = this.status.getState();
			if (tmp == SERVICE_UPLOADING) {
                war = "Uploading... shouldn't be visible though...";
                this.internalState = Client.SENDING;
            } else if (tmp == SERVICE_WAITING_IN_QUEUE) {
                war = TagParser.parse(Messages.getString(
                    "Client.Submission_is_waiting_in_queue_at_position_{1_POS}"), //$NON-NLS-1$
                    new String[] { (status.getProgress()+1)+"" });                    
                this.internalState = Client.WAITING;
            } else if (tmp == SERVICE_PARSING) {
                war = Messages.getString("Client.Submission_is_being_parsed"); //$NON-NLS-1$
                this.internalState = Client.PARSING;
            } else if (tmp > SERVICE_PARSING && tmp < SERVICE_COMPARE_SOURCE_DONE) {
				war = Messages.getString("Client.Submission_is_being_compared"); //$NON-NLS-1$
				this.internalState = Client.COMPARING;
                if(tmp>SERVICE_COMPARING) status.setProgress(100);
			} else if (tmp == SERVICE_COMPARE_SOURCE_DONE) {
				war = Messages.getString("Client.Comparing_source_done"); //$NON-NLS-1$
				this.internalState = Client.LOADING;
			} else if (tmp == SERVICE_ERROR) {
				war = Messages.getString("Client.Error_occured") + ":<br>" //$NON-NLS-1$ //$NON-NLS-2$
					+ this.status.getReport(); 
			} else if (tmp == SERVICE_BAD_LANGUAGE_ERROR) {
				war = Messages.getString("Client.Error") + ": " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ Messages.getString("Client.Bad_language") + "<br>"  //$NON-NLS-1$ //$NON-NLS-2$
					+ this.status.getReport(); 
				this.internalState = Client.PARSING;
			} else if (tmp == SERVICE_BAD_PARAMETER) {
				war = Messages.getString("Client.Error") + ": " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ Messages.getString("Client.Bad_parameter") + "<br>"  //$NON-NLS-1$ //$NON-NLS-2$ 
					+ this.status.getReport();
				this.internalState = Client.PARSING;
			} else if (tmp == SERVICE_BAD_SENSITIVITY_OF_COMPARISON) {
				war = Messages.getString("Client.Error") + ": " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ Messages.getString("Client.Bad_sensitivity_of_comparison")  //$NON-NLS-1$ //$NON-NLS-2$
					+ "<br>" + this.status.getReport(); //$NON-NLS-1$
				this.internalState = Client.PARSING;
			} else if (tmp == SERVICE_NOT_ENOUGH_SUBMISSIONS_ERROR) {
				war = Messages.getString("Client.Error") + ": " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ Messages.getString("Client.Not_enough_submissions")  //$NON-NLS-1$ //$NON-NLS-2$
					+ "<br>" + this.status.getReport(); //$NON-NLS-1$
				this.internalState = Client.PARSING;
			}
			return war;
		}
		return ""; //$NON-NLS-1$
	}

	public Option getOptions() {
		return this.options;
	}

	public void setSubmissionDirectory(String sourceName) {
		this.submissionDirectory = sourceName;
	}

	public String getParserlog() {
		return this.parserlog;
	}

	public boolean isForceStop() {
		return this.forceStop;
	}

	protected void setForceStop(boolean forceStop) {
		this.forceStop = forceStop;
	}
	
	public void forceStop() {
		forceStop = true;
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return this.details;
	}

	public void setTransferProgress(int transferred, int filesize) {
        details = "(" + (transferred+1023)/1024 //$NON-NLS-1$
            + " kB / " + (filesize+1023)/1024 //$NON-NLS-1$
            + " kB)"; //$NON-NLS-1$
        progress = transferred*100/filesize;
        gui.invokeSetTextAndProgress(message, details, progress);
	}

	public int getProgress() {
		return this.progress;
	}

	public void setProgress(int level) {
		this.progress = level;
		this.gui.invokeSetProgress(level);
	}

	public void setGui(InfoPanel gui) {
		this.gui = gui;
	}

	public synchronized void setState(String message, String details,
			int progress, int state, boolean noError) {
		if(errorOccurred)
		{
			// TODO: Remove this
			System.out.println("setState after error: message=\""+message+"\" details=\""+details+"\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		errorOccurred = !noError;
		this.state = state;
		this.details = details;
		this.progress = progress;
		this.message = message;
        
        if(gui != null)
            gui.updateStatus();
	}

	public boolean isErrorOccurred() {
		return errorOccurred;
	}
}