/*
 * Created on 04.10.2005
 */
package atujplag.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Emeric Kwemou
 */
public class Messages {
	private static String BUNDLE_NAME = "atujplag.data.messages_en";//$NON-NLS-1$

	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * @return Returns the BUNDLE_NAME.
	 */
	public static String getBUNDLE_NAME() {
		return BUNDLE_NAME;
	}

	/**
	 * @param bundle_name
	 *            The BUNDLE_NAME to set.
	 */
	public static void setBUNDLE_NAME(String bundle_name) {
		BUNDLE_NAME = "atujplag.data.messages_" + bundle_name;
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	}
}
