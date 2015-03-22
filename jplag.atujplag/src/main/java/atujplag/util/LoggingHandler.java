package atujplag.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPMessage;
 
//import atujplag.view2.LogOutputStream;

public class LoggingHandler extends GenericHandler {

//    String[] MIME_HEADERS = { "Server", "Date", "Content-length", "Content-type", "SOAPAction" };
	
	protected HandlerInfo info=null;

	public boolean handleRequest(MessageContext context) {
//		System.out.println("Entering LoggingHandler::handleRequest()");
/*		try
		{
			SOAPMessageContext smsgct=(SOAPMessageContext) context;
			SOAPMessage msg=smsgct.getMessage();
			SOAPEnvelope envelope=msg.getSOAPPart().getEnvelope();
			SOAPHeader header=msg.getSOAPHeader();
			if(header==null)
			{
				header=envelope.addHeader();
			}
		
			SOAPHeaderElement accessElement=header.addHeaderElement(
					envelope.createName("logged","ns1",
					"http://example.com/jplag"));
			accessElement.addTextNode("You got logged!!");
		}
		catch(SOAPException e)
		{
			e.printStackTrace();
		}*/
		logMessage("request", context);
//		System.out.println("Leaving LoggingHandler::handleRequest()");
		return true;
	}

	public boolean handleResponse(MessageContext context) {
		logMessage("response", context);
		return true;
	}


	public boolean handleFault(MessageContext context) {
		logMessage("fault", context);
		return true;
	}

    private void logMessage(String method, MessageContext context) {
        FileOutputStream fout = createFile();
        if(fout==null) return;
        LogOutputStream out = new LogOutputStream(fout, true);
        try {
            out.println("<" + method + ">");
            //Uncomment this statement to log the SOAP messages.
            out.println(logSOAPMessage(context));
            out.println("</" + method + ">");
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
			throw new JAXRPCException("LoggingHandler: Unable to log the message in " + method + " - {" + ex.getClass().getName() + "}"
					+ ex.getMessage());
        } finally {
            out.flush();
			out.close();
        }
    }	
	private String logSOAPMessage(MessageContext context) {
		StringBuffer stringBuffer = new StringBuffer();
		SOAPMessageContext smc = (SOAPMessageContext) context;
		SOAPMessage soapMessage = smc.getMessage();
/*		try
		{
			SOAPBody soapBody = soapMessage.getSOAPBody();
			NodeList list=soapBody.getElementsByTagName("inputZipFile");
		}
		catch(Exception e)
		{
			// ignore filtering
		}*/

		ByteArrayOutputStream bout= new ByteArrayOutputStream();
		try {
			soapMessage.writeTo(bout);
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
		stringBuffer.append(bout.toString() + "\n");

		return stringBuffer.toString();
	} 

	public FileOutputStream createFile() {
		FileOutputStream fout = null;
		try {
			String logfile = System.getProperty("jplag.client.logfile");
			if(logfile==null || logfile.length()==0) return null;
				fout = new FileOutputStream(logfile, true); //append
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			throw new JAXRPCException("Unable to initialize the log file: " +
				ex.getClass().getName() + " - " + ex.getMessage());
		}
		return fout;
	}
	
	public void init(HandlerInfo arg) {
		info=arg;
	}
	
   	public QName[] getHeaders() {
   		return info.getHeaders();
   	}
}

