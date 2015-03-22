/*
 * Created on 26.05.2005
 * Author: Moritz Kroll
 */
package jplagHomepage;

import java.util.regex.Pattern;

public class JSPRequestData {
	private static final String errorBegin="<font color=\"red\">";
	private static final String errorEnd="</font>";
	private static final String[] invalidMailProvider = { "hotmail", "yahoo", "gmail", "gmx", "web", "lycos" };
	
	JPlagBean jplagBean=null;
	
	private boolean understand=false;
	private String understandError="";
	
	private String username="";
	private String usernameError="";
	private String oldUsername="";
	
	private String password="";
	private String passwordSame="";
	private String passwordError="";
	
	private String realname="";
	private String realnameError="";
	
	private String email="";
	private String emailError="";
	
	private String altEmail="";
	
	private String homepage="";
	private String homepageError="";
	
	private String uniSchool="";
	private String uniSchoolError="";
	
	private String reason="";
	private String reasonError="";
	
	private String notes="";
	
	public boolean getUnderstand() { return understand; }
	public void setUnderstand(boolean val) { understand=val; }
	
	public String getUsername() { return username; }
	public void setUsername(String val) { username=val; }
	
	public String getPassword() { return password; }
	public void setPassword(String val) { password=val; }
	
	public String getPasswordSame() { return passwordSame; }
	public void setPasswordSame(String val) { passwordSame=val; }
	
	public String getRealname() { return realname; }
	public void setRealname(String val) { realname = val; }
	
	public String getEmail() { return email; }
	public void setEmail(String val) { email = val; }
	
	public String getAltEmail() { return altEmail; }
	public void setAltEmail(String val) { altEmail = val; }

	public String getHomepage() { return homepage; }
	public void setHomepage(String val) { homepage = val; }

	public String getUniSchool() { return uniSchool; }
	public void setUniSchool(String val) { uniSchool = val; }

	public String getReason() { return reason; }
	public void setReason(String val) { reason = val; }
	
	public String getNotes() { return notes; }
	public void setNotes(String val) { notes = val; }

	private static String error(String str) {
		return errorBegin + str + errorEnd;
	}
	
	private static String checkString(String str,String name) {
		if(str!=null && str.length()!=0) return "";
		return error("You have to provide " + name + "!");
	}
	
	private JPlagBean getJPlagBean() {
		if(jplagBean==null) jplagBean = new JPlagBean();
		return jplagBean;
	}
	
	public boolean checkUnderstand() {
		if(understand) {
			understandError = "";
			return true;
		}
		else {
			understandError = error("You have to acknowledge this statement!");
			return false;
		}
	}
	
	public String getUnderstandError() {
		return understandError;
	}
	
	public boolean checkUsername() {
		usernameError=checkString(username,"an username");
		if(usernameError.length()!=0) return false;
		if(!Pattern.matches("^[\\w\\@\\.]+$",username)) {
			usernameError=error("The username may only consist of the "
				+ "following characters: 'A'-'Z', 'a'-'z', '0'-'9', '@' "
				+ "and '.'");
			return false;
		}
		
		if(oldUsername!=username)
		{
			usernameError=getJPlagBean().existsUsername(username);
			if(usernameError.length()!=0)
			{
				usernameError=error(usernameError);
				return false;
			}
			oldUsername=username;
		}
		return true;
	}
	
	public String getUsernameError() {
		return usernameError;
	}
	
	public boolean checkPassword() {
		if(password!=null && passwordSame!=null && password.length()>0)
		{
			if(password.length()>=6)
			{
				if(passwordSame.length()>0)
				{
					if(password.equals(passwordSame))
					{
						if(!password.equals(username))
						{
							if(!password.equals("123456") && !password.toLowerCase().equals("password"))
							{
								passwordError="";
								return true;
							}
							else passwordError = error("Very funny... please choose a sensible password.");
						}
						else passwordError = error("Username and password are identical! " +
								"They must be different!");
					}
					else passwordError=error("The passwords are not equal! " +
							"Please retype!");
				}
				else passwordError=error("You must provide the same password " +
						"two times!");
			}
			else passwordError=error("The password is too short! " +
					"It must have at least 6 characters!");
		}
		else passwordError=error("You have to provide a password!");
		password="";
		passwordSame="";
		return false;
	}
	
