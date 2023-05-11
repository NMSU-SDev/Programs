package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle1 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle1Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle1Test
{
	// Data you need for each test case
	private Circle1 circle1;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle1(1, 2, 3);
	}

	//
	// Stuff you want to do after each test case
	//
	@After
	public void teardown()
	{
		System.out.println("\nTest finished.");
	}

	
	//
	// Tests that two circles that are not intersecting are not intersecting
	//
	@Test
	public void intersectNoIntersection() {
		
		System.out.println("Running test: intersectNoIntersection.");
		
		// next to eachother same x
		System.out.println("intersectNoIntersection - next to eachother");
		Circle1 a = new Circle1(0, 50, 10);
		Circle1 b = new Circle1(0, 0, 5);
		
		Assert.assertFalse(a.intersects(b));
		Assert.assertFalse(b.intersects(a));
		
		// one above the other, almost touching
		System.out.println("intersectNoIntersection - one above, almost touching");
		a = new Circle1(1, 10, 2.99);
		b = new Circle1(1, 5, 2);
		
		Assert.assertFalse(a.intersects(b));
		Assert.assertFalse(b.intersects(a));
		
		// circle above to the right
		System.out.println("intersectNoIntersection - above to the right, very close");
		a = new Circle1(16, 128, 4);
		b = new Circle1(23.45, 121.6, 5);
		
		Assert.assertFalse(a.intersects(b));
		Assert.assertFalse(b.intersects(a));
		
		// circle is inside the other circle
		System.out.println("intersectNoIntersection - circle a is inside circle b");
		a = new Circle1(0, 0, 2);
		b = new Circle1(0, 0, 5);
		
		// circle is inside the other circle
		System.out.println("intersectNoIntersection - circle b is inside circle a");
		a = new Circle1(0, 0, 5);
		b = new Circle1(0, 0, 2);
		
		Assert.assertFalse(a.intersects(b));
		Assert.assertFalse(b.intersects(a));
		
	} // end noIntersection
	
	//
	// Tests two circles that are completly overlapping
	//
	@Test
	public void intersectCompleteOverlap() {
		
		System.out.println("Running test: intersectCompleteOverlap.");
		
		// next to eachother same x
		System.out.println("intersectCompleteOverlap - at origin");
		Circle1 a = new Circle1(0, 0, 4);
		Circle1 b = new Circle1(0, 0, 4);
		
		Assert.assertTrue(a.intersects(b));
		Assert.assertTrue(b.intersects(a));
	}
	
	//
	// Tests two circles that are intersecting at a single point
	//
	@Test
	public void intersectAtOnePoint() {
		
		System.out.println("Running test: intersectAtOnePoint.");
		
		// next to each other same x
		System.out.println("intersectAtOnePoint - on x-axis");
		Circle1 a = new Circle1(0, 0, 3);
		Circle1 b = new Circle1(5, 0, 2);
		
		Assert.assertTrue(a.intersects(b));
		Assert.assertTrue(b.intersects(a));
	}
	
	//
	// Tests two circles that are intersecting at two points
	//
	@Test
	public void intersectAtTwoPoints() {
		
		System.out.println("Running test: intersectAtTwoPoints.");
		
		// next to eachother same x
		System.out.println("intersectAtTwoPoints - on x-axis");
		Circle1 a = new Circle1(2, 0, 1);
		Circle1 b = new Circle1(4, 0, 2);
		
		Assert.assertTrue(a.intersects(b));
		Assert.assertTrue(b.intersects(a));
	}
	
	//
	// Tests scaling a circle to a bigger circle
	//
	@Test
	public void scaleBigger() {
		
		System.out.println("Running test: scaleBigger.");
		
		// next to eachother same x
		System.out.println("scaleBigger - x 2.0");
		
		circle1 = new Circle1(1, 1, 1);
		
		circle1.scale(2.0);
		
		Assert.assertTrue(circle1.radius == 2.0);
	}
	
	//
	// Tests scaling a circle to a smaller circle
	//
	@Test
	public void scaleSmaller() {
		
		System.out.println("Running test: scaleSmaller.");
		
		// next to eachother same x
		System.out.println("scaleSmaller - x 0.5");
		
		circle1 = new Circle1(1, 1, 1);
		
		circle1.scale(0.5);
		
		Assert.assertTrue(circle1.radius == 0.5);
	}
	
	//
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMove.");
		p = circle1.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running test simpleMoveNeg.");
		p = circle1.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
