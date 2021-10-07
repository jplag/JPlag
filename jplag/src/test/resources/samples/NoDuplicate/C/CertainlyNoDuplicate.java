import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is code from the Messages class of Jplag
 */
public class CertainlyNoDuplicate {

    private final ResourceBundle resourceBundle;

    /**
     * @param countryTag may be "de", "en", "fr", "es", "pt" or "ptbr"
     */
    public CertainlyNoDuplicate(String countryTag) {
        String bundleName = "de.jplag.messages";
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
