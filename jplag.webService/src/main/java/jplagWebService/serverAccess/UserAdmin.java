package jplagWebService.serverAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

import jplagUtils.PropertiesLoader;
import jplagWebService.server.FinishRequestData;
import jplagWebService.server.JPlagException;
import jplagWebService.server.MailTemplate;
import jplagWebService.server.RequestData;
import jplagWebService.server.UserData;
import jplagWebService.server.UserDataArray;
import jplagWebService.server.UserInfo;
import jplagWebService.serverImpl.JPlagCentral;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UserAdmin extends Thread {

	private static final long ONEDAY = 86400000;

	private boolean doStopUserAdmin = false;

	public static final int USER_INVALID = 0;
	public static final int USER_NORMAL = 1;
	//	public static final int USER_EXPIRED = 5;
	//	public static final int USER_DEACTIVATED = 10;
	public static final int USER_GROUPADMIN = 64;
	public static final int USER_JPLAGADMIN = 192;
	public static final int USER_JPLAGADMINNOTIFY = 224; // (email notify)
	public static final int USER_SERVERPAGE = 256;

	public static final int MASK_DEVELOPER = 2;
	public static final int MASK_EXPIRED = 4;
	public static final int MASK_DEACTIVATED = 8;
	public static final int MASK_REQUESTNOTIFY = 32;
	public static final int MASK_ANYADMIN = 64;
	public static final int MASK_JPLAGADMIN = 128;
	public static final int MASK_SERVERPAGE = 256;
	public static final int MASK_NOAUTOASKEXTEND = 512;

	public static final String JPLAG_SERVER = "https://jplag.ipd.kit.edu/";

	private Document doc = null;
	private Element rootElement = null;

	private File userFile = null;

	private PrintWriter logWriter = null;
	private PrintWriter mailLogWriter = null;

	private HashMap<String, PassAndState> userPassList = new HashMap<String, PassAndState>();
	private Object userPassListMutex = new Object();

	private class PassAndState {
		public String password;
		public int state;

		public PassAndState(String pass, int stat) {
			password = pass;
			state = stat;
		}
	}

	private RequestAdmin requestAdmin = null;

	public UserAdmin(String jplagHome) {
		requestAdmin = new RequestAdmin(jplagHome);
		userFile = new File(jplagHome + File.separator + "jplag_users.xml");
		if (!userFile.exists())
			create();
		else
			parse();
		try {
			logWriter = new PrintWriter(new FileWriter(new File(jplagHome + File.separator + "userAdmin.log"), true), true);
			mailLogWriter = new PrintWriter(new FileWriter(new File(jplagHome + File.separator + "userAdminMail.log"), true), true);
		} catch (java.io.IOException ex) {
			System.out.println("UserAdmin: Unable to open log files!");
		}
	}

	public RequestAdmin getRequestAdmin() {
		return requestAdmin;
	}

	private synchronized void log(String str) {
		if (logWriter != null)
			logWriter.println(str);
	}

	/**
	 * Writes the "jplag-users" data to an XML file
	 */
	private synchronized void writeXMLFile() {
		try {
			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);

			// Prepare the output file
			Result result = new StreamResult(userFile);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
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
		rootElement = doc.createElement("jplag-users");
		doc.appendChild(rootElement);
		writeXMLFile();
	}

	/**
	 * Loads an existing "jplag-user database" into memory
	 */
	private void parse() {
		try {
			FileInputStream xmlStream = new FileInputStream(userFile);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(xmlStream);
			xmlStream.close();
			rootElement = doc.getDocumentElement();

			NodeList userList = rootElement.getElementsByTagName("user");
			System.out.println("Number of users: " + userList.getLength());

			synchronized (userPassListMutex) {
				// avoid rehash operations during initialisation
				userPassList = new HashMap<String, PassAndState>((int) (userList.getLength() * 1.4));
				for (int j = 0; j < userList.getLength(); j++) {
					Element user = (Element) userList.item(j);
					userPassList.put(user.getAttribute("username"),
							new PassAndState(user.getAttribute("password"), parseInt(user, "state")));
				}
			}
		} catch (javax.xml.parsers.ParserConfigurationException e) {
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
	 * Checks whether the given user already exists
	 */
	public boolean exists(String username) {
		boolean ret;
		synchronized (userPassListMutex) {
			ret = userPassList.containsKey(username);
		}
		return ret;
	}

	/**
	 * Checks whether the given username is valid
	 */
	public boolean isValidUsername(String username) {
		return Pattern.matches("^[\\w\\@\\.]+$", username);
	}

	public int getLoginState(String username, String password) {
		synchronized (userPassListMutex) {
			if (userPassList.containsKey(username)) {
				PassAndState pas = (PassAndState) userPassList.get(username);
				if (!pas.password.equals(password))
					return USER_INVALID;
				return pas.state;
			}
		}
		return USER_INVALID;
	}

	/**
	 * Returns the user XML element for the given username
	 */
	private Element getUserXMLElement(String username) {
		NodeList userList = rootElement.getElementsByTagName("user");
		for (int i = 0; i < userList.getLength(); i++) {
			Element elem = (Element) userList.item(i);
			if (elem.getAttribute("username").equals(username))
				return elem;
		}
		return null;
	}

	private void setString(Element elem, String attrname, String str) {
		String old = elem.getAttribute(attrname);
		if (old.equals(str))
			return;
		log("   " + attrname + ": " + old + " -> " + str);
		elem.setAttribute(attrname, str);
	}

	private void setNillableString(Element elem, String attrname, String str) {
		if (str == null || str.length() == 0) {
			if (elem.hasAttribute(attrname)) {
				log("   " + attrname + ": " + elem.getAttribute(attrname) + " gets deleted");
				elem.removeAttribute(attrname);
			}
		} else
			setString(elem, attrname, str);
	}

	private String parseNillableString(Element elem, String attrname) {
		String str = elem.getAttribute(attrname);
		if (str.length() == 0)
			return null;
		else
			return str;
	}

	private static String formatCalendar(Calendar cal) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
		return df.format(cal.getTime());
	}

	private void setNillableCalendar(Element user, String attr, Calendar cal) {
		setNillableString(user, attr, (cal == null) ? null : formatCalendar(cal));
	}

	private static Calendar parseCalendar(String str) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		try {
			cal.setTime(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN).parse(str));
		} catch (java.text.ParseException ex) {
			System.out.println("Illegal date: \"" + str + "\"");
			cal.set(1970, 0, 1); // set an error date (1.1.1970)
			System.out.println("Set to " + formatCalendar(cal));
		}
		return cal;
	}

	private static Calendar parseCalendar(Element elem, String attrname) {
		return parseCalendar(elem.getAttribute(attrname));
	}

	private static Calendar parseNillableCalendar(String str) {
		if (str.length() == 0)
			return null;
		else
			return parseCalendar(str);
	}

	public static Calendar parseNillableCalendar(Element elem, String attrname) {
		return parseNillableCalendar(elem.getAttribute(attrname));
	}

	public static int parseInt(Element elem, String attrname) {
		int val;
		try {
			val = Integer.parseInt(elem.getAttribute(attrname));
		} catch (NumberFormatException ex) {
			System.out.println("Illegal " + attrname + " int: \"" + elem.getAttribute(attrname) + "\"");
			val = -1;
		}
		return val;
	}

	public synchronized int getState(String name) {
		Element user = getUserXMLElement(name);
		int val = parseInt(user, "state");
		if (val < 0)
			return USER_INVALID;
		else
			return val;
	}

	public synchronized UserInfo getUserInfo(String username, int leftSlots) {
		Element elem = getUserXMLElement(username);
		Calendar expires = parseNillableCalendar(elem, "expires");
		String email = elem.getAttribute("email");
		String emailSecond = parseNillableString(elem, "emailSecond");
		String homepage = parseNillableString(elem, "homepage");
		return new UserInfo(leftSlots, expires, email, emailSecond, homepage);
	}

	public synchronized void updateUserInfo(String username, String newpassword, String newemailsecond, String newhomepage)
			throws JPlagException {
		Element elem = (Element) getUserXMLElement(username);

		log("[" + new Date() + "] " + username + " called updateUserInfo:");

		if (newpassword != null) {
			if (newpassword.length() < 6)
				throw new JPlagException("updateUserInfo", "The password is too short!", "The password must have at least 6 characters!");
			setString(elem, "password", newpassword);
			synchronized (userPassListMutex) {
				userPassList.put(username, new PassAndState(newpassword, parseInt(elem, "state")));
			}
		}

		if (newemailsecond != null)
			setNillableString(elem, "emailSecond", newemailsecond);

		if (newhomepage != null)
			setNillableString(elem, "homepage", newhomepage);

		writeXMLFile();
	}

	/**
	 * If isGroupAdmin is false, it returns a list of all users.<br>
	 * If isGroupAdmin is true, it returns a list of all users he created with
	 * the password set to an empty string.<br>
	 * 
	 * @return A list of userdata elements in form of an ArrayOfUserData element
	 */
	public synchronized UserDataArray getUserDataArray(String calling_username, boolean isGroupAdmin) throws JPlagException {
		NodeList userList = rootElement.getElementsByTagName("user");
		int num;
		if (isGroupAdmin) {
			num = 0;
			for (int i = 0; i < userList.getLength(); i++)
				if (((Element) userList.item(i)).getAttribute("createdBy").equals(calling_username))
					num++;
		} else
			num = userList.getLength();
		UserData[] data = new UserData[num];
		for (int i = 0, j = 0; i < userList.getLength(); i++) {
			Element elem = (Element) userList.item(i);
			if (!isGroupAdmin || elem.getAttribute("createdBy").equals(calling_username)) {
				data[j] = new UserData();
				data[j].setUsername(elem.getAttribute("username"));
				data[j].setPassword(isGroupAdmin ? "" : elem.getAttribute("password"));

				data[j].setCreated(parseCalendar(elem, "created"));
				data[j].setCreatedBy(elem.getAttribute("createdBy"));
				data[j].setExpires(parseNillableCalendar(elem, "expires"));
				data[j].setLastUsage(parseNillableCalendar(elem, "lastUsage"));

				data[j].setNumOfSubs(parseInt(elem, "numOfSubs"));
				data[j].setRealName(elem.getAttribute("realname"));
				data[j].setEmail(elem.getAttribute("email"));
				data[j].setEmailSecond(parseNillableString(elem, "emailSecond"));
				data[j].setHomepage(parseNillableString(elem, "homepage"));
				data[j].setReason(elem.getAttribute("reason"));
				data[j].setNotes(parseNillableString(elem, "notes"));
				data[j].setState(parseInt(elem, "state"));
				j++;
			}
		}
		return new UserDataArray(data);
	}

	/**
	 * This function only creates a new user element. It does only initialise
	 * "createdBy", "numOfSubs" and "state"!
	 */
	private Element getNewUserElement(String creator) {
		Element newuser = doc.createElement("user");
		newuser.setAttribute("createdBy", creator);
		newuser.setAttribute("numOfSubs", "0");
		newuser.setAttribute("state", USER_NORMAL + "");
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		newuser.setAttribute("created", formatCalendar(cal));

		return newuser;
	}

	/**
	 * Sets userdata fields, adds a new user or deletes one. Group admins can
	 * only change their "own" users and can not change the "createdBy" and
	 * "state" fields. For JPlag admins the "createdBy" field has to point to a
	 * valid user!
	 */
	public synchronized void setUserData(UserData data, String oldUsername, String calling_username, boolean isGroupAdmin)
			throws JPlagException {
		Element user;

		log("[" + new Date() + "] " + calling_username + " called setUserData " + "for user " + oldUsername + " :");

		if (oldUsername == null) // create a new user?
		{
			if (data.getUsername() == null)
				throw new JPlagException("setUserData", "Illegal parameters! " + "oldUsername and username is null!", "Find out what "
						+ "you want, create an user or delete one!");

			if (!isValidUsername(data.getUsername()))
				throw new JPlagException("setUserData", "Illegal new username!", "The username may only consist of the "
						+ "following characters: 'A'-'Z', 'a'-'z', '0'-'9', " + "'@' and '.'");

			if (exists(data.getUsername()))
				throw new JPlagException("setUserData", "Adding a new user " + "with an already existing name!", "Try another name!");

			log("  " + calling_username + " generates new user " + data.getUsername());
			user = getNewUserElement(calling_username);
		} else
			user = getUserXMLElement(oldUsername);
		if (user == null)
			throw new JPlagException("setUserData", "User doesn't exist (anymore?)!", "Please update the user list!");

		if (data.getUsername() == null) // delete user username?
		{
			if (isGroupAdmin && !calling_username.equals(user.getAttribute("createdBy"))) {
				// log invalid access, wait a bit and throw an exception
				System.out.println("setUserData: group admin " + calling_username + " tried to delete user " + oldUsername + " on "
						+ new Date());
				try {
					Thread.sleep(10);
				} catch (Exception ex) {
				}
				throw new JPlagException("setUserData", "You are not allowed" + "to delete this user!", "You can only delete users you"
						+ " created! This has been logged.");
			}
			log("  deletes user " + oldUsername + " (real name: " + user.getAttribute("realname") + " email: " + user.getAttribute("email")
					+ ")");
			synchronized (userPassListMutex) {
				userPassList.remove(oldUsername);
			}
			AccessStructure[] accStructs = JPlagCentral.listAccessStructures(oldUsername);
			for (int i = 0; i < accStructs.length; i++)
				JPlagCentral.cancelSubmission(accStructs[i]);
			rootElement.removeChild(user);
		} else {
			if (isGroupAdmin && !calling_username.equals(user.getAttribute("createdBy"))) {
				// log invalid access, wait a bit and throw an exception
				System.out.println("setUserData: group admin " + calling_username + " tried to change fields for user "
						+ data.getUsername() + " on " + new Date());
				try {
					Thread.sleep(10);
				} catch (Exception ex) {
				}
				throw new JPlagException("setUserData", "You are not allowed" + "to change this user!", "You can only change users you"
						+ " created! This has been logged.");
			}
			if (!user.getAttribute("username").equals(data.getUsername())) {
				if (!isValidUsername(data.getUsername()))
					throw new JPlagException("setUserData", "Illegal new username!", "The username may only consist of the "
							+ "following characters: 'A'-'Z', 'a'-'z', " + "'0'-'9', '@' and '.'");

				if (exists(data.getUsername()))
					throw new JPlagException("setUserData", "Changing an username to an already existing name!", "Try another name!");

				setString(user, "username", data.getUsername());
				synchronized (userPassListMutex) {
					userPassList.remove(oldUsername);
					userPassList.put(data.getUsername(), new PassAndState(user.getAttribute("password"), parseInt(user, "state")));
				}
			}
			if (data.getPassword().length() != 0 && !user.getAttribute("password").equals(data.getPassword())) {
				setString(user, "password", data.getPassword());
				synchronized (userPassListMutex) {
					userPassList.put(user.getAttribute("username"), new PassAndState(data.getPassword(), parseInt(user, "state")));
				}
			}
			setNillableCalendar(user, "expires", data.getExpires());
			setString(user, "realname", data.getRealName());
			setString(user, "email", data.getEmail());
			setNillableString(user, "emailSecond", data.getEmailSecond());
			setNillableString(user, "homepage", data.getHomepage());
			setString(user, "reason", data.getReason());
			setNillableString(user, "notes", data.getNotes());
			if (!isGroupAdmin) {
				setString(user, "created", formatCalendar(data.getCreated()));
				setString(user, "createdBy", data.getCreatedBy());
				setNillableCalendar(user, "lastUsage", data.getLastUsage());
				setString(user, "numOfSubs", data.getNumOfSubs() + "");
				if (parseInt(user, "state") != data.getState()) {
					setString(user, "state", data.getState() + "");
					synchronized (userPassListMutex) {
						userPassList.put(user.getAttribute("username"), new PassAndState(user.getAttribute("password"), data.getState()));
					}
				}
			} else {
				if (parseInt(user, "state") != data.getState()) {
					switch (data.getState() & ~MASK_DEVELOPER) {
					case USER_NORMAL:
					case MASK_EXPIRED:
					case MASK_DEACTIVATED:
						setString(user, "state", data.getState() + "");
						synchronized (userPassListMutex) {
							userPassList.put(user.getAttribute("username"),
									new PassAndState(user.getAttribute("password"), data.getState()));
						}
						break;
					default:
						// log illegal state
						System.out.println("setUserData: group admin " + calling_username + " tried to set " + data.getUsername()
								+ "'s state to " + data.getState() + " on " + new Date());
						break;
					}
				}
			}
			if (oldUsername == null) // create new user?
				rootElement.appendChild(user);
			checkExpired(user);
		}
		writeXMLFile();
	}

	private boolean updateCalendar(Element elem) {
		Calendar curcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		Calendar cal = parseCalendar(elem, "lastUsage");
		if (curcal.get(Calendar.DAY_OF_MONTH) != cal.get(Calendar.DAY_OF_MONTH) || curcal.get(Calendar.MONTH) != cal.get(Calendar.MONTH)
				|| curcal.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
			elem.setAttribute("lastUsage", formatCalendar(curcal));
			return true;
		}
		return false;
	}

	public synchronized void updateLastUsage(String username) {
		Element elem = getUserXMLElement(username);
		if (updateCalendar(elem))
			writeXMLFile();
	}

	public synchronized void incrementSubmissionCounter(String username) {
		Element elem = getUserXMLElement(username);
		int subs = parseInt(elem, "numOfSubs");
		elem.setAttribute("numOfSubs", (subs + 1) + "");
		updateCalendar(elem); // just in case a client runs a few months ;)
		writeXMLFile();
	}

	/**
	 * Called by an AdminTool client from a user with administrator rights.
	 * Accepts or declines a specified request
	 */
	public synchronized boolean finishAccountRequest(FinishRequestData data, String adminname) throws JPlagException {
		RequestData rd = requestAdmin.getRequestData(data.getOldUsername());
		if (rd == null)
			throw new JPlagException("requestAccount", "Request doesn't exist (anymore)!",
					"Wrong username or this request has already been decided!");

		if (data.getPassword() != null) // user not dismissed?
		{
			// User accepted, add to user database
			UserData ud = new UserData(data.getUsername(), data.getPassword(), new GregorianCalendar(TimeZone.getTimeZone("GMT")),
					adminname, data.getExpires(), null, 0, data.getRealName(), data.getEmail(), data.getEmailSecond(), data.getHomepage(),
					data.getReason(), data.getNotes(), data.getState());
			setUserData(ud, null, adminname, false);
		}

		if (data.getMailSubject() != null && data.getMailSubject().length() != 0) {
			// inform user about acception or dismiss
			sendMail(data.getEmail(), data.getMailSubject(), data.getMailMessage());
		}

		// Remove request from request list
		requestAdmin.removeRequest(data.getOldUsername());
		return true;
	}

	/**
	 * Called by the server page. - If everything but username is null, it
	 * returns, whether the username is OK (true) or it is already used (false).
	 * - If everything but password is null and password is the email validation
	 * code sent to the user, the request becomes available to the AdminTool and
	 * an email is sent to the administrator, if the code is wrong, false is
	 * returned. - If username, password, realname, email and reason is not
	 * null, it saves the data and sends a validation email to the user. -
	 * Otherwise an exception is thrown.
	 */
	public synchronized boolean requestAccount(RequestData data) throws JPlagException {
		if (data.getRealName() == null && data.getEmail() == null && data.getEmailSecond() == null && data.getHomepage() == null
				&& data.getReason() == null && data.getNotes() == null) {
			if (data.getPassword() == null && data.getUsername() != null) {
				if (exists(data.getUsername()))
					return false;
				else
					return !requestAdmin.exists(data.getUsername());
			} else if (data.getPassword() != null && data.getUsername() == null) {
				String username = requestAdmin.validateRequest(data.getPassword());
				RequestData rd = requestAdmin.getRequestData(username);

				// Send notification messages to all administrators with
				// notification to make them notice the new validated request

				NodeList userList = rootElement.getElementsByTagName("user");
				for (int i = 0; i < userList.getLength(); i++) {
					Element elem = (Element) userList.item(i);
					if ((parseInt(elem, "state") & MASK_REQUESTNOTIFY) != 0) {
						sendMail(elem.getAttribute("email"), MailTemplateAdmin.SERVER_REQUESTNOTIFY, rd);
					}
				}
				return true;
			}
		}

		if (data.getUsername() == null || data.getPassword() == null || data.getRealName() == null || data.getEmail() == null
				|| data.getReason() == null)
			throw new JPlagException("requestAccount", "Username, password, " + "real name, primary email or reason is missing!",
					"Check your input!");

		if (!isValidUsername(data.getUsername()))
			throw new JPlagException("requestAccount", "Illegal new username!", "The username may only consist of the "
					+ "following characters: 'A'-'Z', 'a'-'z', " + "'0'-'9', '@' and '.'");

		if (exists(data.getUsername()) || requestAdmin.exists(data.getUsername()))
			throw new JPlagException("requestAccount", "Username already used!", "Seems like somebody else was faster ;)");

		// Everything's fine, so add request to unverificated request list
		// and send a verification mail

		String vericode = requestAdmin.addRequest(data);
		sendMail(data.getEmail(), MailTemplateAdmin.SERVER_VERIFICATION, data, "code", vericode);

		return true;
	}

	public void extendAccount(String extendCode) throws JPlagException {
		if (extendCode.length() >= 12) // username must be at least one character
		{
			String username = extendCode.substring(11);
			System.out.println("[" + new Date() + "] extendAccount: username=" + username + " code=" + extendCode);

			Element userElem = getUserXMLElement(username);
			if (userElem != null) {
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				if (userElem.getAttribute("askExtendCode").equals(extendCode)) {
					cal.add(Calendar.YEAR, 1);
					setNillableCalendar(userElem, "expires", cal);
					userElem.removeAttribute("expWarned");
					userElem.removeAttribute("askExtendCode");
					return;
				} else {
					Calendar expires = parseNillableCalendar(userElem, "expires");
					if (expires != null) {
						Calendar next2Week = (Calendar) cal.clone();
						next2Week.add(Calendar.DATE, 14);
						if (expires.after(next2Week)) {
							throw new JPlagException("extendAccount", "Your account has already been extended!",
									"You probably opened this page more than once and missed the result of the first time.");
						}
					}
				}
			}
		}
		throw new JPlagException("extendAccount", "Wrong extend code given: " + extendCode,
				"Please check the correct spelling of the code!");
	}

	/**
	 * Sends a specified email to all developers
	 */
	public void notifyDevelopers(String subject, String message) throws JPlagException {
		NodeList userList = rootElement.getElementsByTagName("user");
		for (int i = 0; i < userList.getLength(); i++) {
			Element elem = (Element) userList.item(i);
			if ((parseInt(elem, "state") & MASK_DEVELOPER) != 0)
				sendMail(elem.getAttribute("email"), subject, message);
		}
	}

	/**
	 * Sets the developer flag for the specified user according to developer
	 */
	public synchronized void setDeveloperState(String username, boolean developer) throws JPlagException {
		Element user = getUserXMLElement(username);
		if (user == null)
			throw new JPlagException("setDeveloperState", "User doesn't exist (anymore?)!", "Please update the user list!");

		int state = parseInt(user, "state");

		if (developer) {
			if ((state & MASK_DEVELOPER) != 0)
				throw new JPlagException("setDeveloperState", "You are already marked as a developer!", "Nothing changed.");
			state |= MASK_DEVELOPER;
		} else {
			if ((state & MASK_DEVELOPER) == 0)
				throw new JPlagException("setDeveloperState", "You are not marked as a developer!", "Nothing changed.");
			state &= ~MASK_DEVELOPER;
		}
		user.setAttribute("state", state + "");
		synchronized (userPassListMutex) {
			PassAndState pas = (PassAndState) userPassList.get(username);
			pas.state = state;
		}
		writeXMLFile();
	}

	public void stopUserAdmin() {
		doStopUserAdmin = true;
	}

	/**
	 * 
	 * @param elem
	 *            User element to be checked
	 * @param cal
	 *            Current date as calendar
	 * @param next2Week
	 *            Date after which a expiration notice should be sent
	 */
	private void checkExpired(Element elem, Calendar cal, Calendar next2Week) {
		Calendar expires = parseNillableCalendar(elem, "expires");
		if (expires != null) {
			int state = parseInt(elem, "state");
			if ((state & (MASK_EXPIRED | MASK_DEACTIVATED)) == 0) {
				if (cal.after(expires)) {
					System.out.println("[" + new Date() + "] User " + elem.getAttribute("username") + " expired (expires="
							+ elem.getAttribute("expires") + ")");
					elem.setAttribute("state", (state | MASK_EXPIRED) + "");
					synchronized (userPassListMutex) {
						PassAndState pas = (PassAndState) userPassList.get(elem.getAttribute("username"));
						pas.state = state | MASK_EXPIRED;
					}
					try {
						sendMail(elem.getAttribute("email"), MailTemplateAdmin.SERVER_EXPIRED, elem);
					} catch (JPlagException ex) {
						System.out.println("Unable to send \"expired\" mail (" + ex.getExceptionType() + ": " + ex.getDescription() + "\n"
								+ ex.getRepair());
						ex.printStackTrace();
					}
				} else if (next2Week.after(expires)) {
					if (!elem.hasAttribute("expWarned")) {
						if ((state & MASK_NOAUTOASKEXTEND) == 0 && parseInt(elem, "numOfSubs") != 0) {
							System.out.println("[" + new Date() + "] User " + elem.getAttribute("username")
									+ " expires within two weeks! Asking for extend.");
							try {
								String extendCode = RequestAdmin.getRandomCode() + elem.getAttribute("username");
								sendMail(elem.getAttribute("email"), MailTemplateAdmin.SERVER_ASKEXTEND, elem, "code", extendCode);
								elem.setAttribute("expWarned", "");
								elem.setAttribute("askExtendCode", extendCode);
							} catch (JPlagException ex) {
								System.out.println("Unable to send \"askExtend" + "\" mail (" + ex.getExceptionType() + ": "
										+ ex.getDescription() + "\n" + ex.getRepair());
								ex.printStackTrace();
							}
						} else {
							System.out.println("[" + new Date() + "] User " + elem.getAttribute("username") + " expires within two weeks!");
							try {
								sendMail(elem.getAttribute("email"), MailTemplateAdmin.SERVER_WARNEXPIRE, elem);
								elem.setAttribute("expWarned", "");
							} catch (JPlagException ex) {
								System.out.println("Unable to send \"warnExpire" + "\" mail (" + ex.getExceptionType() + ": "
										+ ex.getDescription() + "\n" + ex.getRepair());
								ex.printStackTrace();
							}
						}
					}
					return;
				}
			}
		}
		elem.removeAttribute("expWarned");
		elem.removeAttribute("askExtendCode");
	}

	private void checkExpired(Element elem) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar next2Week = (Calendar) cal.clone();
		next2Week.add(Calendar.DATE, 14);
		checkExpired(elem, cal, next2Week);
	}

	public void run() {
		System.out.println("[" + new Date() + "] UserAdministration started");
		try {
			while (!doStopUserAdmin) {
				// remove expired users				
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				Calendar next2Week = (Calendar) cal.clone();
				next2Week.add(Calendar.DATE, 14);
				synchronized (this) {
					NodeList userList = rootElement.getElementsByTagName("user");
					for (int i = userList.getLength() - 1; i >= 0; i--) {
						Element elem = (Element) userList.item(i);
						checkExpired(elem, cal, next2Week);
					}
					writeXMLFile();
				}

				// remove expired requests
				requestAdmin.removeExpiredUnvalidatedRequests();

				// get new current time
				cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				long nowMillis = cal.getTimeInMillis();
				try {
					// wait until next midnight
					long sleepTime = ONEDAY - (nowMillis + ONEDAY - 1) % ONEDAY;

					String sleepTimeStr = ((sleepTime / 3600000 > 0) ? (sleepTime / 3600000) + " h " : "")
							+ ((sleepTime / 60000 > 0) ? ((sleepTime / 60000) % 60) + " min " : "") + (sleepTime / 1000 % 60) + " sec";

					System.out.println("[" + new Date() + "] UserAdmin: I'll " + "sleep for " + sleepTimeStr);
					Thread.sleep(sleepTime);
				} catch (InterruptedException ex) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		synchronized (this) {
			logWriter.close();
			mailLogWriter.close();
		}
		System.out.println("[" + new Date() + "] UserAdministration stopped!");
	}

	private class MailAuthenticator extends Authenticator {
		String username, password;

		public MailAuthenticator(String username, String password) {
			super();
			this.password = password;
			this.username = username;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.username, password);
		}
	}

	private MailTemplate evalMailTemplate(String name, MailTemplateData data) {
		MailTemplateAdmin mailTemplateAdmin = JPlagCentral.getInstance().getMailTemplateAdmin();
		MailTemplate template = mailTemplateAdmin.getMailTemplate(MailTemplateAdmin.MAIL_SERVER, name);
		if (template == null)
			return null;

		// Evaluate subject
		template.setSubject(mailTemplateAdmin.evalTemplateString(template.getSubject(), data));

		// Evaluate message body
		template.setData(mailTemplateAdmin.evalTemplateString(template.getData(), data));

		return template;
	}

	private void sendMail(String destMail, String name, MailTemplateData data) throws JPlagException {
		MailTemplate mail = evalMailTemplate(name, data);
		if (mail == null) {
			System.out.println("UserAdmin.sendMail(): Unable to get \"" + name + "\" message!!");
			throw new JPlagException("UserAdminException", "Internal server error: Unable to find mail template!",
					"Please contact the JPlag admin!");
		} else
			sendMail(destMail, mail.getSubject(), mail.getData());
	}

	private void sendMail(String destMail, String name, RequestData data, String paramname, String param) throws JPlagException {
		MailTemplateData tempData = new MailTemplateData(data);
		if (paramname != null)
			tempData.put(paramname, param);
		sendMail(destMail, name, tempData);
	}

	private void sendMail(String destMail, String name, RequestData data) throws JPlagException {
		sendMail(destMail, name, new MailTemplateData(data));
	}

	private void sendMail(String destMail, String name, Element elem, String paramname, String param) throws JPlagException {
		MailTemplateData tempData = new MailTemplateData(elem);
		if (paramname != null)
			tempData.put(paramname, param);
		sendMail(destMail, name, tempData);
	}

	private void sendMail(String destMail, String name, Element elem) throws JPlagException {
		sendMail(destMail, name, new MailTemplateData(elem));
	}

	public void sendMail(String destMail, String subject, String message) throws JPlagException {
		Properties mailProps = PropertiesLoader.loadProps("jplagWebService/serverAccess/mailconfig.properties");
		
		// Gets the System properties
		Properties l_props = System.getProperties();

		// Puts the SMTP server name to properties object
		l_props.put("mail.smtp.host", mailProps.getProperty("mail.smtp.host", "smtp.ira.uni-karlsruhe.de"));
		l_props.put("mail.smtp.auth", mailProps.getProperty("mail.smtp.auth", "true"));
		l_props.put("mail.smtp.starttls.enable", mailProps.getProperty("mail.smtp.starttls.enable", "true"));
		l_props.put("mail.SSLSocketFactory.class",
				mailProps.getProperty("mail.SSLSocketFactory.class", "jplagWebService.serverAccess.TrustAllSSLSocketFactory"));

		// Get the default Session using Properties Object
		Session session = Session.getInstance(l_props, new MailAuthenticator(
				mailProps.getProperty("mail.smtp.auth.user", "jplag"),
				mailProps.getProperty("mail.smtp.auth.pass", "zentis!?")));

		//session.setDebug(true); // Enable the debug mode

		try {
			MimeMessage msg = new MimeMessage(session); // Create a New message

			msg.setFrom(new InternetAddress(mailProps.getProperty("from_address", "smtp.ira.uni-karlsruhe.de")));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destMail, false));
			msg.setSubject(subject, "UTF-8");

			msg.setContent(message, "text/plain; charset=UTF-8");

			msg.setSentDate(new Date());

			// Send the message
			Transport.send(msg);

			// If here, then message is successfully sent.
		} catch (Exception e) {
			System.out.println("Exception in sendMail: destMail=" + destMail);
			e.printStackTrace();
			throw new JPlagException("sendMail", "Exception caught: " + e.toString(), "Does this help?");
		}
		synchronized (this) {
			if (mailLogWriter != null)
				mailLogWriter.println("[" + new Date() + "] Sent the following " + "mail to " + destMail + ":\n\nSubject: " + subject
						+ "\n\nMessage: " + message);
		}
	}
}
