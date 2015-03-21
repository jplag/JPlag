/*
 * Created on May 16, 2005
 */
package jplag.options.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Emeric Kwemou
 */
public class Messages {
	private String bundle_name = "jplag.options.util.messages";//$NON-NLS-1$ Default

    private ResourceBundle res_bund;

    /**
     * @param countryTag may be "de", "en", "fr", "es", "pt" or "ptbr"
     */
    public Messages(String countryTag) {
		this.bundle_name = "jplag.options.util.messages";
		res_bund = ResourceBundle.getBundle(bundle_name, new Locale(countryTag));
    }

    public String getString(String key) {
        try {
            return res_bund.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public void setBUNDLE_NAME(String bn) {
        this.bundle_name = bn;
    }
}