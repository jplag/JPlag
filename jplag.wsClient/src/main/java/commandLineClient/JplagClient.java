package commandLineClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

import jplagWsClient.jplagClient.JPlagException;
import jplagWsClient.jplagClient.JPlagService_Impl;
import jplagWsClient.jplagClient.JPlagTyp_Stub;
import jplagWsClient.jplagClient.LanguageInfo;
import jplagWsClient.jplagClient.Option;
import jplagWsClient.jplagClient.ServerInfo;
import jplagWsClient.jplagClient.Status;
import jplagWsClient.jplagClient.Submission;
import jplagWsClient.util.JPlagClientAccessHandler;

public class JplagClient {

	// Konstanten... eigentlich w�rs sinnvoll die nicht an DREI sondern
	// an nur EINEM Platz zu haben.... Sollte noch ge�ndert werden...

	public static final int WRITING_RESULTS = 3;
	public static final int BAD_USER_ID = -1;
	public static final int WAITING_IN_QUEUE = 0;
	public static final int PARSING_WITH_NO_WARNING = 100;
	public static final int PARSING_WITH_WARNING = 101;
	public static final int COMPARING = 200;
	public static final int ILLEGAL_SUFFIX_WARNING = 102;
	public static final int PACKAGING_RESULTS = 201;
	public static final int COMPARE_SOURCE_DONE = 300;
	public static final int UNKOWN_ERROR_OCCURED = 400;
	public static final int BAD_LANGUAGE_ERROR = 401;
	public static final int NO_ENOUGH_SUBMISSIONS_ERROR = 402;
	public static final int BAD_PARAMETER = 403;
	public static final int BAD_SENSITIVITY_OF_COMPARISON = 404;

