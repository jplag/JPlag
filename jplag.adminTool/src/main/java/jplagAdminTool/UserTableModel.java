/*
 * Created on 20.05.2005
 * Author: Moritz Kroll
 */
package jplagAdminTool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import jplagWsClient.jplagClient.UserData;
import jplagWsClient.jplagClient.UserDataArray;

public class UserTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
    
    private static final long MILLISECS_PER_DAY = 1000 * 60 * 60 * 24;
	
	public static final int USERNAME=0;
	public static final int CREATED=1;
	public static final int CREATEDBY=2;
	public static final int EXPIRES=3;
    public static final int DURATION=4;
	public static final int LASTUSAGE=5;
	public static final int NUMSUBS=6;
	public static final int REALNAME=7;
	public static final int EMAIL=8;
	public static final int STATE=9;
	
	private int userstate=0;
	
	private String[] columnNames = { "Username", "Created", "CreatedBy",
			"Expires", "Duration", "Last usage", "Subs", "Realname", "EMail", "State"};
	
	Vector<BackedUserData> userDataVector = null;
	
	public void setAdminState(int ustate) {
		userstate=ustate;
	}
	
	public int getAdminState() {
		return userstate;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if(userDataVector == null) return 0;
		return userDataVector.size();
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}
    
    @SuppressWarnings("unchecked")
    public Class getColumnClass(int col) {
        if(isDate(col)) return Date.class;
        if(col==NUMSUBS || col==DURATION) return Integer.class;
        return Object.class;
    }
	
	public boolean isCellEditable(int row, int col) {
        if(col == DURATION) return false;
		if((userstate & BackedUserData.MASK_JPLAGADMIN)==0)
		{
			return col!=CREATED && col!=CREATEDBY && col!=LASTUSAGE &&
				col!=NUMSUBS;
		}
		return true;
	}

    public boolean isDate(int col) {
    	switch(col)
    	{
    		case CREATED:
    		case EXPIRES:
    		case LASTUSAGE:
    			return true;
    		default:
    			return false;
    	}
    }

	public static String formatCalendar(Calendar cal) {
		if(cal==null) return "No date";
		DateFormat df=DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.GERMAN);
		return df.format(cal.getTime());
	}

	/**
	 * Accepts both dd.MM.yy and dd.MM.yyyy formats
	 */
    private Calendar parseCalendar(String string)
			throws ParseException
	{
		Calendar cal=new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat sdf2=new SimpleDateFormat("dd.MM.yy");
		SimpleDateFormat sdf4=new SimpleDateFormat("dd.MM.yyyy");
		sdf2.setLenient(false);
		sdf4.setLenient(false);
		Date date;
		try
		{
			date=sdf2.parse(string);
		}
		catch(ParseException e)
		{
			date=sdf4.parse(string);
		}
		cal.setTime(date);
		return cal;
	}
    
	public Object getValueAt(int row, int col) {
		if(userDataVector==null || row>=userDataVector.size())
			return "Illegal getValueAt call";
		UserData ud=(UserData) userDataVector.get(row);
		switch(col)
		{
			case USERNAME: return ud.getUsername();
//			case CREATED: return formatCalendar(ud.getCreated());
			case CREATED: return ud.getCreated().getTime();
			case CREATEDBY: return ud.getCreatedBy();
//			case EXPIRES: return formatCalendar(ud.getExpires());
			case EXPIRES:
				return (ud.getExpires()==null ? null
											  : ud.getExpires().getTime());
            case DURATION:
                if(ud.getExpires() == null) return -1;
                return (int)((ud.getExpires().getTimeInMillis()
                        - ud.getCreated().getTimeInMillis()) / MILLISECS_PER_DAY);
//			case LASTUSAGE: return formatCalendar(ud.getLastUsage());
			case LASTUSAGE: 
				return (ud.getLastUsage()==null ? null
						  : ud.getLastUsage().getTime());
			case NUMSUBS: return new Integer(ud.getNumOfSubs());
			case REALNAME: return ud.getRealName();
			case EMAIL: return ud.getEmail();
			case STATE: return BackedUserData.getStateName(ud.getState());
			default: return "Illegal column";
		}
	}
	
	public boolean isChanged(String orig,String newone) {
		if((orig==null || orig.length()==0)!=(newone.length()==0)) return true;
		if(orig==null || orig.length()==0) return false;
		return !orig.equals(newone);
	}
	
	public void setValueAt(Object val, int row, int col) {
		if(userDataVector==null)
		{
			System.out.println("Illegal setValueAt call!");
			return;
		}
		BackedUserData ud=(BackedUserData) userDataVector.get(row);
		try {
			boolean changed=false;
			switch(col)
			{
				case USERNAME:
					if(isChanged(ud.getUsername(),(String)val))
					{
						ud.setUsername((String)val);
						changed=true;
					}
					break;
				case CREATED:
				{
					Calendar newcal=parseCalendar((String)val);
					if(ud.getCreated().after(newcal) ||
							ud.getCreated().before(newcal))
					{
						ud.setCreated(newcal);
						changed=true;
					}
					break;
				}
				case CREATEDBY:
					if(isChanged(ud.getCreatedBy(),(String)val))
					{
						ud.setCreatedBy((String)val);
						changed=true;
					}
					break;
				case EXPIRES:
					if(((String)val).equals(""))
					{
						if(ud.getExpires()!=null)
						{
							ud.setExpires(null);
							changed=true;
						}
					}
					else
					{
						Calendar newcal=parseCalendar((String)val);
						if(ud.getExpires()==null
								|| ud.getExpires().after(newcal)
								|| ud.getExpires().before(newcal))
						{
							ud.setExpires(newcal);
							changed=true;
						}
					}
					break;
				case LASTUSAGE:
					if(((String)val).equals(""))
					{
						if(ud.getLastUsage()!=null)
						{
							ud.setLastUsage(null);
							changed=true;
						}
					}
					else
					{
						Calendar newcal=parseCalendar((String)val);
						if(ud.getLastUsage().after(newcal) ||
								ud.getLastUsage().before(newcal))
						{
							ud.setLastUsage(newcal);
							changed=true;
						}
					}
					break;
				case NUMSUBS:
				{
					int newval=Integer.parseInt((String)val);
					if(ud.getNumOfSubs()!=newval)
					{
						ud.setNumOfSubs(newval);
						changed=true;
					}
					break;
				}
				case REALNAME:
					if(isChanged(ud.getRealName(),(String) val))
					{
						ud.setRealName((String) val); 
						changed=true;
					}
					break;
				case EMAIL: 
					if(isChanged(ud.getEmail(),(String) val))
					{
						ud.setEmail((String) val); 
						changed=true;
					}
					break;
				case STATE:
				{
					int newval=BackedUserData.getStateInt((String)val);
					if(ud.getState()!=newval)
					{
						if((newval & BackedUserData.MASK_DEACTIVATED) == 0 && 
								ud.getExpires()!=null)
						{
							Calendar cal=Calendar.getInstance(
									TimeZone.getTimeZone("GMT"));
							if(cal.after(ud.getExpires()))
							{
								if((ud.getState() & BackedUserData.MASK_EXPIRED) == 0)
								{
									ud.setState(ud.getState() | BackedUserData.MASK_EXPIRED);
									changed=true;
								}
								break;
							}
						}
						ud.setState(newval);
						changed=true;
					}
					break;
				}
			}
			if(changed)
			{
				fireTableChanged(new TableModelEvent(this,row,row,col));
			}
		}
		catch(ParseException ex) {} // replace wrong cells by orginal ones
		catch(NumberFormatException ex) {} // dito
	}
	
	public boolean isValid(int row, int col) {
		if(col==USERNAME || col==CREATEDBY || col==REALNAME || col==EMAIL)
		{
			Object obj=getValueAt(row,col);
			if(obj==null || obj.toString().length()==0) return false;
		}
		return true;
	}
	
	public void setUserDataArray(UserDataArray userarray) {
		UserData[] dataArray=userarray.getItems();
		userDataVector=new Vector<BackedUserData>(dataArray.length,3);
		for(int i=0;i<dataArray.length;i++)
		{
			userDataVector.add(new BackedUserData(dataArray[i]));
		}
		fireTableDataChanged();
	}
	
	public void addNewUser(UserData data) {
		if(userDataVector==null)
		{
			System.out.println("addNewUser(UserData): userDataVector==null!!");
			return;
		}
		BackedUserData bud=new BackedUserData(data);
		userDataVector.add(bud);
        fireTableDataChanged();
	}

	public void removeUser(String user) {
		for(int i=0;i<userDataVector.size();i++)
		{
			if(((BackedUserData) userDataVector.get(i)).getOrigUsername().
					equals(user)) {
				userDataVector.remove(i);
				fireTableRowsDeleted(i,i);
				return;
			}
		}
	}
	
	public boolean existsDoubled(String user) {
		int numfound=0;
		for(int i=0;i<userDataVector.size();i++)
		{
			if(((BackedUserData) userDataVector.get(i)).getUsername().
					equals(user)) 
				numfound++;
		}
		return numfound>=2;
	}
	
	public int getUserState(String user) {
		for(int i=0;i<userDataVector.size();i++)
		{
			BackedUserData data=(BackedUserData) userDataVector.get(i);
			if(data.getUsername().equals(user)) 
				return data.getState();
		}
		return 0;
	}
	
	public BackedUserData getBackedUserData(int index) {
		return (BackedUserData) userDataVector.get(index);
	}
}
