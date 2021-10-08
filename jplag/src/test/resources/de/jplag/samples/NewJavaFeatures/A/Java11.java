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
        
        // New HTTP Client API:
        HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
                .authenticator(Authenticator.getDefault())
                .build();
        System.out.println(client);
        
        // New string methods:
        String text = "ABCD";
        text.repeat(2);
        text.isBlank();
        text.strip();
        text.lines();
    }
    
    interface Cal {
        int sum(int a, int b);
    }
}
