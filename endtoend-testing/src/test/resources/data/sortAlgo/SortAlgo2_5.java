package SortAlgos;

public class SortAlgo2_5 {
	public void ChangeFunctionName(Integer changeVariableArray[], int counter) {
		switch (counter) {
		case 1:
			return;
		}

		int i = 0;
		while(i < counter-1)
		{
			var tempBool = changeVariableArray[i] > changeVariableArray[i + 1];
			if (tempBool) {
				paws(changeVariableArray, i, i + 1);
			}
			i++;
		}
		
		ChangeFunctionName(changeVariableArray, counter - 1);
	}

	public void ChangeFunctionNameWithoutRecursion(Integer arr[]) {
		int otherVariable = arr.length;
		while(otherVariable > 1)
		{
			int innerCounter = 0;
			while(innerCounter < arr.length -1)
			{
				if (arr[innerCounter] > arr[innerCounter + 1]) {
					paws(arr, innerCounter, (innerCounter + 1));
				}
				innerCounter++;
			}
			otherVariable--;
		}
	}

	private final <T> void paws(T[] otherArr, int i, int j) {
		T t = otherArr[i];
		otherArr[i] = otherArr[j];
		otherArr[j] = t;
	}
}
