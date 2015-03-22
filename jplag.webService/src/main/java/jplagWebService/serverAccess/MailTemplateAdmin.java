/*
 * Created on 12.06.2005
 * Author: Moritz Kroll
 */

package jplagWebService.serverAccess;

import java.io.File;
import java.io.FileInputStream;

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
import jplagWebService.server.MailTemplate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MailTemplateAdmin {
	public static final int MAIL_ACCEPTED = 0;
	public static final int MAIL_DECLINED = 1;
	public static final int MAIL_SERVER = 2;
    public static final int MAIL_OTHERS = 3;
	
	private static final String[] mailTypes={ "accepted", "declined",
		"serverMail", "others" };
	
	public static final String SERVER_VERIFICATION = "verification";
	public static final String SERVER_WARNEXPIRE = "warnExpire";
    public static final String SERVER_ASKEXTEND = "askExtend";
	public static final String SERVER_EXPIRED = "expired";
	public static final String SERVER_REQUESTNOTIFY = "requestNotify";
	
    private Document doc = null;
    private Element rootElement = null;
	
	private File mailTemplateFile;

	public MailTemplateAdmin(String jplagHome) {
		mailTemplateFile=new File(jplagHome + File.separator +
				"mailTemplates.xml");
        if(!mailTemplateFile.exists())
            create();
        else
            parse();
	}

    /**
     * Writes the mail templates data to an XML file
     */
    private synchronized void writeXMLFile() {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            Result result = new StreamResult(mailTemplateFile);

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
     * Creates a new "jplag-user database" XML file
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
        rootElement = doc.createElement("mailTemplates");
        doc.appendChild(rootElement);
		
		// Add Standard messages
		
		Element newelem = doc.createElement("accepted");
		newelem.setAttribute("name","Standard");
		newelem.setAttribute("subject","[JPlag] Account request accepted");
		newelem.setAttribute("data","Dear {realname},\n\nYour JPlag account " +
				"request has been accepted.\nPlease visit the following " +
				"URL to get access to the client application:\n\n" +
                "Website: {server}\nUsername: {username}\n\n" +
				"Best regards\nThe JPlag administration");
		rootElement.appendChild(newelem);
		
		newelem = doc.createElement("accepted");
		newelem.setAttribute("name","No email");
		rootElement.appendChild(newelem);
		
		newelem = doc.createElement("declined");
		newelem.setAttribute("name","Standard");
		newelem.setAttribute("subject","[JPlag] Account request rejected");
		newelem.setAttribute("data","Dear {realname},\n\nWe are sorry to tell" +
				" you, that we decided not to give you a JPlag account" +
				" because\n\n<ENTER REASON HERE>\n\nBest regards\nThe JPlag" +
				" administration");
		rootElement.appendChild(newelem);
        
        newelem = doc.createElement("declined");
        newelem.setAttribute("name","No comparison to the Internet");
        newelem.setAttribute("subject","[JPlag] Account request denied");
        newelem.setAttribute("data","Dear {realname},\n\nIt is our impression" +
                " that you want to compare your students work to the" +
                " internet, but JPlag does not do that. For more information" +
                " about what JPlag is able to do, please refer to our" +
                " website.\nIf this assumption is wrong, please apply again" +
                " so that we can give you an account.\n\n" +
                "Best regards\nThe JPlag administration");
        rootElement.appendChild(newelem);
        
        newelem = doc.createElement("declined");
        newelem.setAttribute("name","No teacher");
        newelem.setAttribute("subject","[JPlag] Account request denied");
        newelem.setAttribute("data","Dear {realname},\n\n"
            + "You need to be a teacher or lecturer to qualify for an account. "
            + "If you are neither, you may still get an account for research "
            + "reasons, but you have to give more details about the kind of "
            + "research you are doing.\n\n"
            + "Best regards\nThe JPlag administration");

        newelem = doc.createElement("declined");
        newelem.setAttribute("name","Illegal Email address");
        newelem.setAttribute("subject","[JPlag] Account request denied");
        newelem.setAttribute("data","Dear {realname},\n\n" +
            "We are sorry to tell you that we can not give you a JPlag " +
            "account, since you registered with an anonymous email address. " +
            "You may re-apply with your official school/university email " +
            "address.\n\n" +
            "Best regards\nThe JPlag administration");

        newelem = doc.createElement("declined");
        newelem.setAttribute("name","No individual account");
        newelem.setAttribute("subject","[JPlag] Account request denied");
        newelem.setAttribute("data","Dear {realname},\n\n" +
            "We are sorry to tell you that we can not give you a JPlag " +
            "account, since you did not apply as an individual. (We do not " +
            "give accounts to groups.)\nPlease re-apply with your personal " +
            "data.\n\n" +
            "Best regards\nThe JPlag administration");

        newelem = doc.createElement("declined");
        newelem.setAttribute("name","Duplication");
        newelem.setAttribute("subject","[JPlag] Account request denied");
        newelem.setAttribute("data","Dear {realname},\n\n" +
            "We denied your account, since you plan to use JPlag to find " +
            "code duplication; something which JPlag can not do.\n" +
            "It is made to compare different programs to each other. While " +
            "this seems to be similar to find duplicates, it is something " +
            "different from the technical point of view.\n\n" +
            "Best regards\nThe JPlag administration");

        newelem = doc.createElement("declined");
        newelem.setAttribute("name","English");
        newelem.setAttribute("subject","[JPlag] Account request denied");
        newelem.setAttribute("data","Dear {realname},\n\n" +
            "Although we have localized versions of the JPlag program itself," +
            " you still have to apply in English or German, since these are" +
            " the only languages we understand.\n\n" +
            "Best regards\nThe JPlag administration");

        newelem = doc.createElement("declined");
        newelem.setAttribute("name","Full name");
        newelem.setAttribute("subject","[JPlag] Account request denied");
        newelem.setAttribute("data","Dear {realname},\n\n" +
            "Your account was denied, since you did not fill in the form on " +
            "the Website properly.\nPlease apply with your full name and " +
            "correct email address of your school/university.\n\n" +
            "Best regards\nThe JPlag administration");
        
		newelem = doc.createElement("declined");
		newelem.setAttribute("name","No email");
		rootElement.appendChild(newelem);
		
		newelem = doc.createElement("serverMail");
		newelem.setAttribute("name","verification");
		newelem.setAttribute("subject","[JPlag] Email address verification");
		newelem.setAttribute("data","Dear {realname},\n\nThis mail is sent " +
				"to you in order to verificate your email address. Please " +
                "visit the following URL to activate your request:\n\n" +
				"{server}/indexActivate.jsp?code={code}" +
				"\n\nAfter the activation the request will be sent to the " +
				"administrator who will then decide on your application.\n\n" +
				"Best regards\nThe JPlag administration");
		rootElement.appendChild(newelem);
		
		newelem = doc.createElement("serverMail");
		newelem.setAttribute("name","warnExpire");
		newelem.setAttribute("subject","[JPlag] Your account expires within two weeks!");
		newelem.setAttribute("data","Dear {realname},\n\n" +
				"Your JPlag account will expire on {expires} (DD.MM.YYYY).\n" +
				"Please contact us, if you want to extend your " +
				"account time.\n\nBest regards\nThe JPlag administration");
		rootElement.appendChild(newelem);

        newelem = doc.createElement("serverMail");
        newelem.setAttribute("name","askExtend");
        newelem.setAttribute("subject","[JPlag] Your account expires within two weeks!");
        newelem.setAttribute("data","Dear {realname},\n\n"
                + "Your JPlag account will expire on {expires} (DD.MM.YYYY).\n"
                + "If you want to extend your account time for a year, just "
                + "visit the following URL:\n\n"
                + "{server}/indexExtend.jsp?code={code}"
                + "\n\nIf you do not need this account anymore, you can just "
                + "ignore this mail."
                + "\n\nBest regards\nThe JPlag administration");
        rootElement.appendChild(newelem);

		newelem = doc.createElement("serverMail");
		newelem.setAttribute("name","expired");
		newelem.setAttribute("subject","[JPlag] Your account has expired!");
		newelem.setAttribute("data","Dear {realname},\n\n" +
				"We are sorry to tell you that your JPlag account expired " +
				"today.\nPlease contact us, if you want to reactivate your " +
				"account.\n\nBest regards\nThe JPlag administration");
		rootElement.appendChild(newelem);
		
		newelem = doc.createElement("serverMail");
		newelem.setAttribute("name","requestNotify");
		newelem.setAttribute("subject","[JPlag] New request notification");
		newelem.setAttribute("data","Please decide on the following request " +
				"in the AdminTool:\n\nUsername: {username}" +
				"\nReal name: {realname}" +
				"\nEmail: {email}\nAlternative email: {emailSecond}" +
				"\nHomepage: {homepage}\nReason: {reason}" +
				"\nAdditional notes: {notes}");
		rootElement.appendChild(newelem);
		
        writeXMLFile();
    }

    /**
     * Loads an existing "jplag-user database" into memory
     */
    private void parse() {
        try {
            FileInputStream xmlStream = new FileInputStream(mailTemplateFile);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlStream);
            xmlStream.close();
            rootElement = doc.getDocumentElement();
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

	/**
	 * @return The mail template with the given name of the given type
	 * 		   or null if it doesn't exist
	 */
	public synchronized MailTemplate getMailTemplate(int type, String name)
	{
		if(type<0 || type>=mailTypes.length) return null;
		
        NodeList list = rootElement.getElementsByTagName(mailTypes[type]);
		for(int i=0;i<list.getLength();i++)
		{
			Element elem = (Element) list.item(i);
			if(elem.getAttribute("name").equals(name))
			{
				return new MailTemplate(elem.getAttribute("name"),
						elem.getAttribute("subject"),elem.getAttribute("data"));
			}
		}
		return null;
	}
	
	/**
	 * @return An array of all mail templates of the given type
	 */
	public synchronized MailTemplate[] getMailTemplates(int type)
			throws JPlagException
	{
		if(type<0 || type>=mailTypes.length)
			throw new JPlagException("getMailTemplates", "Type index out of" +
					" bounds", "Please check the type parameter!");
		
        NodeList list = rootElement.getElementsByTagName(mailTypes[type]);
		MailTemplate[] templates = new MailTemplate[list.getLength()];
		for(int i=0;i<list.getLength();i++)
		{
			Element elem = (Element) list.item(i);
			templates[i] = new MailTemplate(elem.getAttribute("name"),
					elem.getAttribute("subject"),elem.getAttribute("data"));
		}
		return templates;
	}
	
	/**
	 * Updates or adds the given mail template for the given type 
	 */
	public synchronized void setMailTemplate(int type, MailTemplate template)
			throws JPlagException
	{
		if(type<0 || type>=mailTypes.length)
			throw new JPlagException("setMailTemplate", "Type index out of" +
					" bounds", "Please check the type parameter!");
        NodeList list = rootElement.getElementsByTagName(mailTypes[type]);
		for(int i=0;i<list.getLength();i++)
		{
			Element elem = (Element) list.item(i);
			if(template.getName().equals(elem.getAttribute("name")))
			{
                if(template.getSubject().length()==0) {     // remove ?
                    if(elem.getAttribute("subject").length()==0)
                        throw new JPlagException("setMailTemplate",
                            "You may not remove any \"No email\" entries!",
                            "Tried to remove template with empty subject!");
                    rootElement.removeChild(elem);
                }
                else if(template.getData().length()==0) {   // rename ?
                    elem.setAttribute("name",template.getSubject());
                }
                else {                                      // change !
                    elem.setAttribute("subject",template.getSubject());
                    elem.setAttribute("data",template.getData());
                }
                writeXMLFile();
				return;
			}
		}

        if(template.getName().length()==0 || template.getSubject().length()==0
                || template.getData().length()==0)
            throw new JPlagException("setMailTemplate", "Name, subject or data"
                + " is empty!", "Please check the parameters!");
        
		Element newelem = doc.createElement(mailTypes[type]);
		newelem.setAttribute("name",template.getName());
		newelem.setAttribute("subject",template.getSubject());
		newelem.setAttribute("data",template.getData());
		rootElement.appendChild(newelem);
		writeXMLFile();
	}
	
	/**
	 * Replaces all keys of the form "{<keyname>}" in the template string
	 * by the appropriate string given by the MailTemplateData object
	 */
	public String evalTemplateString(String template, MailTemplateData td) {
		String result = "";
		String[] tokens = template.split("[{}]");
		// TODO: Check whether every '{' has a following '}'
		result = tokens[0];
		for(int i=1;i<tokens.length;i+=2)
		{
			String value = (String) td.get(tokens[i]);
			if(value==null || value.length()==0) result += "[N/A]";
			else result += td.get(tokens[i]);
			if(i+1<tokens.length) result += tokens[i+1];
		}
		return result;
	}
}
