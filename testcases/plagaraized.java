public void BubbleSortRecursion(Integer arr[], int n) {
    switch (n) {
    case 1:
        return;
    }

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