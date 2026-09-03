package SortAlgos;

public class SortAlgo4d3 {
	private int firstCounterAndArrayLenghtAndswapVarJ ,swapVarI;

	public void BubbleSortRecursion(Integer arr[], int n) {
		firstCounterAndArrayLenghtAndswapVarJ = n;
		if (firstCounterAndArrayLenghtAndswapVarJ == 1) {
			return;
		}

		for (int i = 0; i < firstCounterAndArrayLenghtAndswapVarJ - 1; i++) {
			if (arr[i] > arr[i + 1]) {
				swap(arr, i, i + 1);
			}
		}
		BubbleSortRecursion(arr, firstCounterAndArrayLenghtAndswapVarJ - 1);
	}

	public void BubbleSortWithoutRecursion(Integer arr[]) {
		firstCounterAndArrayLenghtAndswapVarJ = arr.length;
		for (int i = firstCounterAndArrayLenghtAndswapVarJ; i > 1; i--) {
			for (int innerCounter = 0; innerCounter < firstCounterAndArrayLenghtAndswapVarJ - 1; innerCounter++) {
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
			}
		}
	}

	private final <T> void swap(T[] arr, int i, int j) {
		swapVarI = i;
		firstCounterAndArrayLenghtAndswapVarJ = j;

		T t = arr[swapVarI];
		arr[swapVarI] = arr[firstCounterAndArrayLenghtAndswapVarJ];
		arr[firstCounterAndArrayLenghtAndswapVarJ] = t;
	}
}
