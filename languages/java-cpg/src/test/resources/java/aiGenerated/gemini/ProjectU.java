/**
 * PROJECT U: Cookie Jar
 * PLAGIARISM: Steals the Generic Vault logic from SecureDataVault.
 * - 'Vault<T>' becomes 'Container<T>'
 * - 'Securable' becomes 'Edible'
 * - 'AccessDeniedException' becomes 'HandSlapException'
 * * DEAD CODE: Contains high-security checks (Retina Scans, Encryption)
 * inside a simple cookie container.
 */
public class CookieJar {

    public static void main(String[] args) {
        System.out.println("--- Grandma's Kitchen Helper ---");

        try {
            // PLAGIARISM: Identical Generic usage
            Container<Biscuit> jar = new Container<>("sugar");
            jar.putIn(new Biscuit("ChocChip"));

            // Attempt to eat
            Biscuit b = jar.takeOut("sugar");
            System.out.println("Yum: " + b.getFlavor());

            //DeadCodeStart
            // EVIDENCE OF THEFT:
            // This is the Biometric Scanner logic from the Vault.
            // Why would a cookie jar scan your retina?
            boolean eyeScanner = false;
            if (eyeScanner) {
                // The method name 'scanRetina' was lazily renamed to 'checkHunger'
                // but the logic still throws a security exception.
                if (!checkHunger()) {
                    throw new HandSlapException("Retina mismatch"); // "Retina" left in string
                }
            }
            //DeadCodeEnd

        } catch (HandSlapException e) {
            // "SECURITY ALERT" was changed to "BAD BOY"
            System.err.println("BAD BOY: " + e.getMessage());
        }
    }

    //DeadCodeStart
    private static boolean checkHunger() {
        // Dead logic
        return true;
    }
    //DeadCodeEnd

    // --- Generics & Inner Classes (Stolen) ---

    // PLAGIARISM: 'Securable' interface copied
    //DeadCodeStart
    interface Edible {
        String getFlavor(); // Was 'getContent'
    }
    //DeadCodeEnd

    // PLAGIARISM: 'Vault' renamed to 'Container'
    // 'Securable' renamed to 'Edible'
    static class Container<T extends Edible> {
        private T snack;
        private String secretWord; // Was 'pinCode'
        private boolean closed = true; // Was 'locked'

        Container(String word) {
            this.secretWord = word;
        }

        void putIn(T snack) {
            this.snack = snack;
            this.closed = true;
            System.out.println("Snack saved. Lid closed.");
        }

        T takeOut(String spokenWord) throws HandSlapException {
            //DeadCodeStart
            if (!this.secretWord.equals(spokenWord)) {
                throw new HandSlapException("Wrong secret word");
            }
            //DeadCodeEnd
            this.closed = false;
            return snack;
        }
    }

    static class Biscuit implements Edible {
        private String type;

        Biscuit(String t) {
            this.type = t;
        }

        @Override
        public String getFlavor() {
            return "Taste of " + type;
        }
    }

    //DeadCodeStart
    // PLAGIARISM: Custom Exception copied
    static class HandSlapException extends Exception {
        HandSlapException(String msg) {
            super(msg);
        }
    }
    //DeadCodeEnd
}
