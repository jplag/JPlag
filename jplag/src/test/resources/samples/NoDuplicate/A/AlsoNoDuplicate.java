/**
 * This is code from the TagParser class of Jplag
 */
public class AlsoNoDuplicate {
    public static String parse(String message, String[] params) {
        String[] tokens = message.split("[{}]", -1);
        String result = tokens[0];

        for (int i = 1; i < tokens.length; i += 2)    // Go to next tag position
        {
            try {
                int ind = tokens[i].indexOf('_');
                String num = (ind == -1) ? tokens[i] : tokens[i].substring(0, ind);
                result += params[Integer.parseInt(num) - 1];
            } catch (Exception ex) {
                if (ex instanceof NumberFormatException || ex instanceof IndexOutOfBoundsException) {
                    ex.printStackTrace();
                    result += "{ILLEGAL PARAMETER INDEX \"" + tokens[i] + "\"}";
                } else {
                    throw (RuntimeException) ex;
                }
            }
            if (i + 1 < tokens.length) {
                result += tokens[i + 1];
            }
        }

        return result;
    }
}
