package SortAlgos;

/*
 * Unnecessary comment
 * Unnecessary comment
 * Unnecessary comment
 * */


public class SortAlgo1_6 {
	
	//
	//
	//
	//
	public void BubbleSortRecursion(Integer arr[], int n) {
		/*
		 * Unnecessary comment
		 * Unnecessary comment
		 * Unnecessary comment
		 * */
		if (n != 1) 
		{
			//
			
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

	/*
	 * Unnecessary comment
	 * Unnecessary comment
	 * Unnecessary comment
	 * */
	public void BubbleSortWithoutRecursion(Integer arr[]) {
		
		for(int i = arr.length; !(i < 1) ; i--) {
			
			////Comment
			//Comment
			//Comment
			
			for(int innerCounter = 0; innerCounter < arr.length-1; innerCounter++)
			{
				
				//Comment
				if (!(arr[innerCounter] <= arr[innerCounter + 1])) {
					
					
					swap(arr, innerCounter, (innerCounter + 1));
					
					    
				}
			}
		}
	}

	private final <T> void swap(T[] arr, int i, int j) {
		
		T t = arr[i];
		
		
		arr[i] = arr[j];
		
		arr[j] = t;
		
		
	}
}
