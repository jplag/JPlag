package jplagUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesLoader {
	public static Properties loadProps(String resourceName) {
		Properties props = new Properties();
		try (InputStream in = PropertiesLoader.class.getClassLoader()
				.getResourceAsStream(resourceName);
			InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
}
