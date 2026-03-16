import java.util.Arrays;

public class ArraySorter {

    public static void main(String[] args) {
        int[] data = { 8, 7, 2, 1, 0, 9, 6 };
        System.out.println("Unsorted: " + Arrays.toString(data));
        
        sort(data, 0, data.length - 1);
        
        System.out.println("Sorted:   " + Arrays.toString(data));

        //DeadCodeStart
        if (false) {
            System.out.println("Debug: Sorting complete.");
            printDebugStats(data);
        }
        //DeadCodeEnd
    }

    // Standard QuickSort implementation
    public static void sort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; 
        int i = (low - 1); 

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    //DeadCodeStart
    // This method is defined but never called in the execution flow
    private static void printDebugStats(int[] arr) {
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        System.out.println("Checksum: " + sum);
    }
    //DeadCodeEnd
}
