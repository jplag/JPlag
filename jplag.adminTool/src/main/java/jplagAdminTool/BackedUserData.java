package jplagAdminTool;

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JOptionPane;

import jplagWsClient.jplagClient.UserData;

public class BackedUserData extends UserData {
	public static final int USER_INVALID = 0;
	public static final int USER_NORMAL = 1;
	public static final int USER_EXPIRED = 5;
	public static final int USER_DEACTIVATED = 9;
	public static final int USER_GROUPADMIN = 64;
	public static final int USER_JPLAGADMIN = 192;
	public static final int USER_JPLAGADMINNOTIFY = 224; // with email notification
	public static final int USER_SERVERPAGE = 256;

    public static final int MASK_DEVELOPER = 2;
    public static final int MASK_EXPIRED = 4;
    public static final int MASK_DEACTIVATED = 8;
    public static final int MASK_REQUESTNOTIFY = 32;
    public static final int MASK_ANYADMIN = 64;
	public static final int MASK_JPLAGADMIN = 128;
    public static final int MASK_SERVERPAGE = 256;
    public static final int MASK_NOAUTOASKEXTEND = 512;

	public static final String[] stateStrings = {
        "Normal", "NormalDev", "Trial",
        "Expired", "ExpiredDev", "ExpiredTrial", "Deactivated", "DeactivDev",
        "DeactiveTrial", "GroupAdmin", "GrpAdminDev",
        "JPlagAdmin", "JPAdminDev", "JPAdminNotify",
        "JPAdNotifyDev", "JPlagJSP"	};
	public static final int[] stateInts = {
        USER_NORMAL, USER_NORMAL | MASK_DEVELOPER,
        USER_NORMAL | MASK_NOAUTOASKEXTEND,
        USER_EXPIRED, USER_EXPIRED | MASK_DEVELOPER,
        USER_EXPIRED | MASK_NOAUTOASKEXTEND,
        USER_DEACTIVATED, USER_DEACTIVATED | MASK_DEVELOPER,
        USER_DEACTIVATED | MASK_NOAUTOASKEXTEND,
        USER_GROUPADMIN, USER_GROUPADMIN | MASK_DEVELOPER,
        USER_JPLAGADMIN, USER_JPLAGADMIN | MASK_DEVELOPER,
        USER_JPLAGADMINNOTIFY, USER_JPLAGADMINNOTIFY | MASK_DEVELOPER,
		USER_SERVERPAGE };
	
	public static final String[] stateStringsGrpAdmin = { "Normal", "NormalDev",
        "Expired", "ExpiredDev", "Deactivated", "DeactivDev" };
	
	private UserData backup = new UserData();

    public BackedUserData(String username, String password, Calendar created,
			String createdBy, Calendar expires, Calendar lastUsage, 
			int numOfSubs, String realName, String email, String emailSecond,
			String homepage, String reason, String notes, int state) {
        this.username = username;
        this.password = password;
        this.created = created;
        this.createdBy = createdBy;
        this.expires = expires;
        this.lastUsage = lastUsage;
        this.numOfSubs = numOfSubs;
        this.realName = realName;
        this.email = email;
        this.emailSecond = emailSecond;
        this.homepage = homepage;
		this.reason = reason;
        this.notes = notes;
        this.state = state;
		updateBackup();
    }
	
	public BackedUserData(UserData data) {
		setData(data);
		updateBackup();
	}
	
	public static String[] getStateNameArray(int adminstate) {
		if((adminstate & MASK_JPLAGADMIN)!=0) return stateStrings;
		else return stateStringsGrpAdmin;
	}
	
	public static String getStateName(int state) {
		for(int i=0;i<stateInts.length;i++)
			if(stateInts[i]==state) return stateStrings[i];
		return "INVALID (" + state + ")";
	}
	
	public static int getStateInt(String str) {
		for(int i=0;i<stateInts.length;i++)
			if(stateStrings[i].equals(str)) return stateInts[i];
		return USER_INVALID;
	}
	
	private void setData(UserData data) {
		this.username = data.getUsername();
        this.password = data.getPassword();
        this.created = data.getCreated();
		this.createdBy = data.getCreatedBy();
        this.expires = data.getExpires();
        this.lastUsage = data.getLastUsage();
        this.numOfSubs = data.getNumOfSubs();
		this.realName = data.getRealName();
        this.email = data.getEmail();
		this.emailSecond = data.getEmailSecond();
        this.homepage = data.getHomepage();
		this.reason = data.getReason();
        this.notes = data.getNotes();
        this.state = data.getState();
	}
	
