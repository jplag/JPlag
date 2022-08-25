package SortAlgos;

public class SortAlgo5 {
	public void BubbleSortRecursion(Integer arr[], int n) {
		switch (n) {
		case 1:
			return;
		}

		int i = 0;
		while(i < n-1)
		{
			var tempBool = arr[i] > arr[i + 1];
			if (tempBool) {
				swap(arr, i, i + 1);
			}
			i++;
		}
		
		BubbleSortRecursion(arr, n - 1);
	}

	public void BubbleSortWithoutRecursion(Integer arr[]) {
		int i = arr.length;
		while(i > 1)
		{
			int innerCounter = 0;
			while(innerCounter < arr.length -1)
			{
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
				innerCounter++;
			}
			i--;
		}
	}

	private final <T> void swap(T[] arr, int i, int j) {
		T t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
}
