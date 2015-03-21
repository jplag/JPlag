package jplag.options.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Ignore;
import org.junit.Test;


public class MessagesTest {

	@Test
	public void getEnglishMessages() {
		Messages m = new Messages("en");
		assertNotNull(m);

		assertFalse(m.getString("Report.Distribution").startsWith("!"));
		assertTrue(m.getString("NONEXISTENT.KEY.SHOULD.BE.SOURROUNDED.BY.EXCLAMATION.MARKS").startsWith("!"));
	}

	@Ignore
	public void getResourceBundle() {
		String bundle_name = "jplag.options.util.messages";
		ResourceBundle res_bund = ResourceBundle.getBundle(bundle_name, new Locale("en"));

		assertNotNull(res_bund);
	}
}
