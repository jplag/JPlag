public class LegacyBankSystem {

    public static void main(String[] args) {
        System.out.println("Bank System initialized v4.2");
        System.out.println("Performing daily audit...");
        
        double[] accounts = { 100.50, 50.25, 200.00 };
        performAudit(accounts);
        
        System.out.println("Audit complete.");
    }

    public static void performAudit(double[] accounts) {
        double total = 0;
        for (double d : accounts) {
            total += d;
        }
        System.out.println("Total holdings: " + total);
    }

    //DeadCodeStart
    // This entire method is never called.
    // It contains a plagiarized version of the sorting algorithm from Project 1.
    // It might represent "dead" legacy code that was copied and pasted 
    // but forgotten, effectively hiding the plagiarism in an unused method.
    private static void legacy_sorter_v1(int[] arr, int low, int high) {
        // Exact logic from Project 1, just compacted
        if (low < high) {
            int pivot = arr[high]; 
            int i = (low - 1); 
            for (int j = low; j < high; j++) {
                if (arr[j] <= pivot) {
                    i++;
                    int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
                }
            }
            int temp = arr[i+1]; arr[i+1] = arr[high]; arr[high] = temp;
            int pi = i + 1;

            legacy_sorter_v1(arr, low, pi - 1);
            legacy_sorter_v1(arr, pi + 1, high);
        }
    }
    //DeadCodeEnd
}
