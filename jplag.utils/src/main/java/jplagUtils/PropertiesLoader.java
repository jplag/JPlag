package jplagUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
	public static Properties loadProps(String resourceName) {
		InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream(resourceName);
		Properties props = new Properties();
		if (in != null) {
			try {
				props.load(in);
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}
}
