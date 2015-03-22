/*
 * Created on 15.03.2005
 */
package jplagWebService.serverAccess;

import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import jplagWebService.serverImpl.JPlagCentral;

/**
 * @author Moritz Kroll
 */
public class JPlagServerAccessHandler extends GenericHandler {
	private static final String JPLAG_WEBSERVICE_BASE_URL = "http://jplag.ipd.kit.edu/";
	public static final int compatibilityLevel = 4;
	protected HandlerInfo info = null;

	public QName[] getHeaders() {
		return info.getHeaders();
	}

	public void init(HandlerInfo arg) {
		info = arg;
	}

	/**
	 * Extracts the username out of the header of a SOAP message
	 */
	public static String extractUsername(SOAPMessageContext smsg) {
		try {
			SOAPHeader header = smsg.getMessage().getSOAPHeader();
			if (header != null) {
				@SuppressWarnings("unchecked")
				Iterator<SOAPHeaderElement> headers = header.examineAllHeaderElements();
				while (headers.hasNext()) {
					SOAPHeaderElement he = headers.next();

					if (he.getElementName().getLocalName().equals("Access")) {
						@SuppressWarnings("unchecked")
						Iterator<SOAPElement> elements = he.getChildElements();
						while (elements.hasNext()) {
							SOAPElement e = elements.next();
							String name = e.getElementName().getLocalName();
							if (name.equals("username"))
								return e.getValue();
						}
					}
				}
			}
		} catch (SOAPException x) {
			x.printStackTrace();
		}
		return null;
	}

	/**
	 * Manually builds up a JPlagException SOAP message and replaces the
	 * original one with it
	 */
	public void replaceByJPlagException(SOAPMessageContext smsg, String desc, String rep) {
		try {
			SOAPMessage msg = smsg.getMessage();
			SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();

			/*
			 * Remove old header andy body
			 */

			SOAPHeader oldheader = envelope.getHeader();
			if (oldheader != null)
				oldheader.detachNode();
			SOAPBody oldbody = envelope.getBody();
			if (oldbody != null)
				oldbody.detachNode();

			SOAPBody sb = envelope.addBody();
			SOAPFault sf = sb.addFault(envelope.createName("Server", "env", SOAPConstants.URI_NS_SOAP_ENVELOPE),
					"jplagWebService.server.JPlagException");
			Detail detail = sf.addDetail();
			DetailEntry de = detail.addDetailEntry(envelope.createName("JPlagException", "ns0", JPLAG_WEBSERVICE_BASE_URL + "types"));

			SOAPElement e = de.addChildElement("exceptionType");
			e.addTextNode("accessException");

			e = de.addChildElement("description");
			e.addTextNode(desc);

			e = de.addChildElement("repair");
			e.addTextNode(rep);
		} catch (SOAPException x) {
			x.printStackTrace();
		}
	}

	/**
	 * Checks whether the SOAP message contains a valid Access element (correct
	 * username + password) and whether the account may still be used
	 */
	public boolean handleRequest(MessageContext context) {
		String username = null;
		String password = null;
		int compatLevel = 0;
		if (context instanceof SOAPMessageContext) {
			SOAPMessageContext smsg = (SOAPMessageContext) context;

			/*
			 * Iterator iter = context.getPropertyNames();
			 * System.out.println("Context properties:"); while(iter.hasNext())
			 * { String propname = (String) iter.next();
			 * System.out.println(propname + " = " +
			 * context.getProperty(propname).toString()); }
			 * 
			 * HttpServletRequest request = (HttpServletRequest)
			 * context.getProperty(
			 * "com.sun.xml.rpc.server.http.HttpServletRequest");
			 * System.out.println("Client IP: " + request.getRemoteAddr());
			 */

			try {
				SOAPHeader header = smsg.getMessage().getSOAPHeader();
				if (header != null) {
					@SuppressWarnings("unchecked")
					Iterator<SOAPHeaderElement> headers = header.examineAllHeaderElements();
					while (headers.hasNext()) {
						SOAPHeaderElement he = headers.next();

						if (he.getElementName().getLocalName().equals("Access")) {
							@SuppressWarnings("unchecked")
							Iterator<SOAPElement> elements = he.getChildElements();
							while (elements.hasNext()) {
								SOAPElement e = elements.next();
								String name = e.getElementName().getLocalName();
								if (name.equals("username"))
									username = e.getValue();
								else if (name.equals("password"))
									password = e.getValue();
								else if (name.equals("compatLevel")) {
									try {
										compatLevel = Integer.parseInt(e.getValue());
									} catch (NumberFormatException ex) {
										compatLevel = -1;
									}
								}
							}
						}
					}
					if (compatLevel < compatibilityLevel) {
						replaceByJPlagException(smsg, "Client outdated!", "Please update your client " + "to compatibility level "
								+ compatibilityLevel + ".");
						return false;
					}
					if (username != null && password != null) {
						int state = JPlagCentral.getInstance().getUserAdmin().getLoginState(username, password);
						if ((state & UserAdmin.MASK_EXPIRED) != 0) {
							replaceByJPlagException(smsg, "Access denied!", "Your account has " + "expired! Please contact the JPlag "
									+ "administrator to reactivate it!");
							return false;
						} else if ((state & UserAdmin.MASK_DEACTIVATED) != 0) {
							replaceByJPlagException(smsg, "Access denied!", "Your account has " + "been deactivated! Please contact the "
									+ "JPlag administrator to reactivate it!");
							return false;
						} else if (state != UserAdmin.USER_INVALID)
							return true;
					}
				} else {
					System.out.println("No header available!");
					replaceByJPlagException(smsg, "Access denied!", "The SOAP message doesn't contain an access header!");
					return false;
				}
			} catch (SOAPException x) {
				x.printStackTrace();
			}
			System.out.println("[" + new Date() + "] Access denied for user \"" + username + "\"!");

			replaceByJPlagException(smsg, "Access denied!", "Check your username and password!");
			return false;
		}
		System.out.println("Not a SOAP message context!!!");
		return false;
	}
}
