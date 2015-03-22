package jplagWebService.serverImpl;

/**
 * @author Emeric Kwemou, Moritz Kroll
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;

import jplagWebService.server.FinishRequestData;
import jplagWebService.server.JPlagException;
import jplagWebService.server.JPlagTyp;
import jplagWebService.server.LanguageInfo;
import jplagWebService.server.MailTemplateArray;
import jplagWebService.server.NotifyDevelopersParams;
import jplagWebService.server.RequestData;
import jplagWebService.server.RequestDataArray;
import jplagWebService.server.ServerInfo;
import jplagWebService.server.SetDeveloperStateParams;
import jplagWebService.server.SetMailTemplateParams;
import jplagWebService.server.SetUserDataParams;
import jplagWebService.server.StartResultDownloadData;
import jplagWebService.server.StartSubmissionUploadParams;
import jplagWebService.server.Status;
import jplagWebService.server.UpdateUserInfoParams;
import jplagWebService.server.UserDataArray;
import jplagWebService.serverAccess.AccessStructure;
import jplagWebService.serverAccess.JPlagServerAccessHandler;
import jplagWebService.serverAccess.StatusDecorator;
import jplagWebService.serverAccess.UserAdmin;

public class JPlagTypImpl implements JPlagTyp, Remote, ServiceLifecycle {
	private ServletEndpointContext servletEndpointContext = null;

	/**
	 * Singleton object. Will be initialized in the init() method
	 */
	private static JPlagCentral JPLAG_CENTRAL = null;

	/**
	 * Minimal time between two getStatus request of the same user.
	 */
	private static final long MINIMAL_DIFF_TIME = 10000;

	private static final String[] javasuffixes = { ".java", ".jav", ".JAVA", ".JAV" };
	private static final String[] schemesuffixes = { ".scm", ".SCM" };
	private static final String[] csuffixes = { ".cpp", ".CPP", ".c++", ".C++", ".c", ".C", ".h", ".H", ".hpp", ".HPP" };
	private static final String[] textsuffixes = { ".txt", ".TXT", ".asc", ".ASC", ".tex", ".TEX" };
	private static final String[] cssuffixes = { ".cs", ".CS" };// @formatter:off
	
	public static final LanguageInfo[] languageInfos = {
//		new LanguageInfo("java12", javasuffixes, 9),
		new LanguageInfo("java15", javasuffixes, 8),
		new LanguageInfo("java15dm", javasuffixes, 8),
		new LanguageInfo("java17", javasuffixes, 8),
		new LanguageInfo("scheme", schemesuffixes, 13),
		new LanguageInfo("c/c++", csuffixes, 12),
		new LanguageInfo("text", textsuffixes, 5),
//		new LanguageInfo("char", textsuffixes, 10),
		new LanguageInfo("c#-1.2", cssuffixes, 8) }; // @formatter:on

	public static final String[] countryLanguages = { "en", "de", "fr", "es" };

	/**
	 * Initializes the web service and starts the main thread
	 */
	public void init(Object context) throws ServiceException {
		servletEndpointContext = (ServletEndpointContext) context;

		// ensure that entry and result directories exist
		AccessStructure.ensureExistence();

		// create and start jplagCentral thread
		JPLAG_CENTRAL = JPlagCentral.getInstance();
		JPLAG_CENTRAL.start();
	}

	/**
	 * Sets a stop flag for the main thread
	 */
	public void destroy() {
		servletEndpointContext = null;
		JPLAG_CENTRAL.stopCentral();
		JPLAG_CENTRAL = null;
	}

	private String getUsername() {
		SOAPMessageContext smc = (SOAPMessageContext) servletEndpointContext.getMessageContext();
		return JPlagServerAccessHandler.extractUsername(smc);
	}

	public ServerInfo getServerInfo() throws JPlagException, RemoteException {
		String username = getUsername();
		JPLAG_CENTRAL.getUserAdmin().updateLastUsage(username);
		return new ServerInfo(JPlagCentral.getUserInfo(username), languageInfos, countryLanguages, JPlagCentral.listSubmissions(username));
	}

	// TODO: Perhaps get rid of the step over AccessStructure to make it clearer
	public java.lang.String compareSource(jplagWebService.server.Option arguments, javax.mail.internet.MimeMultipart inputZipFile)
			throws JPlagException, RemoteException {
		String username = getUsername();
		JPlagCentral.checkQuota(username);
		JPLAG_CENTRAL.getUserAdmin().incrementSubmissionCounter(username);

		AccessStructure struct = new AccessStructure(username, arguments, inputZipFile);

		JPlagCentral.addToReadyQueue(struct);
		return struct.getSubmissionID();
	}

	public String startSubmissionUpload(StartSubmissionUploadParams params) throws JPlagException, RemoteException {
		if (params.getData().length < 1 || params.getData().length > 81920) {
			System.out.println("startSubmissionUpload: data.length=" + params.getData().length);
			throw new JPlagException("startSubmissionUploadException", "The size of the data array is invalid! Must be non empty and"
					+ " 80 kB at maximum!", "Create correct data parts!");
		}
		String username = getUsername();
		JPlagCentral.checkQuota(username);
		JPLAG_CENTRAL.getUserAdmin().incrementSubmissionCounter(username);

		AccessStructure struct = new AccessStructure(username, params.getSubmissionParams());

		struct.getDecorator().setState(StatusDecorator.UPLOADING);

		JPLAG_CENTRAL.getTransferManager().startUpload(struct, params.getFilesize(), params.getData());

		return struct.getSubmissionID();
	}

	public int continueSubmissionUpload(byte[] data) throws JPlagException, RemoteException {
		String username = getUsername();
		if (data.length < 1 || data.length > 81920) {
			System.out.println("startSubmissionUpload: data.length=" + data.length);
			JPLAG_CENTRAL.getTransferManager().cancelUpload(username, null);
			throw new JPlagException("continueSubmissionUploadException", "The size of the data array is invalid! Must be non empty and"
					+ " 80 kB at maximum!", "Create correct data parts!");
		}
		JPLAG_CENTRAL.getTransferManager().writeNextPart(username, data);
		return 0;
	}

	/**
	 * @param submissionID
	 *            : Used to retrieve the submission of this user.
	 * @return Status object, containing all information about the state of
	 *         operation on the submission with ID 'submissionID'
	 * @throws jplag.server.JPlagException
	 *             this exception occurs when a submissionID is not a valid one
	 * @throws java.rmi.RemoteException
	 */
	public Status getStatus(java.lang.String submissionID) throws JPlagException, RemoteException {
		String username = getUsername();

		AccessStructure struct = JPlagCentral.search(JPlagCentral.ALLQUEUES, submissionID); // Search on all queues

		/**
		 * Here we want to solve any getStatus flooding problem. In fact, this
		 * problem is very difficult to solve because an user could send
		 * getStatus request through different Threads
		 */

		if (struct == null || !struct.getUsername().equals(username)) {
			/**
			 * In this situation, the access is denied or the submission does
			 * not exist. JPlagTyp_Impl just waits 10 second and then sends the
			 * exception or error message to the user.
			 */

			try {
				Thread.sleep(MINIMAL_DIFF_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (struct != null) {
				System.out.println("Wrong username (" + username + ") for " + "submission ID " + submissionID);
			}
			throw new JPlagException("statusException", "Submission does not exist!", "Please check submission ID " + "and username!");
		} else {
			/**
			 * The submission exists for this user, so check whether enough time
			 * has elapsed since the last status request
			 */
			long difference_time = System.currentTimeMillis() - struct.getLastStatusRequest();
			try {
				if (difference_time < MINIMAL_DIFF_TIME)
					Thread.sleep(MINIMAL_DIFF_TIME - difference_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			struct.setLastStatusRequest();
			return struct.getDecorator().getStatus();
		}
	}

	public javax.mail.internet.MimeMultipart getResult(String submissionID) throws JPlagException, RemoteException {
		String username = getUsername();

		// Search on terminatedQueue
		AccessStructure struct = JPlagCentral.search(JPlagCentral.TERMINATEDQUEUE, submissionID);
		if (struct == null || !struct.getUsername().equals(username)) {
			throw new JPlagException("resultException", "Submission does not exist!", "Please check submission ID " + "and username!");
		}
		Status status = struct.getDecorator().getStatus();

		if (status == null) {
			throw new JPlagException("resultException", "Unable to retrieve status for submission!",
					"Please check submission ID and username!");
		} else if (status.getState() >= 400) {
			throw new JPlagException("resultException", "Submission has been completed with errors! " + "Results not available!",
					"You need to remove the errors and submit again!");
		} else if (status.getState() < 300) {
			throw new JPlagException("resultException", "Submission has not been completed yet!",
					"Please check submission ID or try again " + "later!");
		} else {
			// Compare source finished without an error
			return struct.getResult();
		}
	}

	public StartResultDownloadData startResultDownload(String submissionID) throws JPlagException, RemoteException {
		String username = getUsername();

		// Search on terminatedQueue
		AccessStructure struct = JPlagCentral.search(JPlagCentral.TERMINATEDQUEUE, submissionID);
		if (struct == null || !struct.getUsername().equals(username)) {
			throw new JPlagException("resultException", "Submission does not exist!", "Please check submission ID " + "and username!");
		}
		Status status = struct.getDecorator().getStatus();

		if (status == null) {
			throw new JPlagException("resultException", "Unable to retrieve status for submission!",
					"Please check submission ID and username!");
		} else if (status.getState() >= 400) {
			throw new JPlagException("resultException", "Submission has been completed with errors! " + "Results not available!",
					"You need to remove the errors and submit again!");
		} else if (status.getState() < 300) {
			throw new JPlagException("resultException", "Submission has not been completed yet!",
					"Please check submission ID or try again " + "later!");
		} else {
			// Compare source finished without an error
			return JPLAG_CENTRAL.getTransferManager().startDownload(struct);
		}
	}

	public byte[] continueResultDownload(int dummy) throws JPlagException, RemoteException {
		String username = getUsername();
		return JPLAG_CENTRAL.getTransferManager().readNextPart(username);
	}

	public int cancelSubmission(String submissionID) throws JPlagException, RemoteException {
		String username = getUsername();

		AccessStructure struct = JPlagCentral.search(JPlagCentral.ALLQUEUES, submissionID);
		if (struct == null || !struct.getUsername().equals(username)) {
			if (struct == null) {
				// not in entry or result list? check up- and downloads
				if (JPLAG_CENTRAL.getTransferManager().cancelTransfer(username, submissionID))
					return 0;
				return 0;
			}
			throw new JPlagException("cancelException", "Submission does not exist!", "Please check submission ID " + "and username!");
		}
		Status status = struct.getDecorator().getStatus();

		if (status == null) {
			throw new JPlagException("cancelException", "Unable to retrieve status for submission!",
					"Please check submission ID and username!");
		}
		System.out.println("[" + new Date() + "] User " + username + " cancels submission " + struct.getSubmissionID());
		JPlagCentral.cancelSubmission(struct);
		return 0;
	}

	private void logInvalidAccessAndThrow(String methodName, String user) throws JPlagException {
		// log invalid access, wait a bit and throw an exception
		System.out.println(methodName + ": " + user + " tried to access this function on " + new Date());
		try {
			Thread.sleep(10);
		} catch (Exception ex) {
		}
		throw new JPlagException(methodName, "You don't have the rights to access this function!",
				"Your username has been logged. Go away!");
	}

	/**
	 * Checks whether the current user has enough rights (mask parameter) to
	 * access the function with the given name
	 * 
	 * @throws JPlagException
	 *             when the user doesn't have those rights. The access will also
	 *             be logged and the user has to wait 10 seconds.
	 */
	private int assertUser(String methodName, int mask) throws JPlagException {
		String user = getUsername();
		int userstate = JPLAG_CENTRAL.getUserAdmin().getState(user);

		if ((userstate & mask) == 0)
			logInvalidAccessAndThrow(methodName, user);

		return userstate;
	}

	public UserDataArray getUserDataArray(int dummy) throws JPlagException, RemoteException {
		int state = assertUser("getUserDataArray", UserAdmin.MASK_ANYADMIN);
		return JPLAG_CENTRAL.getUserAdmin().getUserDataArray(getUsername(), (state & UserAdmin.MASK_JPLAGADMIN) == 0);
	}

	public int setUserData(SetUserDataParams params) throws JPlagException, RemoteException {
		int state = assertUser("setUserData", UserAdmin.MASK_ANYADMIN);
		JPLAG_CENTRAL.getUserAdmin().setUserData(params.getUserdata(), params.getOldUsername(), getUsername(),
				(state & UserAdmin.MASK_JPLAGADMIN) == 0);
		return 0;
	}

	public int updateUserInfo(UpdateUserInfoParams params) throws JPlagException, RemoteException {
		JPLAG_CENTRAL.getUserAdmin().updateUserInfo(getUsername(), params.getNewPassword(), params.getNewEmailSecond(),
				params.getNewHomepage());
		return 0;
	}

	public boolean requestAccount(RequestData data) throws JPlagException, RemoteException {
		assertUser("requestAccount", UserAdmin.USER_SERVERPAGE);
		return JPLAG_CENTRAL.getUserAdmin().requestAccount(data);
	}

	public RequestDataArray getAccountRequests(boolean lengthOnly) throws JPlagException, RemoteException {
		assertUser("getAccountRequests", UserAdmin.MASK_ANYADMIN);
		return new RequestDataArray(JPLAG_CENTRAL.getUserAdmin().getRequestAdmin().getRequests(lengthOnly));
	}

	public int finishAccountRequest(FinishRequestData finishData) throws JPlagException, RemoteException {
		assertUser("finishAccountRequest", UserAdmin.MASK_JPLAGADMIN);
		JPLAG_CENTRAL.getUserAdmin().finishAccountRequest(finishData, getUsername());
		return 0;
	}

	public int extendAccount(String extendCode) throws JPlagException, RemoteException {
		assertUser("extendAccount", UserAdmin.USER_SERVERPAGE);
		JPLAG_CENTRAL.getUserAdmin().extendAccount(extendCode);
		return 0;
	}

	public MailTemplateArray getMailTemplates(int type) throws JPlagException, RemoteException {
		assertUser("getMailTemplates", UserAdmin.MASK_JPLAGADMIN);
		return new MailTemplateArray(JPLAG_CENTRAL.getMailTemplateAdmin().getMailTemplates(type));
	}

	public int setMailTemplate(SetMailTemplateParams params) throws JPlagException, RemoteException {
		assertUser("setMailTemplate", UserAdmin.MASK_JPLAGADMIN);
		JPLAG_CENTRAL.getMailTemplateAdmin().setMailTemplate(params.getType(), params.getTemplate());
		return 0;
	}

	public int notifyDevelopers(NotifyDevelopersParams params) throws JPlagException, RemoteException {
		assertUser("notifyDevelopers", UserAdmin.MASK_JPLAGADMIN);
		JPLAG_CENTRAL.getUserAdmin().notifyDevelopers(params.getSubject(), params.getMessage());
		return 0;
	}

	public int setDeveloperState(SetDeveloperStateParams params) throws JPlagException, RemoteException {
		assertUser("setDeveloperState", UserAdmin.USER_SERVERPAGE);

		int curstate = JPLAG_CENTRAL.getUserAdmin().getLoginState(params.getUsername(), params.getPassword());
		if ((curstate & UserAdmin.MASK_EXPIRED) != 0) {
			throw new JPlagException("setDeveloperState", "Access denied!",
					"Your account has expired! Please contact the JPlag administrator to reactivate it!");
		} else if ((curstate & UserAdmin.MASK_DEACTIVATED) != 0) {
			throw new JPlagException("setDeveloperState", "Access denied!",
					"Your account has been deactivated! Please contact the JPlag administrator to reactivate it!");
		} else if (curstate == UserAdmin.USER_INVALID) {
			throw new JPlagException("setDeveloperState", "Access denied!", "Wrong username or password!");
		}
		JPLAG_CENTRAL.getUserAdmin().setDeveloperState(params.getUsername(), params.isDeveloper());
		return 0;
	}
}
