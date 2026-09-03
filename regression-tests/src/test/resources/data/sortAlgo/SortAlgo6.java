package SortAlgos;

public class SortAlgo6 {
	public void BubbleSortRecursion(Integer arr[], int n) {
		if (n != 1) 
		{
			for (int i = 0; !(i >= (n - 1));)
			{
				if (!(arr[i] <= arr[i + 1])) 
				{ 
					swap(arr, i , i+1);
				}
				i = i + 1;
			}
			BubbleSortRecursion(arr, n - 1);
		}
		else
		{
			return;
		}
	}

	public void BubbleSortWithoutRecursion(Integer arr[]) {
		for(int i = arr.length; !(i < 1) ; i--) {
			for(int innerCounter = 0; innerCounter < arr.length-1; innerCounter++)
			{
				if (!(arr[innerCounter] <= arr[innerCounter + 1])) {
					swap(arr, innerCounter, (innerCounter + 1));
				}
			}
		}
	}

	private final <T> void swap(T[] arr, int i, int j) {
		var tempVar1 = 0;
		if (true) {
			T t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
			var tempVar2 = 0;
			tempVar2++;
			tempVar2 = tempVar2 + 1;
		}
	}
}
