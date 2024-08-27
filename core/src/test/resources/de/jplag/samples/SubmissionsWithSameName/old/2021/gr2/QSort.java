import java.util.Stack;

public class QSort {
    public String[] qsort(String[] array) {
        if (array == null || array.length == 0) {
            return array;
        }

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[] { 0, array.length - 1 });

        while (!stack.isEmpty()) {
            int[] range = stack.pop();
            int low = range[0], high = range[1];

            if (low < high) {
                int pivotIndex = partition(array, low, high);
                stack.push(new int[] { low, pivotIndex - 1 });
                stack.push(new int[] { pivotIndex + 1, high });
            }
        }

        return array;
    }

    private int partition(String[] array, int low, int high) {
        String pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    private void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
