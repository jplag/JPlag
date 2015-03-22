package atujplag;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jplagUtils.DesktopUtils;
import jplagUtils.PropertiesLoader;
import jplagWsClient.jplagClient.LanguageInfo;
import jplagWsClient.jplagClient.Option;
import jplagWsClient.jplagClient.ServerInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import apollo.BasicService;
import apollo.FileContents;
import apollo.PersistenceService;
import apollo.ServiceManager;
import atujplag.client.SimpleClient;
import atujplag.util.LanguageSetting;
import atujplag.util.LoginDialog;
import atujplag.util.Messages;
import atujplag.util.TagParser;
import atujplag.view.JPlagCreator;
import atujplag.view.OptionPanel;
import atujplag.view.View;
import atujplag.view.WelcomeOptionsDialog;

public class ATUJPLAG {
	private static final Properties versionProps = PropertiesLoader.loadProps("atujplag/version.properties");
	public static final String VERSION_STRING = versionProps.getProperty("version", "devel");

	public static final String programName = "JPlag Web Start client " + VERSION_STRING; //$NON-NLS-1$

	public static final int DELETE = 0;
	public static final int RENAME = 1;

	public static final String[] COUNTRY_LANGUAGES = new String[] {
			Messages.getString("ATUJPLAG.English"), //$NON-NLS-1$
			Messages.getString("ATUJPLAG.German"), //$NON-NLS-1$
            Messages.getString("ATUJPLAG.French"), //$NON-NLS-1$
            Messages.getString("ATUJPLAG.Spanish"), //$NON-NLS-1$
			Messages.getString("ATUJPLAG.BrazilianPortuguese") }; //$NON-NLS-1$
	
	public static final String[] COMPARE_MODES = new String[] {
		"Normal",
		"Revision"
	};

	public static final String[] COMPARE_MODE_TIPNAMES = new String[] {
		"OptionPanel.Comparison_mode_normal_TIP",
		"OptionPanel.Comparison_mode_revision_TIP"
	};

	private JFrame owner = null;
	private JDialog dialog = null;
	
	private Document baseConfigDoc = null;
	private Element baseRootElem = null;
	private Document userConfigDoc = null;
	private Element userRootElem = null;
	
	private String username = null;
	private String password = null;	
	private boolean savePassword = false;
	private String resultLocation = "";
	private String lastSubmissionLocation = "";
	private String countryLanguage = null;
	
	private ServerInfo serverInfo = null;
	
	/**
	 * Contains LanguageSetting elements
	 */
	private HashMap<String, LanguageSetting> languageMap = null;
	private String[] languageNames = null;