	JPlagTyp_Stub stub = null;
	JPlagClientAccessHandler accessHandler;

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("jplan CLI needs two parameters: username and password!");
			System.exit(1);
		}

		try {
			JplagClient jpClient = new JplagClient();
			jpClient.stub = (JPlagTyp_Stub) (new JPlagService_Impl().getJPlagServicePort());

			HandlerChain handlerchain = jpClient.stub._getHandlerChain();
			@SuppressWarnings("rawtypes")
			Iterator handlers = handlerchain.iterator();
			while (handlers.hasNext()) {
				Handler handler = (Handler) handlers.next();
				if (handler instanceof JPlagClientAccessHandler) {
					jpClient.accessHandler = ((JPlagClientAccessHandler) handler);
					break;
				}
			}
			if (jpClient.accessHandler == null) {
				System.out.println("Unable to find access handler!");
			} else {
				jpClient.accessHandler.setUserPassObjects(args[0], args[1]);
			}

			ServerInfo serverInfo = jpClient.invoke_getServerInfo();
			String lstr = "";
			LanguageInfo[] langs = serverInfo.getLanguageInfos();
			for (int i = 0; i < langs.length; i++) {
				lstr += "\nLanguage: " + langs[i].getName() + "\nSuffixes: ";
				String[] suf = langs[i].getSuffixes();
				for (int j = 0; j < suf.length; j++)
					lstr += suf[j] + ((j < suf.length - 1) ? "," : "");
				lstr += "\nDefault minimum token length: " + langs[i].getDefMinMatchLen() + "\n";
			}

			String str = "";
			Submission[] subs = serverInfo.getSubmissions();
			for (int i = 0; i < subs.length; i++) {
				str += "Submission ID: " + subs[i].getSubmissionID() + " Title: " + subs[i].getTitle() + " Date: " + subs[i].getDate()
						+ "\n";
			}

			System.out.println("Left submission slots: " + serverInfo.getUserInfo().getLeftSubmissionSlots());
			System.out.println("Account end date: " + serverInfo.getUserInfo().getExpires());
			System.out.println("List of languages:\n" + lstr + "\nList of submissions on server:\n" + str);

			if (args.length == 2) {
				String containerid = jpClient.invoke_compareSource(args);
				Status status;
				System.out.println("Waiting for server to be done...");
				do {
					Thread.sleep(4000);
					status = jpClient.stub.getStatus(containerid);
					System.out.println("State=" + status.getState() + " Progress=" + status.getProgress());
					//					System.out.flush();
				} while (status.getState() >= WAITING_IN_QUEUE && status.getState() < COMPARE_SOURCE_DONE);
				if (status.getState() == BAD_USER_ID) {
					System.out.println("ARGH! Where did my container go?!?      " + status.getReport());
				} else if (status.getState() == UNKOWN_ERROR_OCCURED || status.getState() == BAD_LANGUAGE_ERROR
						|| status.getState() == NO_ENOUGH_SUBMISSIONS_ERROR) {
					System.out.println("Error occurred: " + status.getReport());
				} else {
					jpClient.extractPart(jpClient.stub.getResult(containerid), args[1]);
				}
			}

		} catch (Exception ex) {
			if (ex instanceof JPlagException) {
				JPlagException jex = (JPlagException) ex;
				System.out.println(jex.getExceptionType());
				System.out.println(jex.getDescription());
				System.out.println(jex.getRepair());
			}
			ex.printStackTrace();
		}
	}

	public ServerInfo invoke_getServerInfo() {
		System.out.println("\n" + "############# JPLAG-RESULT :REQUEST = getServerInfo()  ##############" + "\n" + "\n");
		ServerInfo s = null;

		try {
			s = this.stub.getServerInfo();
		} catch (Exception e) {
			if (e instanceof JPlagException) {
				JPlagException jex = (JPlagException) e;
				System.out.println(jex.getExceptionType());
				System.out.println(jex.getDescription());
				System.out.println(jex.getRepair());
			}
			e.printStackTrace();
		}
		return s;

	}

	public String invoke_compareSource(String[] args) {
		// package a zipped file
		File file = new File(args[0]);
		//		MimeMultipart result=null;
		String result = null;

		System.out.println("Entering try block...");

		try {
			FileDataSource fds = new FileDataSource(file);

			// Construct a MimeBodyPart

			// Add Part on the .....

			MimeMultipart mmp = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setDataHandler(new DataHandler(fds));
			mbp.setFileName(file.getName());

			mmp.addBodyPart(mbp);

			System.out.println("Creating Option object...");

			// Prepare Options

			/*
			 * String username= "mo"; boolean use_root_dir =true ; String
			 * directories =""; int sensitivity_of_comparison =9 ; boolean
			 * use_basecode =false ; String basecode_dir =""; boolean
			 * read_subdirs =true ; boolean clustering_on =true ; String
			 * clustertype="max"; int store_matches = 30 ; int store_in_percent
			 * = 10 ; boolean exclude_file_on = false ; String excluded_file =
			 * ""; boolean include_file_on=false ; String included_file ="";
			 * boolean suffixes_on =true ; String suffixes = "java";
			 */
			String language = "java12";
			String title = "Server test";

			Option option = new Option(args[0],/* username, */
			null, 0, null, true, "avr", "50", null, null, language, title, "en");
			// running Jplag

			System.out.println("Calling compareSource...");

			result = this.stub.compareSource(option, mmp);
			System.out.println("\n" + "############# JPLAG-RESULT :REQUEST =compareSource()  ##############" + "\nResult= " + result);

		} catch (Exception ex) {
			if (ex instanceof JPlagException) {
				JPlagException jex = (JPlagException) ex;
				System.out.println(jex.getExceptionType());
				System.out.println(jex.getDescription());
				System.out.println(jex.getRepair());
			}
			ex.printStackTrace();
		}
		return result; //extractPart(result, args[1]);
	}

	private File extractPart(MimeMultipart inputZipFile, String path) {
		File result = new File(path + "/jplagResult.zip");
		try {
			if (inputZipFile == null)
				return null;
			MimeBodyPart bdp = (MimeBodyPart) inputZipFile.getBodyPart(0);
			System.out.println("Content Type  " + bdp.getContentType());

			DataHandler dh = bdp.getDataHandler();
			FileOutputStream os = new FileOutputStream(result);
			dh.writeTo(os);
			os.close();

		} catch (Exception e) {
			if (e instanceof JPlagException) {
				JPlagException jex = (JPlagException) e;
				System.out.println(jex.getExceptionType());
				System.out.println(jex.getDescription());
				System.out.println(jex.getRepair());
			}
			e.printStackTrace();
			System.exit(-1);
		}
		String report = "\n"
				+ "\n"
				+ ((result == null) ? "compareSource was not successfull  sorry"
						: "WAOOOOOOOUUUUU  ******** CompareSource was succesfull**********") + "\n" + "\n";

		System.out.println(report);
		return result;

	}

}
