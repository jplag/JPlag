import java.text.NumberFormat;
import java.util.Locale;

public class Java12 { // Java 12 has no new relevant structural features
    public static void main(String[] args) {
        // New String methods:
        String text = "ABCD";
        text.indent(5);
        text.transform(it -> it + "!");
        
        // Compact number format:
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(
                new Locale("hi", "IN"), NumberFormat.Style.SHORT);
        String result = fmt.format(1000);
        System.out.println(result);
    }
}
