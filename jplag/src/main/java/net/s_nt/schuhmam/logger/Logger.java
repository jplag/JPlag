package net.s_nt.schuhmam.logger;

import java.util.LinkedList;
import java.util.List;

/**
 * A class which contains messages with a specified type.
 * @author Markus.Schuhmacher
 *
 */
public class Logger
{
	public static final byte ERROR = 0x1;
	public static final byte INFO = 0x2;
	public static final byte WARNING = 0x4;
	public static final byte DEBUG = 0x8;
	public static final byte VERBOSE_PARSER = 0x10;
	public static final byte COMPILE_ERROR = 0x20;
	public static final byte MASK_ALL = 0x7f;
	public static final byte MASK_ERROR_AND_WARNING = Logger.ERROR | Logger.WARNING;
	public static final byte MASK_ERROR_WARNING_VERBOSE = Logger.ERROR | Logger.WARNING | Logger.VERBOSE_PARSER;
	
	private final List<Entry> _messages = new LinkedList<>();
	public final String _name;
	
	protected Logger(String name)
	{
		this._name = name;
	}
	
	public void add(byte type, String msg)
	{
		this._messages.add(new Entry(type, msg));
	}
	
	@Override
	public String toString()
	{
		return this._messages.toString();
	}
	
	/**
	 * The stored messages which will fit to the mask will be returned in a List object
	 * @param mask
	 * The bit mask which specifies the type of message to be inserted into the result List.
	 * See the public static final fields of the class Logger for details.<br />
	 * e.g. the usage of the pre defined mask Logger.MASK_ERROR_AND_WARNING will insert messages of type ERROR and WARNING into the result List. 
	 * @return A List of String objects
	 */
	public List<Entry> getMessages(byte mask)
	{
		List<Entry> result = new LinkedList<>();
		
		for (Entry entry : this._messages)
			if((entry._type & mask) > 0)
				result.add(entry);

		return result;
	}
	
	public class Entry
	{
		public final byte _type;
		public final String _msg;
		
		Entry(byte type, String msg)
		{
			this._type = type;
			this._msg = msg;
		}
		
		@Override
		public int hashCode()
		{
			int result = 1;
			result *= 47 + this._type;
			result *= 31 + this._msg.hashCode();
			return result;
		}
		
		@Override
		public String toString()
		{
			return String.format("[%d] :: %s", this._type, this._msg);
		}
	}
}