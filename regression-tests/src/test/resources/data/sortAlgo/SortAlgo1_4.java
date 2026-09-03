package SortAlgos;
/*
 * Unnecessary comment
 * Unnecessary comment
 * Unnecessary comment
 * */
public class SortAlgo1_4 {
	
	/*
	
	Unnecessary comment
	*/
	public void BubbleSortWithoutRecursion(Integer arr[]) {
		//Unnecessary comment
		for(int i = arr.length; i > 1 ; i--) {
			
			for(int innerCounter = 0; innerCounter < arr.length-1; innerCounter++)
				
			{
				
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					swap(arr, innerCounter, (innerCounter + 1));
					
				}
			}
		}
	}
	

	////Unnecessary comment
	private final <T> void swap(T[] arr, int i, int j) {
		T t = arr[i];
		
		arr[i] = arr[j];
		
		arr[j] = t;
	}
	
	//Unnecessary comment
		public void BubbleSortRecursion(Integer arr[], int n) {
			

			for (int i = 0; i < n - 1; i++)
			{
				
				if (arr[i] > arr[i + 1]) 
					
				{ 
					
					swap(arr, i , i+1);
					
				}
			}
			
			
			if (n == 2) {
				
				return;
				
			} else {
				
				
				BubbleSortRecursion(arr, n - 1);
				
			}
			
		}
		
	

}
