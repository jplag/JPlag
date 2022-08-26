package SortAlgos;

public class SortAlgo1_2 {
	//Unnecessary comment
	public void ChangeFunctionName(Integer changeVariableArray[], int counter) {
		
		/*  
		 * Comment
		 * */
		if (counter == 1) 
		{
			return;
		}

		for (int i = 0; i < counter - 1; i++)
		{
			
			
			if (changeVariableArray[i] > changeVariableArray[i + 1]) 
			{ 
				
				
				paws(changeVariableArray, i , i+1);
				
				
			}
		}
		ChangeFunctionName(changeVariableArray, counter - 1);
	}
		
	/*
		
		Unnecessary comment
		*/
	public void ChangeFunctionNameWithoutRecursion(Integer arr[]) {
		//Unnecessary comment
		for(int otherVariable = arr.length; otherVariable > 1 ; otherVariable--) {
			
			
			for(int counter = 0; counter < arr.length-1; counter++)
				
			{
				if (arr[counter] > arr[counter + 1]) {
					paws(arr, counter, (counter + 1));
					
				}
				
			}
		}
	}
		public void BubbleSortWithoutRecursion(Integer arr[]) {
			//Unnecessary comment
			for(int i = arr.length; i > 1 ; i--) {
				
				for(int innerCounter = 0; innerCounter < arr.length-1; innerCounter++)
					
				{
					
					if (arr[innerCounter] > arr[innerCounter + 1]) {
						paws(arr, innerCounter, (innerCounter + 1));
						
					}
					
				}
			}
		}

		////Unnecessary comment
		private final <T> void paws(T[] arr, int i, int j) {
			T t = arr[i];
			//Comment
			arr[i] = arr[j];
			//Comment
			arr[j] = t;
		}
}


//Comment