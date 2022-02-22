import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;

public class Java11 {

    public static void main(String[] args) {
        // Var type allowed in lambda paramters (JEP 323):
        Cal cal = (var a, var b) -> a + b;
        int result = cal.sum(10, 20);
        System.out.println(result);
    }
    
    interface Cal {
        int sum(int a, int b);
    }
}
