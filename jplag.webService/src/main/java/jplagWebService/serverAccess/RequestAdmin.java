/*
 * Created on 28.05.2005
 * Author: Moritz Kroll
 */
package jplagWebService.serverAccess;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jplagWebService.server.JPlagException;
import jplagWebService.server.RequestData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RequestAdmin {

    private Document doc = null;
    private Element rootElement = null;
	private Element nonValidatedRequestsElement = null;
	private Element validatedRequestsElement = null;

    private File requestFile = null;
	
    public RequestAdmin(String jplagHome) {
        requestFile = new File(jplagHome + File.separator +
				"account_requests.xml");
        if(!requestFile.exists())
            create();
        else
            parse();
    }
	
    /**
     * Writes the "account-requests" data to an XML file
     */
    private synchronized void writeXMLFile() {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            Result result = new StreamResult(requestFile);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance()
                    .newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a new "account-request database" XML file
     */
    private void create() {
        DocumentBuilderFactory docBFac;
        DocumentBuilder docBuild;
        try {
            docBFac = DocumentBuilderFactory.newInstance();
            docBuild = docBFac.newDocumentBuilder();
            doc = docBuild.newDocument();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        rootElement = doc.createElement("account-requests");
        doc.appendChild(rootElement);
		nonValidatedRequestsElement = doc.createElement("nonvalidated-requests");
		validatedRequestsElement = doc.createElement("validated-requests");
		rootElement.appendChild(nonValidatedRequestsElement);
		rootElement.appendChild(validatedRequestsElement);
        writeXMLFile();
    }

    /**
     * Loads an existing "account-request database" into memory
     */
    private void parse() {
        try {
            FileInputStream xmlStream = new FileInputStream(requestFile);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlStream);
            xmlStream.close();
            rootElement = doc.getDocumentElement();
			NodeList nodes=rootElement.getElementsByTagName(
					"nonvalidated-requests");
			nonValidatedRequestsElement=(Element) nodes.item(0);
			nodes=rootElement.getElementsByTagName("validated-requests");
			validatedRequestsElement=(Element) nodes.item(0);
        }
        catch (javax.xml.parsers.ParserConfigurationException e) {
            // throw new RuntimeException("Failed to create DocumentBuilder");
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            // throw new RuntimeException("Error parsing users.xml");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("File error!");
            e.printStackTrace();
        }
    }
	
	private void setString(Element elem, String attr, String str) {
		if(str!=null && str.length()!=0)
			elem.setAttribute(attr,str);
	}
	
	public static String getRandomCode() {
		char[] code=new char[11];
		for(int i=0;i<11;i++) {
			if((i&3)==3) code[i]=(char)((Math.random()*10)+'0');
			else code[i]=(char)((Math.random()*26)+'A');
		}
		return new String(code);
	}

	private String formatCalendar(Calendar cal)
	{
		DateFormat df=DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.GERMAN);
		return df.format(cal.getTime());
	}
	
/*	private void setCalendar(Element user, Calendar cal, String attr) {
		user.setAttribute(attr,formatCalendar(cal));
	}*/
	
    private Calendar parseCalendar(String str)
    {
		Calendar cal=new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		try
		{
			cal.setTime(DateFormat.getDateInstance(DateFormat.MEDIUM,
					Locale.GERMAN).parse(str));
		}
		catch(java.text.ParseException ex)
		{
			ex.printStackTrace();
			System.out.println("Illegal date: " + str);
			cal.set(1970,0,1);	// set an error date (1.1.1970)
			System.out.println("Set to " + formatCalendar(cal));
		}
		return cal;
    }

    private Calendar parseCalendar(Element elem, String attrname) {
		return parseCalendar(elem.getAttribute(attrname));
	}
	
	public synchronized String addRequest(RequestData rd) {
		Element elem=doc.createElement("request");
		setString(elem,"username",rd.getUsername());
		setString(elem,"password",rd.getPassword());
		setString(elem,"realname",rd.getRealName());
		setString(elem,"email",rd.getEmail());
		setString(elem,"emailSecond",rd.getEmailSecond());
		setString(elem,"homepage",rd.getHomepage());
		setString(elem,"reason",rd.getReason());
		setString(elem,"notes",rd.getNotes());
		String code=getRandomCode() + rd.getUsername();
		setString(elem,"code",code);
		Calendar cal=Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		elem.setAttribute("requested",formatCalendar(cal));
		nonValidatedRequestsElement.appendChild(elem);
		writeXMLFile();
		return code;
	}
	
	/**
	 * Checks the email verification and moves the request from "non-validated"
	 * to "validated" status, if the verification is valid. Otherwise it throws
	 * an exception.
	 * 
	 * @return The username corresponding to the verification code
	 * @throws JPlagException when the username wasn't found, the code is not
	 * 		  correct or the request has already been verified
	 */
	public synchronized String validateRequest(String code)
			throws JPlagException
	{
		if(code.length()<12) // username must be at least one character
		{
			throw new JPlagException("validateRequest",
					"Wrong verification code given: " + code,
					"Please check the correct spelling of the code!");
		}
		String username=code.substring(11);
		System.out.println("[" + new Date() + "] validateRequest: username="
            + username + " code=" + code);
		NodeList reqs=nonValidatedRequestsElement.getElementsByTagName("request");
		for(int i=0;i<reqs.getLength();i++) {
			Element elem=(Element) reqs.item(i);
			if(elem.getAttribute("username").equals(username))
			{
				if(elem.getAttribute("code").equals(code))
				{
					nonValidatedRequestsElement.removeChild(elem);
					elem.removeAttribute("code");
					elem.setAttribute("validated",new Date()+"");
					validatedRequestsElement.appendChild(elem);
					writeXMLFile();
					return username;
				}
				else throw new JPlagException("validateRequest",
						"Wrong verification code given: " + code,
						"Please check the correct spelling of the code!");
			}
		}
		reqs=validatedRequestsElement.getElementsByTagName("request");
		for(int i=0;i<reqs.getLength();i++) {
			Element elem=(Element) reqs.item(i);
			if(elem.getAttribute("username").equals(username))
			{
				throw new JPlagException("validateRequest",
						"The request's email address has already been " +
						"verified!", "Please wait until the administrator has" +
						" decided on your request!");
			}
		}
		throw new JPlagException("validateRequest",
				"No request for user \"" + username + "\" found!",
				"Please check the correct spelling of the username! "
                + "The request may also have expired.");
	}
	
	public synchronized void removeRequest(String username) {
		NodeList reqs=validatedRequestsElement.getElementsByTagName("request");
		for(int i=0;i<reqs.getLength();i++) {
			Element elem=(Element) reqs.item(i);
			if(elem.getAttribute("username").equals(username))
			{
				validatedRequestsElement.removeChild(elem);
				writeXMLFile();
				return;
			}
		}
	}
	
	public synchronized void removeExpiredUnvalidatedRequests() {
		boolean changed=false;
		NodeList reqs=nonValidatedRequestsElement.getElementsByTagName("request");
		Calendar cal=Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.add(Calendar.DATE,-3);
		for(int i=reqs.getLength()-1;i>=0;i--) {
			Element elem=(Element) reqs.item(i);
			Calendar curcal=parseCalendar(elem,"requested");
			if(cal.after(curcal))
			{
				System.out.println("[" + new Date() + "] Non-validated request"
						+ " for username " + elem.getAttribute("username")
						+ " for " + elem.getAttribute("realname")
						+ " with email " + elem.getAttribute("email")
						+ " expired!");
				nonValidatedRequestsElement.removeChild(elem);
				changed=true;
			}
		}
		if(changed) writeXMLFile();
	}
	
	private String parseNillableString(Element elem, String attrname) {
		String str=elem.getAttribute(attrname);
		if(str.length()==0) return null;
		else return str;
	}
	
	private RequestData getRequestDataFromElement(Element elem) {
		RequestData rd=new RequestData();
		rd.setValidateTime(elem.getAttribute("validated"));
		rd.setUsername(elem.getAttribute("username"));
		rd.setPassword(elem.getAttribute("password"));
		rd.setRealName(elem.getAttribute("realname"));
		rd.setEmail(elem.getAttribute("email"));
		rd.setEmailSecond(parseNillableString(elem,"emailSecond"));
		rd.setHomepage(parseNillableString(elem,"homepage"));
		rd.setReason(elem.getAttribute("reason"));
		rd.setNotes(parseNillableString(elem,"notes"));
		return rd;
	}
	
	public synchronized RequestData getRequestData(String username) {
		NodeList reqs=validatedRequestsElement.getElementsByTagName("request");
		for(int i=0;i<reqs.getLength();i++) {
			Element elem=(Element) reqs.item(i);
			if(elem.getAttribute("username").equals(username))
				return getRequestDataFromElement(elem);
		}
		return null;
	}
	
	public synchronized RequestData[] getRequests(boolean lengthOnly) {
		NodeList reqs=validatedRequestsElement.getElementsByTagName("request");
		RequestData[] rds=new RequestData[reqs.getLength()];
		if(!lengthOnly)	{
			for(int i=0;i<reqs.getLength();i++) {
				Element elem=(Element) reqs.item(i);
				rds[i]=getRequestDataFromElement(elem);
			}
		}
		else {
			for(int i=0;i<reqs.getLength();i++) {
				rds[i]=new RequestData();
			}
		}
		return rds;
	}
	
	/**
	 * @return whether there is a request for the given username
	 */
	public synchronized boolean exists(String username) {
		NodeList reqs=nonValidatedRequestsElement.getElementsByTagName("request");
		for(int i=0;i<reqs.getLength();i++) {
			Element elem=(Element) reqs.item(i);
			if(elem.getAttribute("username").equals(username))
				return true;
		}
		reqs=validatedRequestsElement.getElementsByTagName("request");
		for(int i=0;i<reqs.getLength();i++) {
			Element elem=(Element) reqs.item(i);
			if(elem.getAttribute("username").equals(username))
				return true;
		}
		return false;
	}
}
