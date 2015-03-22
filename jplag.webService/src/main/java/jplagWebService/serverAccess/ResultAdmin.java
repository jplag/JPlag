/*
 * Created on 16.03.2005
 */
package jplagWebService.serverAccess;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jplagWebService.server.Submission;
import jplagWebService.serverImpl.JPlagCentral;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResultAdmin {

	public static final int SEARCH_ENTRIES=1;
	public static final int SEARCH_RESULTS=2;
	public static final int SEARCH_ALL=SEARCH_ENTRIES | SEARCH_RESULTS;
	
	private File usersDataFile=null;
	private Document doc=null;
	private Element entryRoot=null;
	private Element resultRoot=null;
	private Element rootElement=null;
	
	private int lastSubmissionID=1000000000;
	
	public ResultAdmin(String jplagHome)
	{
		usersDataFile=new File(jplagHome + File.separator + "usersdata.xml");
		if(!usersDataFile.exists()) create();
		else parse();
	}
	
    /**
     * Writes the "user submission database" to an XML file
     */ 
    private void writeXMLFile()
    {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);
    
            // Prepare the output file
            Result result = new StreamResult(usersDataFile);
    
            // Write the DOM document to the file
            Transformer xformer =
                TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
        	e.printStackTrace();
        } catch (TransformerException e) {
        	e.printStackTrace();
        }
    }
	
    /**
     * Creates a new "user submission database" XML file
     */
	private void create()
	{
		DocumentBuilderFactory docBFac;
		DocumentBuilder docBuild;
		try
		{
			docBFac=DocumentBuilderFactory.newInstance();
			docBuild = docBFac.newDocumentBuilder();
			doc = docBuild.newDocument();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return;
		}
		rootElement=doc.createElement("jplagData");
		lastSubmissionID=1000000000;
		rootElement.setAttribute("lastSubID",lastSubmissionID+"");
		entryRoot=doc.createElement("EntriesQueue");
		resultRoot=doc.createElement("ResultData");
		rootElement.appendChild(entryRoot);
		rootElement.appendChild(resultRoot);
		doc.appendChild(rootElement);

		writeXMLFile();
	}
	
	/**
	 * Loads an existing "user submission database" into memory
	 */
	private void parse()
	{
		try
		{
			FileInputStream xmlStream=new FileInputStream(usersDataFile);
			DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			DocumentBuilder builder=factory.newDocumentBuilder();
			doc=builder.parse(xmlStream);
            xmlStream.close();
			rootElement=doc.getDocumentElement();
			lastSubmissionID=Integer.parseInt(
                rootElement.getAttribute("lastSubID"));
			System.out.println("lastSubmissionID="+lastSubmissionID);
			
			NodeList entriesList=doc.getElementsByTagName("EntriesQueue");
			entryRoot=(Element) entriesList.item(0);
			entriesList=doc.getElementsByTagName("ResultData");
			resultRoot=(Element) entriesList.item(0);
		}
		catch(javax.xml.parsers.ParserConfigurationException e)
		{
			System.out.println("Failed to create DocumentBuilder!");
			e.printStackTrace();
		}
		catch(org.xml.sax.SAXException e)
		{
			System.out.println("Error parsing users.xml!");
			e.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("File error!");
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Next available submission ID
	 */
	public synchronized String getNextSubmissionID()
	{
		lastSubmissionID++;
		rootElement.setAttribute("lastSubID",lastSubmissionID+"");
        writeXMLFile();
		return lastSubmissionID+"";
	}
	
	/**
	 * Adds a new submission to the entry queue
	 * @param struct Submission to be queued
	 */
	public synchronized void addEntry(AccessStructure struct)
	{
		entryRoot.appendChild(struct.toXMLEntryElement(doc));
		
		writeXMLFile();
	}

	public synchronized int getNumEntries()
	{
		NodeList list=entryRoot.getElementsByTagName("entry");
		return list.getLength();
	}

	public synchronized AccessStructure getNextEntry()
	{
		if(!entryRoot.hasChildNodes()) return null;
		return AccessStructure.fromXMLEntryElement(
            (Element) entryRoot.getFirstChild());
	}
	
	public synchronized AccessStructure getAccessStruct(String subID,
            int searchFlags)
	{
		NodeList list;
		if((searchFlags & SEARCH_ENTRIES)!=0)
		{
			list=entryRoot.getElementsByTagName("entry");
			for(int i=0;i<list.getLength();i++)
			{
				Element entry=(Element) list.item(i);
			
				if(entry.getAttribute("id").equals(subID))
				{
					AccessStructure struct = 
                        AccessStructure.fromXMLEntryElement(entry);
                    struct.getDecorator().setProgress(i);   // set queue pos
                    return struct;
				}
			}
		}
		if((searchFlags & SEARCH_RESULTS)!=0)
		{
			list=resultRoot.getElementsByTagName("result");
			for(int i=0;i<list.getLength();i++)
			{
				Element submission=(Element) list.item(i);
			
				if(submission.getAttribute("id").equals(subID))
				{
					return AccessStructure.fromXMLResultElement(submission);
				}
			}
		}
		return null;
	}
	
	/**
	 * Adds a completed submission to the resultRoot and removes the first
	 * submission from the elementRoot 
	 */
	public synchronized void addResult(AccessStructure struct)
	{
		NodeList usersList=resultRoot.getElementsByTagName("user");
		Element founduser=null;
		for(int i=0;i<usersList.getLength();i++)
		{
			Element user=(Element) usersList.item(i);
			
			if(user.getAttribute("username").equals(struct.getUsername()))
			{
				founduser=user;
				break;
			}
		}
		if(founduser==null)
		{
			founduser=doc.createElement("user");
			founduser.setAttribute("username",struct.getUsername());
			resultRoot.appendChild(founduser);
		}
		founduser.appendChild(struct.toXMLResultElement(doc));
		
		// XXX: Check whether the first is the right one		
		Node firstEntry=entryRoot.getFirstChild();
		entryRoot.removeChild(firstEntry);
		writeXMLFile();
	}
	
	/**
	 * Removes a given ready to be processed submission from the database
	 * @return True, when it was successfully deleted.
	 * False, if the submission wasn't found.
	 */
	public synchronized boolean deleteEntry(String username,
            String submissionID)
	{
		NodeList list=entryRoot.getElementsByTagName("entry");
		for(int i=0;i<list.getLength();i++)
		{
			Element entry=(Element) list.item(i);
		
			if(entry.getAttribute("id").equals(submissionID))
			{
				if(!entry.getAttribute("username").equals(username))
					return false;
				entryRoot.removeChild(entry);
				writeXMLFile();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes a given completed submission from the database
	 * @return True, when it was successfully deleted.
	 * False, if the submission wasn't found.
	 */
	public synchronized boolean deleteResult(String username,
            String submissionID)
	{
		NodeList usersList = resultRoot.getElementsByTagName("user");
        int numusers = usersList.getLength();
		for(int i=0; i<numusers; i++)
		{
			Element user = (Element) usersList.item(i);
			
			if(user.getAttribute("username").equals(username))
			{
				NodeList subList = user.getElementsByTagName("result");
                int numresults = subList.getLength();
				for(int j=0; j<numresults; j++)
				{
					Element elem = (Element) subList.item(j);
					if(elem.getAttribute("id").equals(submissionID))
					{
						user.removeChild(elem);
						if(numresults==1)                // removed last result?
							resultRoot.removeChild(user);	// remove user entry
					
						writeXMLFile();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Deletes an submission from the database
	 * @param struct: Submission to be deleted
	 * @return true, if everything's alright
	 */	
	public synchronized boolean deleteSubmission(AccessStructure struct)
	{
		if(struct.getState()<300)
			return deleteEntry(struct.getUsername(),struct.getSubmissionID());
		else
			return deleteResult(struct.getUsername(),struct.getSubmissionID());
	}

	/**
	 * @return String array containing all usernames
	 */
	public synchronized String[] usersList()
	{	
		NodeList usersList= doc.getElementsByTagName("user");
		String [] result = new String [usersList.getLength()];
		for(int i=0;i<usersList.getLength();i++)
		{
			Element user=(Element) usersList.item(i);
			result[i]=user.getAttribute("username");
		}
		return result;
	}
	
	/**
	 * @return The number of waiting submissions for a given user
	 */
	public synchronized int getNumUserEntries(String username)
	{
		NodeList list=entryRoot.getElementsByTagName("entry");
		int numentries=0;
		for(int j=0;j<list.getLength();j++)
		{
			Element entry=(Element) list.item(j);
		
			if(entry.getAttribute("username").equals(username))
				numentries++;
		}
		return numentries;
	}
	
	/**
	 * @return The number of finished submissions for a given user
	 */
	public synchronized int getNumUserResults(String username)
	{
		NodeList usersList=doc.getElementsByTagName("user");
		
		for(int i=0;i<usersList.getLength();i++)
		{
			Element user=(Element) usersList.item(i);
				
			if(user.getAttribute("username").equals(username))
			{
				NodeList subList=user.getElementsByTagName("result");
				return subList.getLength();
			}
		}
		return 0;
	}
	
	/**
	 * @return A list of Submission objects belonging to a given user
	 */
	public synchronized Submission[] listSubmissions(String username)
	{
		NodeList usersList=doc.getElementsByTagName("user");
	
		for(int i=0;i<usersList.getLength();i++)
		{
			Element user=(Element) usersList.item(i);
			
			if(user.getAttribute("username").equals(username))
			{
				NodeList list=entryRoot.getElementsByTagName("entry");
				int numentries=0;
				for(int j=0;j<list.getLength();j++)
				{
					Element entry=(Element) list.item(j);
				
					if(entry.getAttribute("username").equals(username))
						numentries++;
				}
				NodeList subList = user.getElementsByTagName("result");
				Submission[] subArray =
                    new Submission[numentries+subList.getLength()];
				int subind=0;
				for(int j=0;j<list.getLength();j++)
				{
					Element entry=(Element) list.item(j);
				
					if(entry.getAttribute("username").equals(username))
					{
						String subID=entry.getAttribute("id");
						int state=JPlagCentral.getStateIfActive(username,subID);
						subArray[subind++]=new Submission(subID,
							entry.getAttribute("title"),new java.util.Date(
								Long.parseLong(entry.getAttribute("date")))
								.toString(),state);
					}
				}
				for(int j=0;j<subList.getLength();j++)
				{
					Element elem=(Element) subList.item(j);
					int laststate;
					try
					{
						laststate=Integer.parseInt(
                            elem.getAttribute("laststate"));
					}
					catch(NumberFormatException e)
					{
						laststate=499;
					}
					subArray[subind++]=new Submission(elem.getAttribute("id"),
						elem.getAttribute("title"),
						new java.util.Date(
								Long.parseLong(elem.getAttribute("date")))
								.toString(),
						laststate);
				}
				return subArray;
			}
		}
		return new Submission[0];
	}

	/**
	 * @return A list of AccessStructure objects belonging to a given user
	 */
	public synchronized AccessStructure[] listAccessStructures(String username)
	{
		NodeList usersList=doc.getElementsByTagName("user");
	
		for(int i=0;i<usersList.getLength();i++)
		{
			Element user=(Element) usersList.item(i);
			
			if(user.getAttribute("username").equals(username))
			{
				NodeList list=entryRoot.getElementsByTagName("entry");
				int numentries=0;
				for(int j=0;j<list.getLength();j++)
				{
					Element entry=(Element) list.item(j);
					if(entry.getAttribute("username").equals(username))
						numentries++;
				}
				NodeList subList=user.getElementsByTagName("result");
				AccessStructure[] structArray=new AccessStructure[
                        numentries+subList.getLength()];
				int subind=0;
				for(int j=0;j<list.getLength();j++)
				{
					Element entry=(Element) list.item(j);
					if(entry.getAttribute("username").equals(username))
						structArray[subind++] =
                            AccessStructure.fromXMLEntryElement(entry);
				}
				for(int j=0;j<subList.getLength();j++)
				{
					Element elem=(Element) subList.item(j);
					structArray[subind++] =
                        AccessStructure.fromXMLResultElement(elem);
				}
				return structArray;
			}
		}
		return new AccessStructure[0];
	}
}