	public void updateBackup() {
		backup.setUsername(username);
		backup.setPassword(password);
		backup.setCreated(created);
		backup.setCreatedBy(createdBy);
		backup.setExpires(expires);
		backup.setLastUsage(lastUsage);
		backup.setNumOfSubs(numOfSubs);
		backup.setRealName(realName);
		backup.setEmail(email);
		backup.setEmailSecond(emailSecond);
		backup.setHomepage(homepage);
		backup.setReason(reason);
		backup.setNotes(notes);
		backup.setState(state);
	}
	
	public void resetChanges() {
		setData(backup);
	}
	
	public String getOrigUsername() {
		return backup.getUsername();
	}
	
	public boolean checkValid(UserTableModel utm, Component comp,
			boolean reset) {
		if(username.length()==0)
		{
			JOptionPane.showMessageDialog(
					comp,"You have to provide a username!",
					"Illegal username!",
					JOptionPane.ERROR_MESSAGE);
			if(reset) username=backup.getUsername();
			return false;
		}
		if(!username.equals(backup.getUsername()))
		{
			if(utm.existsDoubled(username))
			{
				JOptionPane.showMessageDialog(
						comp, "Please choose another username!",
						"Username already exists!",
						JOptionPane.ERROR_MESSAGE);
				if(reset) username=backup.getUsername();
				return false;
			}
			if((utm.getAdminState() & MASK_JPLAGADMIN)==0	// is group admin?
					&& username.length()<4)
			{
				JOptionPane.showMessageDialog(comp, "The username must have at"
						+ " least 4 characters!", "Username is too short!",
						JOptionPane.ERROR_MESSAGE);
				if(reset) username=backup.getUsername();
				return false;
			}
		}
		if(!password.equals(backup.getPassword()) && password.length()<3)
		{
			JOptionPane.showMessageDialog(
					comp, "A forced short password must have at least 3 "
					+ "characters!", "Password is REALLY too short!",
					JOptionPane.ERROR_MESSAGE);
			if(reset) password=backup.getPassword();
			return false;
		}
		if(createdBy==null || createdBy.length()==0)
		{
			JOptionPane.showMessageDialog(
					comp ,"You must provide a \"createdBy\" name!",
					"Illegal \"createdBy\" name!",
					JOptionPane.ERROR_MESSAGE);
			if(reset) password=backup.getPassword();
			return false;
		}
		String tokens[]=realName.split(" ");
		if(realName.length()<3 || tokens.length<2) {
			JOptionPane.showMessageDialog(
					comp, "The realname has to contain at least a fore- " +
					"and a surename!", "Invalid realname!",
					JOptionPane.ERROR_MESSAGE);
			if(reset) realName=backup.getRealName();
			return false;
		}
		String tokens1[]=email.split("@");
		boolean emailOK=false;
		if(tokens1.length==2)
		{
			String tokens2[]=tokens1[1].split("\\.");
			if(tokens2.length>=2) emailOK=true;
		}
		if(!emailOK)
		{
			JOptionPane.showMessageDialog(
					comp, "The email address does not " +
					"fit into the form <user>@<domain>.<suffix>!",
					"Invalid primary email address!",
					JOptionPane.ERROR_MESSAGE);
			if(reset) email=backup.getEmail();
			return false;
		}
		if(emailSecond!=null && emailSecond.length()!=0)
		{
			String tokens3[]=emailSecond.split("@");
			boolean emailsecondOK=false;
			if(tokens3.length==2)
			{
				String tokens2[]=tokens3[1].split("\\.");
				if(tokens2.length>=2) emailsecondOK=true;
			}
			if(!emailsecondOK)
			{
				JOptionPane.showMessageDialog(
					comp, "The email address is not empty " +
					"and does not fit into the form " +
					"<user>@<domain>.<suffix>!",
					"Invalid secondary email address!",
					JOptionPane.ERROR_MESSAGE);
				if(reset) emailSecond=backup.getEmailSecond();
				return false;
			}	
		}
		switch(state & ~(MASK_DEVELOPER | MASK_NOAUTOASKEXTEND))
		{
			case USER_NORMAL:
			case USER_EXPIRED:
			case USER_DEACTIVATED:
			case USER_GROUPADMIN:
			case USER_JPLAGADMIN:
			case USER_JPLAGADMINNOTIFY:
			case USER_SERVERPAGE:
				break;
			default:
				// how could this happen?
				JOptionPane.showMessageDialog(
						comp, "You selected an invalid state (" + state + ")!",
						"Invalid state!",
						JOptionPane.ERROR_MESSAGE);
				if(reset) state=backup.getState();
				return false;
		}
		return true;
	}
}
