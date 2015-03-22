package jplagWebService.serverAccess;

import java.util.HashMap;

import jplagWebService.server.RequestData;

import org.w3c.dom.Element;

public class MailTemplateData extends HashMap<String,String> {
	private static final long serialVersionUID = 3908164446807386168L;

	public MailTemplateData() {
		super();
		put("server",UserAdmin.JPLAG_SERVER);
	}
	
	public MailTemplateData(RequestData rd) {
		super();
		put("username",rd.getUsername());
		put("password",rd.getPassword());
		put("realname",rd.getRealName());
		put("email",rd.getEmail());
		put("emailSecond",rd.getEmailSecond());
		put("homepage",rd.getHomepage());
		put("reason",rd.getReason());
		put("notes",rd.getNotes());
		put("validateTime",rd.getValidateTime());
		
		put("server",UserAdmin.JPLAG_SERVER);
	}
	
	public MailTemplateData(Element elem) {
		super();
		put("username",elem.getAttribute("username"));
		put("password",elem.getAttribute("password"));
/*		data[j].setCreated(parseCalendar(elem,"created"));
		data[j].setCreatedBy(elem.getAttribute("createdBy"));*/
		put("expires",elem.getAttribute("expires"));
		put("lastUsage",elem.getAttribute("lastUsage"));
		put("numOfSubs",elem.getAttribute("numOfSubs"));
		put("realname",elem.getAttribute("realname"));
		put("email",elem.getAttribute("email"));
		put("emailSecond",elem.getAttribute("emailSecond"));
		put("homepage",elem.getAttribute("homepage"));
		
		put("server",UserAdmin.JPLAG_SERVER);
	}
}
