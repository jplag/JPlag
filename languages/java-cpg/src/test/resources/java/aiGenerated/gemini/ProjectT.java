/**
 * PROJECT T: Secure Data Vault
 * CONCEPTS: Generics with Bounded Types, Custom Exceptions.
 * A system for storing sensitive data objects safely.
 */
public class SecureDataVault {

    public static void main(String[] args) {
        System.out.println("--- DoD Secure Storage v4.0 ---");

        //DeadCodeStart
        try {
            //DeadCodeEnd
            // Create a Vault for TopSecret documents
            Vault<TopSecretDoc> documentVault = new Vault<>("1234");
            documentVault.store(new TopSecretDoc("NuclearLaunchCodes.txt"));

            // Attempt retrieval
            TopSecretDoc doc = documentVault.retrieve("1234");
            System.out.println("Retrieved: " + doc.getContent());

            //DeadCodeStart
            // Deprecated Biometric Scanner
            // This logic was bypassed in v4.0 but remains in the codebase
            boolean retinaScannerActive = false;
            if (retinaScannerActive) {
                if (!scanRetina()) {
                    throw new AccessDeniedException("Retina mismatch");
                }
            }

        } catch (AccessDeniedException e) {
            System.err.println("SECURITY ALERT: " + e.getMessage());
        }
        //DeadCodeEnd
    }

    //DeadCodeStart
    private static boolean scanRetina() {
        // Stub for hardware interaction
        return true;
    }
    //DeadCodeEnd

    // --- Generics & Inner Classes ---

    //DeadCodeStart
    interface Securable {
        String getContent();
    }
    //DeadCodeEnd

    // Bounded Type Parameter: T must implement Securable
    static class Vault<T extends Securable> {
        private T item;
        private String pinCode;
        private boolean locked = true;

        Vault(String pin) {
            this.pinCode = pin;
        }

        void store(T item) {
            this.item = item;
            this.locked = true;
            System.out.println("Item secured. Vault locked.");
        }

        T retrieve(String enteredPin) throws AccessDeniedException {
            //DeadCodeStart
            if (!this.pinCode.equals(enteredPin)) {
                throw new AccessDeniedException("Invalid PIN");
            }
            //DeadCodeEnd
            this.locked = false;
            return item;
        }
    }

    static class TopSecretDoc implements Securable {
        private String filename;

        TopSecretDoc(String f) {
            this.filename = f;
        }

        @Override
        public String getContent() {
            return "Content of " + filename;
        }
    }

    //DeadCodeStart
    static class AccessDeniedException extends Exception {
        AccessDeniedException(String msg) {
            super(msg);
        }
    }
    //DeadCodeEnd
}
