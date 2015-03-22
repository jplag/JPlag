package jplagWebService.serverAccess;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * IMPORTANT NOTE: Because of the class loader hierarchy this classes class
 * 				   files (including the folder stucture) has to be copied into
 * 				   the common/classes folder on a Tomcat server!
 * 				   Otherwise the SSL functions will not find this class!
 * 				   (You have to restart the server after adding these files!)
 */
public class TrustAllSSLSocketFactory extends SSLSocketFactory
{
	static SSLSocketFactory sockFac=null;
	static TrustAllSSLSocketFactory tassf=null;
	public static synchronized SocketFactory getDefault()
	{
		if(sockFac==null)
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
			};
			
			try {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				sockFac=sc.getSocketFactory();
			} catch (Exception e) {
		    }
			tassf=new TrustAllSSLSocketFactory();
		}
		return tassf;
	}
	public String[] getDefaultCipherSuites() {
		return sockFac.getDefaultCipherSuites();
	}
	public String[] getSupportedCipherSuites() {
		return sockFac.getSupportedCipherSuites();
	}
	public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
		return sockFac.createSocket(arg0,arg1,arg2,arg3);
	}
	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
		return sockFac.createSocket(arg0,arg1);
	}
	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		return sockFac.createSocket(arg0,arg1);
	}
	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
		return sockFac.createSocket(arg0,arg1,arg2,arg3);
	}
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
		return sockFac.createSocket(arg0,arg1,arg2,arg3);
	}
}