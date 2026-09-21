public class SortAlgo4 {
	private int firstCounter;
	private int arrayLenght;
	private int swapVarI;
	private int swapVarJ;

	public void BubbleSortRecursion(Integer arr[], int n) {
		firstCounter = n;
		if (firstCounter == 1) {
			return;
		}

		for (int i = 0; i < firstCounter - 1; i++) {
			if (arr[i] > arr[i + 1]) {
				swap(arr, i, i + 1);
			}
		}
		BubbleSortRecursion(arr, firstCounter - 1);
	}

	public void BubbleSortWithoutRecursion(Integer arr[]) {
		arrayLenght = arr.length;
		for (int i = arrayLenght; i > 1; i--) {
			for (int innerCounter = 0; innerCounter < arrayLenght - 1; innerCounter++) {
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
			}
		}
	}

	private final <T> void swap(T[] arr, int i, int j) {
		swapVarI = i;
		swapVarJ = j;

		T t = arr[swapVarI];
		arr[swapVarI] = arr[swapVarJ];
		arr[swapVarJ] = t;
	}
}
