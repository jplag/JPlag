/*
 * Created on 27.05.2005
 * Author: Moritz Kroll
 */
package jplagHomepage;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

import jplagWsClient.jplagClient.JPlagException;
import jplagWsClient.jplagClient.JPlagService_Impl;
import jplagWsClient.jplagClient.JPlagTyp_Stub;
import jplagWsClient.jplagClient.RequestData;
import jplagWsClient.jplagClient.SetDeveloperStateParams;
import jplagWsClient.util.JPlagClientAccessHandler;

public class JPlagBean {
	private JPlagTyp_Stub stub = null;

	public JPlagBean() {
		// Setup SSL stuff to allow message encryption
		//		System.setProperty("javax.net.ssl.trustStorePassword", "gulpie!");
		//		System.setProperty("javax.net.ssl.trustStore", System.getProperty("catalina.home") + File.separator + "server.trust");
		//		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		//		java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		stub = (JPlagTyp_Stub) (new JPlagService_Impl().getJPlagServicePort());

		// Set the server page login data

		HandlerChain handlerchain = stub._getHandlerChain();
		@SuppressWarnings("unchecked")
		Iterator<Handler> handlers = handlerchain.iterator();
		while (handlers.hasNext()) {
			Handler handler = handlers.next();
			if (handler instanceof JPlagClientAccessHandler) {
				((JPlagClientAccessHandler) handler).setUserPassObjects("JPlagJSP", "#Ca+#h8n4un!");
				return;
			}
		}
		throw new RuntimeException("JPlagBean(): Unable to find client access" + " handler!");
	}

	/**
	 * Converts an exception into a string depending on its type and logs a
	 * stack trace if it's not a JPlagException
	 */
	public String checkException(Exception ex) {
		if (ex instanceof JPlagException) {
			JPlagException jex = (JPlagException) ex;
			return "JPlagException: " + jex.getExceptionType() + ": " + jex.getDescription() + "<br />" + jex.getRepair();
		} else if (ex instanceof RemoteException) {
			ex.printStackTrace();
			return "RemoteException: " + ex.toString();
		} else {
			ex.printStackTrace();
			return "Unknown exception: " + ex.toString();
		}
	}

	/**
	 * @return an empty String if the user doesn't exist, otherwise an error
	 *         message
	 */
	public String existsUsername(String username) {
		try {
			RequestData rd = new RequestData(null, username, null, null, null, null, null, null, null);

			if (!stub.requestAccount(rd))
				return "Username already exists, please choose another one!";
			return "";
		} catch (Exception ex) {
			return checkException(ex);
		}
	}

	/**
	 * Sends an account request to the server
	 * 
	 * @return An empty string on success, an error string on failure
	 */
	public String requestAccount(JSPRequestData reqd) {
		try {
			RequestData rd = new RequestData(null, reqd.getUsername(), reqd.getPassword(), reqd.getRealname(), reqd.getEmail(),
					reqd.getAltEmail(), reqd.getHomepage(), reqd.getReason() + "\n" + "Institution: " + reqd.getUniSchool(),
					reqd.getNotes());

			if (!stub.requestAccount(rd))
				return "Username already exists, please choose another one!";
			return "";
		} catch (Exception ex) {
			return checkException(ex);
		}
	}

	/**
	 * Sends a provided account activation code to the server validating it. The
	 * username is part of the code.
	 * 
	 * @return An empty string on success, an error string on failure
	 */
	public String validateEmail(String code) {
		try {
			RequestData rd = new RequestData(null, null, code, null, null, null, null, null, null);

			if (!stub.requestAccount(rd))
				return "The activation code is not correct! Please make sure" + "The code is the same as the one in the email you "
						+ "received!";
			return "";
		} catch (Exception ex) {
			return checkException(ex);
		}
	}

	public String extendAccount(String code) {
		try {
			stub.extendAccount(code);
			return "";
		} catch (Exception ex) {
			return checkException(ex);
		}
	}

	public String setDeveloperState(JSPDeveloperData data) {
		try {
			SetDeveloperStateParams dsp = new SetDeveloperStateParams(data.getUsername(), data.getPassword(), data.getSignAsDeveloper());
			stub.setDeveloperState(dsp);
			return "";
		} catch (JPlagException ex) {
			return ex.getDescription() + "<br />" + ex.getRepair();
		} catch (Exception ex) {
			return checkException(ex);
		}

	}

}
