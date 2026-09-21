package SortAlgos;

public class SortAlgo1_3 {
	
	private boolean _notUsedBool = false;
	private int _thisIsNotUsed = 0;
//Unnecessary comment
	public void BubbleSortRecursion(Integer arr[], int n) {
		if (_thisIsNotUsed == 0 && !_notUsedBool)
			if (n == 1) {
				return;
			}
		/*
		 * Unnecessary comment
		 * */

		var tempVar = 0;
		//Comment
		var tempVar1 = tempVar + 1;
		//Comment
		var tempVar2 = tempVar1 + 1;
		
		for (int i = 0; i < n - 1; i++) {
			
			/*
			 * Comment
			 * Comment
			 * */
			if (arr[i] > arr[i + 1]) {
				
				
				swap(arr, i, i + 1);
				
			}
		}
		
		
		//Unnecessary comment
		BubbleSortRecursion(arr, n - 1);
	}
/*
 * Unnecessary comment
 * Unnecessary comment
 * */
	public void BubbleSortWithoutRecursion(Integer arr[]) {
		//Unnecessary comment
		var tempVar = 0;
		var tempVar1 = tempVar + 1;
		tempVar1 = tempVar1 + 1;
		tempVar = tempVar1 *1;
		
		for (int i = arr.length; i > 1; i--) {
			//Comment
			for (int innerCounter = 0; innerCounter < arr.length - 1; innerCounter++) {
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					
					swap(arr, innerCounter, (innerCounter + 1));
					
					
					
				}
			}
		}
	}

	private final <T> void swap(T[] arr, int i, int j) {
		var tempVar1 = 0;
		var tempVar2 = tempVar1 + 1;
		tempVar2 = tempVar2 + 1;
		
		//Unnecessary comment
		
		
		if (true) {
			T t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
			tempVar2 = 0;
		}
	}
}
