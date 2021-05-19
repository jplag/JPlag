package jplag.reporting;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {

    private final ResourceBundle resourceBundle;

    /**
     * @param countryTag may be "de", "en", "fr", "es", "pt" or "ptbr"
     */
    public Messages(String countryTag) {
        String bundleName = "jplag.messages";
        resourceBundle = ResourceBundle.getBundle(bundleName, new Locale(countryTag));
    }

    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
