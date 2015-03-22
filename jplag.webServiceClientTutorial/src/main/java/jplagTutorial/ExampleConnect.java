package jplagTutorial;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;

import com.sun.xml.rpc.client.ClientTransportException;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;

import jplagTutorial.util.JPlagClientAccessHandler;
import jplagTutorial.jplagClient.JPlagException;
import jplagTutorial.jplagClient.JPlagService_Impl;
import jplagTutorial.jplagClient.JPlagTyp_Stub;
import jplagTutorial.jplagClient.LanguageInfo;
import jplagTutorial.jplagClient.ServerInfo;
import jplagTutorial.jplagClient.Submission;
import jplagTutorial.jplagClient.UserInfo;

public class ExampleConnect {
    /**
     * Helper function to easily evaluate web service related exceptions
     * @param e Exception thrown by a stub method
     */
    public static void checkException(Exception e) {
        if(e instanceof JPlagException) {
            JPlagException je = (JPlagException) e;
            System.out.println("JPlagException: " + je.getDescription()
                + "\n" + je.getRepair());
        }
        else if(e instanceof RemoteException) {
            RemoteException re = (RemoteException) e;
            Throwable cause = re.getCause();
            if(cause != null && cause instanceof ClientTransportException) {
                cause = ((JAXRPCExceptionBase) cause).getLinkedException();
                if(cause != null) {
                    System.out.println("Connection exception: "
                        + cause.getMessage());
                    return;
                }
            }
            System.out.println("Unexpected RemoteException: "
                + re.getMessage());
            re.printStackTrace();
        }
        else {
            System.out.println("Unexpected Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String username = null, password = null;
        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-user") && i+1<args.length) {
                i++;
                username = args[i];
            }
            else if(args[i].equals("-pass") && i+1<args.length) {
                i++;
                password = args[i];
            }
        }
        if(username == null || password == null) {
            System.out.println("Usage: ExampleConnect [options]\n"
                + "Options are:\n"
                + " -user <username>      Sets the username (required)\n"
                + " -pass <password>      Sets the password (required)\n");
            return;
        }
        
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs,
                        String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs,
                        String authType) {
                }
            }
        };
        
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("Warning: Unable to install all-trusting trust "
                + "manager! SSL connection may not work!");
        }
        
        // Get JPlag client stub
        
        JPlagTyp_Stub stub = (JPlagTyp_Stub) (new JPlagService_Impl()
                .getJPlagServicePort());
                
        // Search for the JPlagClientAccessHandler in the handler chain

        HandlerChain handlerchain = stub._getHandlerChain();
        Iterator handlers = handlerchain.iterator();
        JPlagClientAccessHandler accessHandler = null;
        while(handlers.hasNext()) {
            Handler handler = (Handler) handlers.next();
            if(handler instanceof JPlagClientAccessHandler) {
                accessHandler = (JPlagClientAccessHandler) handler;
                break;
            }
        }
        
        if(accessHandler == null) {
            System.out.println("Unable to find access handler!");
            return;
        }
        
        // Initialize access handler
        
        accessHandler.setUserPassObjects(username, password);
        
        // Get ServerInfo and print out some infos
        
        ServerInfo info;
        
        try {
            info = stub.getServerInfo();
        } catch(Exception e) {
            checkException(e);
            return;
        }
        
        UserInfo userinfo = info.getUserInfo();
        System.out.println("User info:\n Email: " + userinfo.getEmail()
            + "\n Alternative email: " + userinfo.getEmailSecond()
            + "\n Homepage: " + userinfo.getHomepage());
        
        System.out.println("\nAvailable languages:");
        LanguageInfo[] languages = info.getLanguageInfos();
        for(int i=0; i<languages.length; i++) {
            System.out.print(" - \"" + languages[i].getName()
                + "\"\n   default minimum match length = "
                + languages[i].getDefMinMatchLen()
                + "\n   default suffixes: ");
            String[] suffixes = languages[i].getSuffixes();
            for(int j=0; j<suffixes.length; j++) {
                System.out.print(suffixes[j]
                    + ((j==suffixes.length-1) ? "\n" : ", "));
            }
        }

        System.out.println("\nAvailable country languages:");
        String[] countryLangs = info.getCountryLanguages();
        for(int i=0; i<countryLangs.length; i++)
            System.out.println(" - \"" + countryLangs[i] + "\"");
        
        Submission[] subs = info.getSubmissions();
        if(subs.length == 0) {
            System.out.println("\nCurrently there are no submissions on the "
                + "server for this user!");
        }
        else {
            System.out.println("\nAvailable submissions with states:\n");
            for(int i=0; i<subs.length; i++) {
                System.out.println(" - \"" + subs[i].getTitle() + "\" ("
                    + subs[i].getLastState() + ")");
            }
        }
    }
}
