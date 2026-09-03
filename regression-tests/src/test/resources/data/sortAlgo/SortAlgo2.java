package SortAlgos;


public class SortAlgo2 {

	public void ChangeFunctionName(Integer changeVariableArray[], int counter) {
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

	public void ChangeFunctionNameWithoutRecursion(Integer arr[]) {
		for(int otherVariable = arr.length; otherVariable > 1 ; otherVariable--) {
			for(int counter = 0; counter < arr.length-1; counter++)
			{
				if (arr[counter] > arr[counter + 1]) {
					paws(arr, counter, (counter + 1));
				}
			}
		}
	}

	private final <T> void paws(T[] otherArr, int i, int j) {
		T t = otherArr[i];
		otherArr[i] = otherArr[j];
		otherArr[j] = t;
	}
}
