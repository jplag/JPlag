/*
 * Created on 15.03.2005
 * 
 * For more information about SOAP headers see:
 *   http://java.sun.com/webservices/docs/1.3/tutorial/doc/JAXRPC7.html#wp122942
 */
package jplagWsClient.util;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

/**
 * @author Moritz Kroll
 */
public class JPlagClientAccessHandler extends GenericHandler {
	private static final String JPLAG_TYPES_NS = "http://jplag.ipd.kit.edu/JPlagService/types";
	public static final int compatibilityLevel = 4;
	protected HandlerInfo info = null;

	/*
	 * Access information used to build up the Access header element
	 */

	protected String username = null;
	protected String password = null;

	/**
	 * Return the headers given by the info HandlerInfo object
	 */
	public QName[] getHeaders() {
		return info.getHeaders();
	}

	/**
	 * Save the HandlerInfo object
	 */
	public void init(HandlerInfo arg) {
		info = arg;
	}
	
	/**
	 * Used to set the username and password
	 * Use something like the following to access this function:
	 * 
	 * 	private JPlagClientAccessHandler accessHandler=null;
	 * 
	 * 	[...]
	 * 
	 * 		HandlerChain handlerchain=stub._getHandlerChain();
	 *		Iterator handlers=handlerchain.iterator();
	 *		while(handlers.hasNext())
	 *		{
	 *			Handler handler=(Handler) handlers.next();
	 *			if(handler instanceof JPlagClientAccessHandler)
	 *			{
	 *				accessHandler=((JPlagClientAccessHandler)handler);
	 *				break;
	 *			}
	 *		}
	 *		if(accessHandler!=null)
	 *		{
	 *			accessHandler.setUserPassObjects(getJUsernameField().getText(),
	 *				getJPasswordField().getText());
	 *		}
	 */
	public void setUserPassObjects(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Adds an "Access" element to the SOAP header
	 */
	public boolean handleRequest(MessageContext msgct) {
		if (msgct instanceof SOAPMessageContext) {
			SOAPMessageContext smsgct = (SOAPMessageContext) msgct;
			try {
				SOAPMessage msg = smsgct.getMessage();
				SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
				SOAPHeader header = msg.getSOAPHeader();

				if (header == null)
					header = envelope.addHeader(); // add an header if non exists

				SOAPHeaderElement accessElement = header.addHeaderElement(envelope.createName("Access", "ns0", JPLAG_TYPES_NS));
				SOAPElement usernameelem = accessElement.addChildElement("username");
				usernameelem.addTextNode(username);
				SOAPElement passwordelem = accessElement.addChildElement("password");
				passwordelem.addTextNode(password);
				SOAPElement compatelem = accessElement.addChildElement("compatLevel");
				compatelem.addTextNode(compatibilityLevel + "");
			} catch (SOAPException x) {
				System.out.println("Unable to create access SOAP header!");
				x.printStackTrace();
			}
		}
		return true;
	}
}
