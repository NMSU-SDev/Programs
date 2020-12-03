public class Sorting {

    private int arrayQuick[];
    private int arrayMerge[];
    private int arrayBubble[];
    private static boolean quick = false, merge = false, bubble = false;

    public static void main(String[] args) {

       Sorting sort = new Sorting();
       int[] inArray = new int[100000];
       for (int i = 0; i < inArray.length; i++) {
           inArray[i] = (int)(Math.random() * 1001);
       }
       System.out.println("Passing array input size of 512 into QuickSort");
       System.out.print("Result: {");

       long start = System.nanoTime();

       sort.sortArray(inArray);
       float totalTimeElapsed = System.nanoTime() - start;

       for (int conversion = 0; conversion < 9; conversion++ )
            totalTimeElapsed /= 10;

       for (int i : sort.arrayQuick) {
           quick = true;
           System.out.print(i + ", ");
       }
       System.out.print("}");
       System.out.println("\nTotal time elapsed: " + totalTimeElapsed + "\n");

       int[] newArray = new int[100000];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = (int)(Math.random() * 1001);
        }
       System.out.println("\nPassing array input size of 512 into MergeSort");
       System.out.print("Result: {");

       start = System.nanoTime();

       sort.sortArray(newArray);
       totalTimeElapsed = System.nanoTime() - start;
        for (int conversion = 0; conversion < 9; conversion++ )
            totalTimeElapsed /= 10;

        for (int i : sort.arrayMerge) {
            merge = true;
            System.out.print(i + ", ");
        }
        System.out.print("}");
        System.out.println("\nTotal time elapsed: " + totalTimeElapsed + "\n");

        int[] newerArray = new int[100000];
        for (int i = 0; i < newerArray.length; i++) {
            newerArray[i] = (int)(Math.random() * 1001);
        }
        System.out.println("\nPassing array input size of 512 into BubbleSort");
        System.out.print("Result: {");

        start = System.nanoTime();

        sort.sortArray(newerArray);
        totalTimeElapsed = System.nanoTime() - start;
        for (int conversion = 0; conversion < 9; conversion++ )
            totalTimeElapsed /= 10;

        for (int i : sort.arrayBubble) {
            bubble = true;
            System.out.print(i + ", ");
        }
        System.out.print("}");
        System.out.println("\nTotal time elapsed: " + totalTimeElapsed + "\n");

    }

    public void sortArray(int[] inArray) {


        int arrayLength = inArray.length;

        if (!quick) {
            this.arrayQuick = inArray;
            QuickSort( 0, arrayLength - 1  );
        }

        if (!merge && quick) {
            this.arrayMerge = inArray;
            merge(inArray, 0, arrayLength - 1);
        }

        if (!bubble && quick && merge) {
            this.arrayBubble = inArray;
            BubbleSort(inArray);
        }


    }

    public void swapQuick(int i, int j) {

        int swapTempNum = arrayQuick[j];
        arrayQuick[j] = arrayQuick[i];
        arrayQuick[i] = swapTempNum;

    }

    public void swapBubble(int i, int j) {

        int swapTempNum = arrayBubble[j];
        arrayBubble[j] = arrayBubble[i];
        arrayBubble[i] = swapTempNum;

    }

    public void QuickSort( int low, int high) {
        int pivot = arrayQuick[low + ( high - low ) / 2];

        int i = low, j = high;

        while (i <= j) {



            while(arrayQuick[i] < pivot) {
                i++;
            }

            while (arrayQuick[j] > pivot) {
                j--;
            }

            if (i <= j) {

                swapQuick(i,j);
                i++;
                j--;

            }

        }

        if (low < j)
            QuickSort(low, j);
        if (i < high)
            QuickSort(i, high);

    }

    public static void MergeSort(int[] inArray, int start, int middle, int end) {

        int num1 = middle - start + 1;
        int num2 = end - middle;

        int leftArray[] = new int[num1];
        int rightArray[] = new int[num2];

        for (int i = 0; i < num1; i++)
            leftArray[i] = inArray[start + i];
        for (int j = 0; j < num2; j++)
            rightArray[j] = inArray[middle + 1 + j];

        int i = 0, j = 0, k = start;

        while (i < num1 && j < num2) {
            if (leftArray[i] <= rightArray[j]) {
                inArray[k] = leftArray[i];
                i++;
            }
            else {
                inArray[k] = rightArray[j];
                j++;
            }
            k++;
        }
        while (i < num1) {
            inArray[k] = leftArray[i];
            k++;
            i++;
        }

        while(j < num2) {
            inArray[k] = rightArray[j];
            k++;
            j++;
        }

    }

    public static void merge(int inArray[], int start, int end ) {

        if (start < end) {
            int middle = (start + end) / 2;

            merge(inArray, start, middle);
            merge(inArray, middle + 1, end);

            MergeSort(inArray, start, middle, end);
        }
    }

    public void BubbleSort(int inArray[]) {

        int n = inArray.length;
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (inArray[j] > inArray[j+1]) {
                    swapBubble(j, j+1);
                }
    }

}
