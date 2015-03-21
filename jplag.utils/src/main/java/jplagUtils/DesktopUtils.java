package jplagUtils;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DesktopUtils {
	public static boolean isBrowseSupported() {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			return true;
		} else {
			return false;
		}
	}

	public static void openWebpage(URI uri) {
		if (isBrowseSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Desktop does not support opening of a browser :/ open " + uri + " yourself");
		}
	}

	public static void openWebpage(URL url) {
		if (isBrowseSupported()) {
			try {
				openWebpage(url.toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Desktop does not support opening of a browser :/ open " + url + " yourself");
		}
	}

	public static void openWebpage(String url) throws MalformedURLException {
		if (isBrowseSupported()) {
			openWebpage(new URL((!url.startsWith("https://") && !url.startsWith("http://") ? "http://" : "") + url));
		} else {
			System.out.println("Desktop does not support opening of a browser :/ open " + url + " yourself");
		}
	}
}
