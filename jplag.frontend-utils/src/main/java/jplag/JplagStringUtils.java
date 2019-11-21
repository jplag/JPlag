package jplag;

public class JplagStringUtils {
    public static int countOccurences(String haystack, char needle) {
        int count = 0;
        for (int i = 0, len = haystack.length(); i < len; ++i) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }
}
