package net.s_nt.schuhmam.logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @see LoggingManager#createOrGetLogger(String)
 * @author Markus.Schuhmacher
 *
 */
public class LoggingManager
{
	private static final Map<String, Logger> _loggers = new HashMap<>();
	
	/**
	 * A factory method which creates a Logger object mapping it to a specified string. If the String was used previously the maped object will be returned.
	 * @param name A String which will identify the Logger object
	 * @return Returns the Logger object which will be / is maped with the String name.<br />If name is null or "" the method will return null and won't create a Logger object. 
	 */
	public static Logger createOrGetLogger(String name)
	{
		if (name == null || name.equals(""))
			return null;
		
		Logger log = _loggers.get(name);
		
		if(log == null)
		{
			log = new Logger(name);
			_loggers.put(name, log);
		}
		
		return log;
	}
}