	private ATUJPLAG() {
		this.showStartDialog();
		PersistenceService ps = ServiceManager.lookupPersistenceService();
		BasicService bs = ServiceManager.lookupBasicService();

		URL baseURL = bs.getCodeBase();

		URL jplagURL;
		try {
			jplagURL = new URL(baseURL, "JPlagBaseConfig.xml"); //$NON-NLS-1$
			LoginDialog loginDialog;

			owner = new JFrame("JPlag login dialog"); //$NON-NLS-1$
			owner.setLocation(-1000, -1000);
			owner.setVisible(true);

			try {
				FileContents fc = ps.get(jplagURL);
				InputStream xmlStream = fc.getInputStream();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setIgnoringComments(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				baseConfigDoc = builder.parse(xmlStream);
                xmlStream.close();
				baseRootElem = baseConfigDoc.getDocumentElement();
				this.countryLanguage = baseRootElem.getAttribute("lastCountryLanguage");
				if(countryLanguage!=null && !countryLanguage.equals(""))
				    Messages.setBUNDLE_NAME(getCountryLanguageValue());
                
				loginDialog = new LoginDialog(
						baseRootElem.getAttribute("lastUsername"), //$NON-NLS-1$
						baseRootElem.getAttribute("lastPassword"), //$NON-NLS-1$
						true, this, owner);
			} catch(FileNotFoundException e1) {
				// No problem, file does not exist
				loginDialog = new LoginDialog(this, owner);
			} catch(IOException e1) {
				JPlagCreator.showError(owner,Messages.getString(
                    "ATUJPLAG.JPlag_client_error"), //$NON-NLS-1$
					e1.getMessage());
				e1.printStackTrace();
				return;
			} catch(Exception e) {
				System.out.println("Unable to read base settings => Ignored");
				e.printStackTrace();
				baseConfigDoc = null;
				loginDialog = new LoginDialog(this, owner);
			}
			
			if(baseConfigDoc == null) {
				try {
					ps.create(jplagURL, 1024);
					DocumentBuilderFactory docBFac;
					DocumentBuilder docBuild;
					docBFac = DocumentBuilderFactory.newInstance();
					docBuild = docBFac.newDocumentBuilder();
					baseConfigDoc = docBuild.newDocument();
					baseRootElem = baseConfigDoc.createElement("baseConfig");
					baseConfigDoc.appendChild(baseRootElem);
					printBaseInXML();
				}
				catch(Exception e) {
					e.printStackTrace();
					System.out.println("Unable to create base configuration "
							+ "file => Abort");
					return;
				}
			}
			
			dialog.dispose();
			loginDialog.pack();
			loginDialog.setModal(true);
			loginDialog.setLocationRelativeTo(null);
			loginDialog.setVisible(true);
			owner.dispose();
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void showStartDialog() {
		dialog = new JDialog();

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 140));
		panel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.blue, 2));
		dialog.setContentPane(panel);

		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(getClass().getResource("/atujplag/data/biglogo.gif"))); //$NON-NLS-1$
		panel.add(label);
		dialog.setUndecorated(true);
		dialog.getContentPane().setBackground(Color.WHITE);

		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
	
	public boolean login(String username, String password, boolean savePass, Component parent) {
		this.username = username;
		this.password = password;
		this.savePassword = savePass;

		updateServerInfo(parent);
		if(serverInfo == null) return false;
				
		baseRootElem.setAttribute("lastUsername", username); //$NON-NLS-1$
		if(!savePassword && baseRootElem.hasAttribute("lastPassword")) //$NON-NLS-1$
			baseRootElem.removeAttribute("lastPassword"); //$NON-NLS-1$
		else if(savePassword) baseRootElem.setAttribute("lastPassword", password); //$NON-NLS-1$
				
		PersistenceService ps = ServiceManager.lookupPersistenceService();
		BasicService bs = ServiceManager.lookupBasicService();
		URL baseURL = bs.getCodeBase();
		
		languageMap = new HashMap<String,LanguageSetting>();
		LanguageInfo[] langInfos=serverInfo.getLanguageInfos();
		boolean[] found = new boolean[langInfos.length]; //defaults to false
		
		boolean userConfigChanged = false;
		URL userConfigURL = null;
		resultLocation = null;
		lastSubmissionLocation = "";
		userConfigDoc = null;
		userRootElem = null;
		
		try {
			// username has been validated by the server,
			// so the characters are valid
			userConfigURL = new URL(baseURL, "JPlag_"+username+".xml"); //$NON-NLS-1$
			FileContents fc = ps.get(userConfigURL);
			InputStream xmlStream = fc.getInputStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			userConfigDoc = builder.parse(xmlStream);
            xmlStream.close();
			userRootElem = userConfigDoc.getDocumentElement();
			
			resultLocation = userRootElem.getAttribute("resultLocation");
			lastSubmissionLocation = userRootElem.getAttribute("lastSubmissionLocation");
			countryLanguage = userRootElem.getAttribute("countryLanguage");
			
			NodeList langList = userRootElem.getChildNodes();
			for(int i=0;i<langList.getLength();i++) {
				Element langelem = (Element) langList.item(i);
				String name = langelem.getAttribute("name");
				int j;
				for(j=0;j<langInfos.length;j++) {
					if(name.equals(langInfos[j].getName()))
						break;
				}
				if(j==langInfos.length) {
					System.out.println("Language \""+name+"\" is not supported "
							+ "anymore => Ignored");
					continue;
				}
				if(found[j]) {
					System.out.println("Language \""+name+"\" has multiple "
							+ "settings => Last used");
				}
				found[j] = true;
				languageMap.put(name,new LanguageSetting(langelem));
			}
		}
		catch(FileNotFoundException fnfe) {
			try {
				// userConfigURL is valid as new URL cannot cause this exception
				ps.create(userConfigURL, 8192);
				DocumentBuilderFactory docBFac;
				DocumentBuilder docBuild;
				docBFac = DocumentBuilderFactory.newInstance();
				docBuild = docBFac.newDocumentBuilder();
				userConfigDoc = docBuild.newDocument();
				userRootElem = userConfigDoc.createElement("userConfig");
				userConfigDoc.appendChild(userRootElem);
				
				countryLanguage = "English";
				userRootElem.setAttribute("countryLanguage",countryLanguage);	
				userConfigChanged = true;
				printUserInXML();
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.println("Unable to create base configuration "
						+ "file => Abort");
				System.exit(0);
			}
		}
		catch(Exception e) {} // ignore parsing errors
		
		if(userRootElem == null) {
			System.out.println("Unable to open/create user root element!");
			System.exit(0);
		}
		
		if(resultLocation == null || !new File(resultLocation).isDirectory()) {
			WelcomeOptionsDialog welcomeDlg = new WelcomeOptionsDialog(owner);
            welcomeDlg.setVisible(true);
			resultLocation = welcomeDlg.getResultDir();
			if(resultLocation==null || !new File(resultLocation).isDirectory())
				System.exit(0);
			userRootElem.setAttribute("resultLocation",resultLocation);
            setCountryLanguage(welcomeDlg.getLanguage(), false);
			userConfigChanged = true;
		}
		
		//
		// Fill remaining language settings with default values
		// and fill languageNames array
		//
		languageNames = new String[langInfos.length];
		for(int i=0;i<langInfos.length;i++) {
			languageNames[i] = langInfos[i].getName();
			if(!found[i]) {
				System.out.println("New language found: \""
						+ langInfos[i].getName() + "\"");
				LanguageSetting lang = new LanguageSetting(userConfigDoc,
						langInfos[i].getName());
				lang.setSuffixes(langInfos[i].getSuffixes());
				lang.setMinMatchLen(langInfos[i].getDefMinMatchLen());
				languageMap.put(langInfos[i].getName(), lang);
			}
		}
		baseRootElem.setAttribute("lastCountryLanguage", countryLanguage);
		Messages.setBUNDLE_NAME(getCountryLanguageValue());
		printBaseInXML();
		if(userConfigChanged)
			printUserInXML();
		return true;
	}
	
	public String[] getLanguageNames() {
		return languageNames;
	}
	
	public LanguageSetting getLastLanguageSetting() {
		return getLanguageSettingForName(getLastLanguageName());
	}

	// TODO: Save lastLanguage in a private variable
	public String getLastLanguageName() {
		String lastlang = userRootElem.getAttribute("lastLanguage");
		if(lastlang.length()==0 || !languageMap.containsKey(lastlang))
			return serverInfo.getLanguageInfos()[0].getName();
		return lastlang;
	}
	
	public LanguageSetting getLanguageSettingForName(String name) {
		return languageMap.get(name);
	}
	
	public LanguageSetting getDefaultLanguageSettingForName(String name) {
		LanguageInfo[] langInfos=serverInfo.getLanguageInfos();
		for(int i=0;i<langInfos.length;i++) {
			if(name.equals(langInfos[i].getName())) {
				LanguageSetting lang = new LanguageSetting(
						langInfos[i].getName(),
						langInfos[i].getSuffixes(),
						langInfos[i].getDefMinMatchLen());
				return lang;
			}
		}
		return null;
	}
	
	public void updateLastSubmissionInfos(Option option) {
		lastSubmissionLocation = option.getOriginalDir();
		userRootElem.setAttribute("lastSubmissionLocation", lastSubmissionLocation);
		userRootElem.setAttribute("lastLanguage", option.getLanguage());
		
		LanguageSetting lang = languageMap.get(option.getLanguage());
		if(lang == null) {
			System.out.println("Unable to find language settings! Not updated!");
			return;
		}
		
		lang.setClusterType(option.getClustertype());
		lang.setMinMatchLen(option.getMinimumMatchLength());
		lang.setReadSubdirs(option.isReadSubdirs());
		lang.setStoreMatches(option.getStoreMatches());
		lang.setSuffixes(option.getSuffixes());
		printUserInXML();
	}

	public static String generateParserLogString(File file, Component parent) {
		StringBuffer strbuf = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				strbuf.append(line).append('\n');
			}
			reader.close();
		} catch(Exception e) {
			JPlagCreator.showError(parent, Messages.getString("ATUJPLAG.Parser_log_error"), //$NON-NLS-1$
					TagParser.parse(Messages.getString("ATUJPLAG.Parser_log_error_{1_ERROR}_DESC"),//$NON-NLS-1$
							new String[] { e.getMessage() }));
			return null;
		}

		return strbuf.toString();
	}

	public boolean isSavePassword() {
		return savePassword;
	}

	private static void setElemString(Element elem, String name, String str) {
		elem.setAttribute(name, (str == null) ? "" : str); //$NON-NLS-1$
	}

	public static boolean delete(File file) {
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(int i = 0; i < files.length; i++) {
				if(!delete(files[i]))
                {
				    System.out.println("Unable to delete "+files[i]);
                }
			}
		}
		return file.delete();
	}

	public void setCountryLanguage(String newLang, boolean updateXML) {
		countryLanguage = newLang;
		baseRootElem.setAttribute("lastCountryLanguage", newLang);
		userRootElem.setAttribute("countryLanguage", newLang); //$NON-NLS-1$
        if(updateXML)
        {
            printBaseInXML();
            printUserInXML();
        }
		Messages.setBUNDLE_NAME(getCountryLanguageValue());
	}
	
	public String getCountryLanguage() {
		return countryLanguage;
	}

	public String getCountryLanguageValue() {
		String c_lang = countryLanguage;
		if(c_lang.equals(Messages.getString("ATUJPLAG.French"))) //$NON-NLS-1$
			c_lang = "fr"; //$NON-NLS-1$
		else if(c_lang.equals(Messages.getString("ATUJPLAG.German"))) //$NON-NLS-1$
			c_lang = "de"; //$NON-NLS-1$
		else if(c_lang.equals(Messages.getString("ATUJPLAG.BrazilianPortuguese"))) //$NON-NLS-1$
			c_lang = "ptbr"; //$NON-NLS-1$
		else if(c_lang.equals(Messages.getString("ATUJPLAG.Spanish"))) //$NON-NLS-1$
            c_lang = "es"; //$NON-NLS-1$
        else
			c_lang = "en"; //$NON-NLS-1$
		return c_lang;
	}
	
	public String getResultLocation() {
		return resultLocation;
	}
	
	public void setResultLocation(String location) {
		resultLocation = location;
		Element root = userConfigDoc.getDocumentElement();
		root.setAttribute("resultLocation", location);
		printUserInXML();
	}
	
	public String getLastSubmissionLocation() {
		return lastSubmissionLocation;
	}

	public boolean manageResults(int action, String string, int row,
            Component parent) {
		Document doc = getSubmissions().get(row);
		Element elem = (Element) doc.getElementsByTagName("infos").item(0); //$NON-NLS-1$
		String locationName = getResultLocation();
		File location = new File(locationName);
		String oldResDir = elem.getAttribute("title"); //$NON-NLS-1$
		File file = new File(location, oldResDir);

		if(action == ATUJPLAG.RENAME) {
			if(oldResDir.equals(string))
				return false;
			if(string.length() == 0) {
				JPlagCreator.showMessageDialog(Messages
					.getString("ATUJPLAG.Renaming_error"), //$NON-NLS-1$
					Messages.getString("ATUJPLAG.Directory_name_missing"));
				return false;
			}
			while(string.length() != 0 && string.endsWith(".")) //$NON-NLS-1$
				string = string.substring(0, string.length() - 1);
			if(string.length() == 0) {
				JPlagCreator.showMessageDialog(Messages
					.getString("ATUJPLAG.Renaming_error"), //$NON-NLS-1$
					Messages.getString("ATUJPLAG.Invalid_directory_name"));
				return false;
			}
			if(!file.exists()) {
				JPlagCreator.showError(parent, Messages.getString(
                        "ATUJPLAG.Renaming_error"), //$NON-NLS-1$
					TagParser.parse(Messages
						.getString("ATUJPLAG.File_does_not_exist_{1_PATH}"), //$NON-NLS-1$
						new String[] { file.getPath() }));
				return false;
			}
			File newFile = new File(location, string);
			if(newFile.exists()) {
				JPlagCreator.showError(parent, Messages.getString(
						"ATUJPLAG.Directory_already_exists"), //$NON-NLS-1$
					TagParser.parse(Messages.getString(
							"ATUJPLAG.Directory_already_exists_{1_NAME}_{2_PATH}_DESC"), //$NON-NLS-1$
						new String[] { newFile.getName(), location.getPath() }));
				return false;
			}
			file.renameTo(newFile);
			elem.setAttribute("title", string); //$NON-NLS-1$
			printInXml(new File(newFile, "result.xml"), doc); //$NON-NLS-1$
			return true;
		} else if(action == ATUJPLAG.DELETE) {
			int confirm = JPlagCreator.showConfirmDialog(Messages
				.getString("ATUJPLAG.Confirm_delete"), //$NON-NLS-1$
				TagParser.parse(Messages
					.getString("ATUJPLAG.Confirm_delete_{1_TITLE}_DESC"), //$NON-NLS-1$
					new String[] { elem.getAttribute("title") })); //$NON-NLS-1$ 
			if(confirm == JOptionPane.YES_OPTION) {
				delete(file);
				return true;
			}
		}

		return false;
	}

	/**
	 * Writes the given XML document into the given XML file
	 */
	private static synchronized void printInXml(File file, Document doc) {
        FileOutputStream os = null;
		try {
			Source source = new DOMSource(doc);
			os = new FileOutputStream(file);
			// Prepare the output file
			Result result = new StreamResult(os);
			// Write the DOM document to the file
			Transformer xformer;

			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(TransformerConfigurationException e) {
			e.printStackTrace();
		} catch(TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch(TransformerException e) {
			e.printStackTrace();
        }
        finally {
            try { if(os!=null) os.close(); } catch(Exception e) {}
        }
	}

	private synchronized void printUserInXML() {
		PersistenceService ps = ServiceManager.lookupPersistenceService();
		BasicService bs = ServiceManager.lookupBasicService();

		Source source = new DOMSource(userConfigDoc);
		OutputStream os = null;
		try {
			URL baseURL = bs.getCodeBase();
			URL jplagURL = new URL(baseURL, "JPlag_"+username+".xml"); //$NON-NLS-1$ //$NON-NLS-2$

			FileContents fc = ps.get(jplagURL);
			os = fc.getOutputStream(true);

			// Prepare the output file
			Result result = new StreamResult(os);

			// Write the DOM document to the file
			Transformer xformer;

			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
        finally {
            try { if(os!=null) os.close(); } catch(Exception e) {}
        }
	}
	
	private synchronized void printBaseInXML() {
		PersistenceService ps = ServiceManager.lookupPersistenceService();
		BasicService bs = ServiceManager.lookupBasicService();

		Source source = new DOMSource(baseConfigDoc);
		OutputStream os = null;
		try {
			URL baseURL = bs.getCodeBase();
			URL jplagURL = new URL(baseURL, "JPlagBaseConfig.xml"); //$NON-NLS-1$

			FileContents fc = ps.get(jplagURL);
			os = fc.getOutputStream(true);

			// Prepare the output file
			Result result = new StreamResult(os);

			// Write the DOM document to the file
			Transformer xformer;

			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
        finally {
            try { if(os!=null) os.close(); } catch(Exception e) {}
        }
	}

	public Vector<Document> getSubmissions() {
		Vector<Document> vector = new Vector<Document>();
		File[] files = new File(getResultLocation()).listFiles();
        if(files == null) {
            System.out.println("\'" + getResultLocation()
                + "\' is not a directory!");
            return vector;
        }
		File f = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch(ParserConfigurationException e) {
			System.out.println("Unable to produce a document builder!");
			e.printStackTrace();
			return vector;
		}
		for(int i = 0; i < files.length; i++) {
			if(files[i].exists() && files[i].isDirectory()
					&& ((f = new File(files[i], "result.xml")).exists())) { //$NON-NLS-1$
                FileInputStream xmlStream = null;
				try {
					xmlStream = new FileInputStream(f);
					Document doc = builder.parse(xmlStream);
					Element elem = (Element) doc.getElementsByTagName("infos") //$NON-NLS-1$
						.item(0);
					// If directory has been renamed, update title attribute
					if(!elem.getAttribute("title").equals(files[i].getName())) { //$NON-NLS-1$
						elem.setAttribute("title", files[i].getName()); //$NON-NLS-1$
						printInXml(f, doc);
					}
					vector.add(doc);
				} catch(FileNotFoundException e) {
					e.printStackTrace();
				} catch(SAXException e) {
					System.out.println("SAXException caused by file: " + f);
					e.printStackTrace();
				} catch(IOException e) {
                    System.out.println("IOException caused by file: " + f);
					e.printStackTrace();
				} catch(Exception e) {
                    System.out.println("Exception caused by file: " + f);
				    e.printStackTrace();
                }
                finally {
                    try { if(xmlStream!=null) xmlStream.close(); }
                    catch(Exception e) {}
                }
			}
		}
		return vector;
	}

	public void updateServerInfo(Component parent) {
		serverInfo = SimpleClient.serverInfo(this, parent);
	}
	
	public ServerInfo getServerInfo() {
		return serverInfo;
	}
	
	public String getUsername() {
		return username;
	}
	
	/**
	 * Only updates the internal username value!
	 */
	public void setUsername(String newusername) {
		username = newusername;
	}
	
	public String getPassword() {
		return password;
	}
	
	/**
	 * Only updates the internal password value!
	 */
	public void setPassword(String newpassword) {
		password = newpassword;
	}
	
	/**
	 * Sets the password which is to be entered automatically into the login
	 * dialog. If remember is false, the password will be set to empty.
	 */
	public void setRememberPassword(boolean remember) {
		setElemString(baseRootElem, "lastPassword", remember ? password : null); //$NON-NLS-1$
		savePassword = remember;
		printBaseInXML();
	}

	public LoginDialog switchUser(JFrame owner) {
		LoginDialog def = new LoginDialog(username, "", false, this, owner); //$NON-NLS-1$

		String str = baseRootElem.getAttribute("lastPassword"); //$NON-NLS-1$
		boolean bol = str.length()!=0;
		def.setSavePassword(bol);
		def.pack();
		def.setModal(true);
		def.setLocationRelativeTo(null);
		def.setVisible(true);
		return def;
	}

	/**
	 * @param doc is the Document of the "result.xml" file of the given
	 * 		submission for which the parameters are to be loaded into a
	 * 		SimpleClient object
	 */
	public SimpleClient loadClient(Document doc) {
		Element elem = (Element) doc.getElementsByTagName("infos").item(0); //$NON-NLS-1$
		SimpleClient client = new SimpleClient(elem.getAttribute("title"), this); //$NON-NLS-1$
		client.setSubmissionDirectory(elem.getAttribute("source")); //$NON-NLS-1$

		Option options = client.getOptions();
		String compMode = elem.getAttribute("comparison_mode"); //$NON-NLS-1$
		if(compMode.length() != 0)
			options.setComparisonMode(Integer.parseInt(compMode));
		options.setBasecodeDir(elem.getAttribute("basecode_dir")); //$NON-NLS-1$
		options.setClustertype(elem.getAttribute("clustertype")); //$NON-NLS-1$
		options.setCountryLang(elem.getAttribute("country_lang")); //$NON-NLS-1$
		options.setLanguage(elem.getAttribute("language_name")); //$NON-NLS-1$
		options.setMinimumMatchLength(Integer.parseInt(elem.getAttribute("min_token"))); //$NON-NLS-1$
		options.setPathToFiles(elem.getAttribute("path_to_files")); //$NON-NLS-1$
		boolean read = elem.getAttribute("read_subdirs").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$
		options.setReadSubdirs(read);
		options.setStoreMatches(elem.getAttribute("store_matches")); //$NON-NLS-1$
		options.setTitle(elem.getAttribute("title")); //$NON-NLS-1$
		String suffixes = elem.getAttribute("suffixes"); //$NON-NLS-1$
		options.setSuffixes(suffixes.split(",")); //$NON-NLS-1$

		return client;
	}

	public OptionPanel changeSubmissionValues(Document doc, View view) {
		SimpleClient client = loadClient(doc);
		return new OptionPanel(client, view);
	}

    static final char[] hexchars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    
    /**
     * Escapes special characters not suitable for URLs with "%xx"
     * @param str String to be encoded in UTF-8 format
     * @return For URL encoded string
     */
    public static String encodeForURL(String str) {
        StringBuffer result = new StringBuffer();
        for(int i=0;i<str.length();i++) {
            char ch = str.charAt(i);
            if(ch>='A' && ch<='Z' || ch>='a' && ch<='z' || ch>='0' && ch<='9')
                result.append(ch);
            else {
                switch(ch) {
                    case '-':
                    case '_':
                    case '.':
                    case '!':
                    case '~':
                    case '*':
                    case '\'':
                    case '(':
                    case ')':
                        result.append(ch);
                        break;
                    default:
                        result.append('%');
                        result.append(hexchars[(ch&0xf0)>>4]);
                        result.append(hexchars[ch&0x0f]);
                        break;
                }
            }
        }
        return result.toString();
    }

    /**
     * Escapes special characters not suitable for path URLs with "%xx"
     * @param str Path string to be encoded in UTF-8 format
     * @return For URL encoded path string
     */
    public static String encodePathForURL(String str) {
        StringBuffer result = new StringBuffer();
        for(int i=0;i<str.length();i++) {
            char ch = str.charAt(i);
            if(ch>='A' && ch<='Z' || ch>='a' && ch<='z' || ch>='0' && ch<='9')
                result.append(ch);
            else {
                switch(ch) {
                    case '-':
                    case '_':
                    case '.':
                    case '!':
                    case '~':
                    case '*':
                    case '\'':
                    case '(':
                    case ')':
                    case ':':
                    case '/':
                    case '\\':
                        result.append(ch);
                        break;
                    default:
                        result.append('%');
                        result.append(hexchars[(ch&0xf0)>>4]);
                        result.append(hexchars[ch&0x0f]);
                        break;
                }
            }
        }
        return result.toString();
    }

	public static void show(File file) {
		if (DesktopUtils.isBrowseSupported()) {
			DesktopUtils.openWebpage(file.toURI());
		} else {
			JPlagCreator.showMessageDialog(Messages.getString("ATUJPLAG.JPlag_warning"), //$NON-NLS-1$
					TagParser.parse(Messages.getString("ATUJPLAG.JPlag_warning_{1_URL}_DESC"), //$NON-NLS-1$ 
							new String[] { file.toString() }));
		}

	}

	public int getCountryLanguageIndex() {
		String lang = getCountryLanguage();
		for(int i = 0; i < COUNTRY_LANGUAGES.length; i++) {
			if(lang.equals(COUNTRY_LANGUAGES[i]))
				return i;
		}
		System.out.println("Current country language \"" + lang + "\" doesn't match any supported language!");
		return 0;
	}

	public String findNextUnusedTitle() {
		String untitled = "Untitled"; //$NON-NLS-1$
		String result = ""; //$NON-NLS-1$
		File f = new File(getResultLocation());
		for(int i = 1; i < 100000; i++) {
			result = untitled + i;
			if(!new File(f, result).exists())
				return result;
		}
		return untitled;
	}

	public static void main(String[] args) {
		new ATUJPLAG();
	}
}