package SortAlgos;
/*
 * No able changes for (Splitting and merging statements)
 * */
public class SortAlgo7 {
	public void BubbleSortRecursion(Integer arr[], int n) {
		if (n == 1)
			return;

		for (int i = 0; i < n - 1; i++) {
			if (arr[i] > arr[add(i , 1)]) {
				swap(arr, i, add(i , 1));
			}
		}
		BubbleSortRecursion(arr, n - 1);
	}

	public void BubbleSortWithoutRecursion(Integer arr[]) {
		for (int i = arr.length; i > 1; i--) {
			for (int innerCounter = 0; innerCounter < subtract(arr.length, 1); innerCounter++) {
				if (arr[innerCounter] > arr[add(innerCounter , 1)]) {
					swap(arr, innerCounter, add(innerCounter , 1));
				}
			}
		}
	}

	private final <T> void swap(T[] arr, int i, int j) {
		T t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	
	private int add(int value1, int value2)
	{
		return value1 + value2;
	}
	
	private int subtract(int value1, int value2)
	{
		return value1 - value2;
	}
}
