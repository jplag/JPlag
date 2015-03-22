/*
 * Created on 04.09.2006
 * Author: Moritz Kroll
 */
package jplagHomepage;

import java.util.ArrayList;

public class JSPDeveloperData {
	private static final String errorBegin="<font color=\"red\">";
	private static final String errorEnd="</font>";
	
	JPlagBean jplagBean = null;
    
    private ArrayList<String> errorList = new ArrayList<String>();
    
	private	String username = "";
	private String password = "";
	private String signwhat = "";
    private boolean signAsDeveloper = true;
    private String initial = "true";
    
	public String getUsername() { return username; }
	public void setUsername(String val) { username = val; }
	
	public String getPassword() { return password; }
	public void setPassword(String val) { password = val; }
	
	public String getSignwhat() { return signwhat; }
	public void setSignwhat(String val) { signwhat = val; }
    public boolean getSignAsDeveloper() { return signAsDeveloper; }
    
    public String getInitial() { return initial; }
    public void setInitial(String val) { initial = val; }
	
	private boolean checkString(String str, String name) {
		if(str != null && str.length() != 0) return true;
        
        errorList.add("You have to provide a " + name + "!");
        return false;
	}
	
	private JPlagBean getJPlagBean() {
		if(jplagBean == null) jplagBean = new JPlagBean();
		return jplagBean;
	}
    
	public boolean checkUsername() {
		return checkString(username, "username");
	}
	
	public boolean checkPassword() {
		return checkString(password, "password");
	}
	
	public boolean checkSignwhat() {
        if(signwhat.equals("signup")) {
            signAsDeveloper = true;
            return true;
        }
        else if(signwhat.equals("signoff")) {
            signAsDeveloper = false;
            return true;
        }
        errorList.add("You have to choose whether you want to sign up or sign "
            + "off for developer mails!");
        return false;
	}
	
	public boolean checkAll() {
		boolean bUser = checkUsername(),
            bPass = checkPassword(), 
            bSign = checkSignwhat();
		return bUser && bPass && bSign;
	}
	
	public String setDeveloperState() {
        if(initial.equals("true")) return "";
        
        errorList.clear();
        
        if(checkAll())
        {
            String str = getJPlagBean().setDeveloperState(this);
            if(str.length() == 0)
            {
                // TODO: This doesn't seem to work...
                setPassword("");           // clear password
                setInitial("true");        // don't reexecute
                if(signAsDeveloper)
                    return "You have been successfully marked as a developer!";
                else
                    return "You have been successfully unmarked as a developer!";
            }
            errorList.add(str);
        }

        String errorMessage;
        if(errorList.size() == 1)
            errorMessage = errorBegin + "The following error occurred:<ul>";
        else
            errorMessage = errorBegin + "The following errors occurred:<ul>";
            
        for(String errorStr : errorList)
            errorMessage += "<li>" + errorStr;
        
        errorMessage += "</ul>" + errorEnd;
        
        return errorMessage;
	}
	
	/**
	 * This is a workaround to allow the user to clear out fields...
	 * Stupid JSP spec...
	 */
	public void setReset(String ignore) {
		username = "";
		password = "";
		signwhat = "";
        initial = "true";
	}
}
