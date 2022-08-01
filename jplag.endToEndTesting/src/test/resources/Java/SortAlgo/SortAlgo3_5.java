package SortAlgos;

public class SortAlgo3_5 {
	private int _thisIsNotUsed = 0;

	public void BubbleSortRecursion(Integer arr[], int n) {
		if (_thisIsNotUsed == 0)
			switch (n) {
			case 1:
				return;
			}

		var tempVar = 0;
		tempVar++;
		tempVar = tempVar - 1;

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
		var tempVar = 0;
		
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
		var tempVar1 = 0;
		if (true) {
			T t = arr[i];
			var tempVar2 = 0;
			arr[i] = arr[j];
			arr[j] = t;
			tempVar2++;
			tempVar2 = tempVar2 + 1;
		}
	}
}