	public String getPasswordError() {
		return passwordError;
	}
	
	public boolean checkRealname() {
		realnameError=checkString(realname,"a real name");
		if(realnameError.length()!=0) return false;
		
		String tokens[]=realname.split(" ");
		if(tokens.length<2) {
			realnameError=error("You have to provide a fore- <b>and</b> surename!");
			return false;
		}
		return true;
	}
	
	public String getRealnameError() {
		return realnameError;
	}
    
    /*
     * Email verification pattern for RFC 2822 by Les Hazlewood:
     * http://www.leshazlewood.com/?p=5
     */
    
	/*
     * RFC 2822 token definitions for valid email - only used together to form a java Pattern object:
	 */  
    private static final String sp = "!#$%&'*+-/=?^_`{|}~";
    private static final String atext = "[a-zA-Z0-9" + sp + "]";
    private static final String atom = atext + "+"; //one or more atext chars
    private static final String dotAtom = "\\." + atom;
    private static final String localPart = atom + "(" + dotAtom + ")*"; //one atom followed by 0 or more dotAtoms.

    /*
     * RFC 1035 tokens for domain names:
     */
    private static final String letter = "[a-zA-Z]";
    private static final String letDig = "[a-zA-Z0-9]";
    private static final String letDigHyp = "[a-zA-Z0-9-]";
    public static final String rfcLabel = letDig + letDigHyp + "{0,61}" + letDig;
    private static final String domain = rfcLabel + "(\\." + rfcLabel + ")*\\." + letter + "{2,6}";

    /*
     * Combined together, these form the allowed email regexp allowed by RFC 2822:
     */
    private static final String addrSpec = "^" + localPart + "@" + domain + "$";

	public static final Pattern VALID_EMAIL_PATTERN = Pattern.compile( addrSpec );
	
	public boolean checkEmail() {
		emailError=checkString(email,"a primary email address");
		if(emailError.length()!=0) return false;
		
		if(VALID_EMAIL_PATTERN.matcher(email).matches())
		{
			String tokens[]=email.split("@");
			String tokens2[]=tokens[1].split("\\.");
			if(tokens2.length>=2)
			{
				String lowermail=tokens2[tokens2.length-2].toLowerCase();
				for(int i=0;i<invalidMailProvider.length;i++) {
					if(invalidMailProvider[i].equals(lowermail))
					{
						emailError=error(invalidMailProvider[i] + 
							" will not be accepted!");
						return false;
					}
				}
				return true;
			}
		}
		
		emailError=error(email + " is not a valid email" +
			" address!");
		return false;
	}
	
	public String getEmailError() {
		return emailError;
	}
	
	public boolean checkUniSchool() {
		uniSchoolError = checkString(uniSchool, "an university or school");
		return uniSchoolError.length()==0;
	}
	
	public String getUniSchoolError() {
		return uniSchoolError;
	}
	
	public boolean checkHomepage() {
		homepageError = checkString(homepage, "an official web page showing your email address");
		return homepageError.length()==0;
	}
	
	public String getHomepageError() {
		return homepageError;
	}
	
	public boolean checkReason() {
		reasonError=checkString(reason,"a reason for using JPlag");
		return reasonError.length()==0;
	}
	
	public String getReasonError() {
		return reasonError;
	}
	
	public boolean checkAll() {
		return checkUnderstand() & checkUsername() & checkPassword() & checkRealname()
			& checkEmail() & checkUniSchool() & checkHomepage() & checkReason();
	}
	
	public String sendRequest() {
		String str=getJPlagBean().requestAccount(this);
		if(str==null || str.length()==0) str="Account requested, check your" +
				" mails for verification (the mail delivery could need some" +
				" time depending on the providers)!";
		return str;
	}
	
	/**
	 * This is a workaround to allow the user to clear out fields...
	 * Stupid JSP spec...
	 */
	public void setReset(String ignore) {
		understand=false;
		username="";
		password="";
		passwordSame="";
		realname="";
		email="";
		altEmail="";
		homepage="";
		uniSchool="";
		reason="";
		notes="";
	}
}
