public class Fibonacci {

	public static void main(String[] args) {
		
		testExponential();
		testPolynomial();
		
		ExponentialOverTime();
		PolynomialOverTime();
		
	} // end main
	
	public static int exponential(int n) {
		
		if ( n == 0 ) return 0;
		if ( n == 1 ) return 1;
		
		return exponential( n - 1 ) + exponential( n - 2 );
	
	} // end exponential

	public static int polynomial(int n) {
		
		if ( n == 0 ) return 0;
		
		int[] array = new int[ n + 1 ];
		
		array[0] = 0;
		array[1] = 1;
		
		for (int i = 2; i <= n; i++)		
			array[i] = array[ i - 1 ] + array[ i - 2 ];
			
		return array[n];
		
	} // end polynomial
	
	public static void testExponential() {
		
		int[] answer = {0,1,1,2,3,5,8,13,21,34,55};
		
		for (int i = 0; i < 10; i++) {
			
			if (answer[i] != exponential(i)) {
				
				System.out.println("Error, exponential function failed.");
				return;
				
			} // end if

		} // end for
		
		System.out.println("Exponential function passed.");
	
	} // end testExponential
	
	public static void testPolynomial() {
		
		int[] answer = {0,1,1,2,3,5,8,13,21,34,55};
		
		for (int i = 0; i < 10; i++) {
			
			if (answer[i] != polynomial(i)) {
				
				System.out.println("Error, polynomial function failed.");
				return;
				
			} // end if

		} // end for
		
		System.out.println("Polynomial function passed.");

	} // end testPolynomial
	
	public static void ExponentialOverTime() {

		long[] times = new long[61];
		float[] SecondsDuration = new float[61];

		for (int i = 0; i < 61; i++) {
			
			long start = System.nanoTime();
			
			int numExp = exponential(i);

			float inSeconds = System.nanoTime() - start;
			
			for (int conversion = 0; conversion < 9; conversion++ )
				inSeconds /= 10;
			
			System.out.println("Exponential: " + numExp + " " + "Time: " + inSeconds + " " + "Current Time: " + start + " " + "Index: " + i);

			times[i] = start;
			SecondsDuration[i] = inSeconds;
			
		} // end for

		float totalTimeElapsed = (times[times.length - 1]) - times[0];

		for (int conversion = 0; conversion < 9; conversion++ )
			totalTimeElapsed /= 10;

		System.out.println("Total time elapsed: " + totalTimeElapsed + "\n");

		System.out.println("List to copy + paste Exponential data from:");

		int spreadSheetNum = 1;

		for (int index = 0; index < times.length; index++) {

			System.out.println("= " + "B" + (spreadSheetNum) + " " + "+" + SecondsDuration[index]);
			spreadSheetNum++;

		} // end for
		
	} // end ExponentialOverTime
	
public static void PolynomialOverTime() {

		long[] times = new long[61];
		float[] SecondsDuration = new float[61];

		for (int i = 0; i < 61; i++) {
			
			long start = System.nanoTime();
			
			int numPoly = polynomial(i);

			float inSeconds = System.nanoTime() - start;
			
			for (int conversion = 0; conversion < 9; conversion++ )
				inSeconds /= 10;
			
			System.out.println("Polynomial: " + numPoly + " " + "Time: " + inSeconds + " " + "Current Time: " + start + " " + "Index: " + i);

			times[i] = start;
			SecondsDuration[i] = inSeconds;

		} // end for

		float totalTimeElapsed = (times[times.length - 1]) - times[0];

		for (int conversion = 0; conversion < 9; conversion++ )
			totalTimeElapsed /= 10;

		System.out.println("Total time elapsed: " + totalTimeElapsed + "\n");

		System.out.println("List to copy + paste Polynomial data from:");

		int spreadSheetNum = 1;

		for (int index = 0; index < times.length; index++) {

			System.out.println("= " + "C" + (spreadSheetNum) + " " + "+" + SecondsDuration[index]);
			spreadSheetNum++;

		} // end for

	} // end PolynomialOverTime
	
} // end class
