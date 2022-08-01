package SortAlgos;
/*
 * Unnecessary comment
 * Unnecessary comment
 * Unnecessary comment
 * */
public class SortAlgo1_5 {
	
	public void BubbleSortRecursion(Integer arr[], int n) {
		
		switch (n) {
		
		case 1:
			return;
			
			
			
		}
	////Unnecessary comment
	////Unnecessary comment
	////Unnecessary comment
		/*
		 * 
		 * 
		 * 
		 * */
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
////Unnecessary comment
	public void BubbleSortWithoutRecursion(Integer arr[]) {
		
		
		int i = arr.length;
		
	////Unnecessary comment
		while(i > 1)
		{
		////Unnecessary comment
			int innerCounter = 0;
			
			
			while(innerCounter < arr.length -1)
			////Unnecessary comment
			{
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					
					
					swap(arr, innerCounter, (innerCounter + 1));
					
				}
				
				
				innerCounter++;
				
				
			}
			i--;
		}
	}
////Unnecessary comment
	private final <T> void swap(T[] arr, int i, int j) {
		
	////Unnecessary comment////Unnecessary comment
		T t = arr[i];
		
		
	////Unnecessary comment
		arr[i] = arr[j];
		
	////Unnecessary comment
		arr[j] = t;
	}
}
