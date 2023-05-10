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

public class Circle2Test
{
	// Data you need for each test case
	private Circle2 circle1;

	// Stuff you want to do before each test case
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle2(1, 2, 3);
	}

	// Stuff you want to do after each test case
	@After
	public void teardown()
	{
		System.out.println("\nTest finished.");
	}

	// Testing intersections of two different circles at one point
	@Test
	public void onePointIntersection()
	{
		System.out.println("Test: onePointIntersection.");
		Circle2 circle2 = new Circle2(0, 5, 5);
		Circle2 circle3 = new Circle2(0, -5, 5);
		Assert.assertTrue(circle2.intersects(circle3) == true);
	}

	// Testing intersections of two different circles at two points
	@Test
	public void twoPointIntersection()
	{
		System.out.println("Test: twoPointIntersection.");
		Circle2 circle2 = new Circle2(2, 5, 5);
		Circle2 circle3 = new Circle2(4, 3, 5);
		Assert.assertTrue(circle2.intersects(circle3) == true);
	}

	// Testing merged circle intersection
	@Test
	public void mergedCircles()
	{
		System.out.println("Test: mergedCircles.");
		Assert.assertTrue(circle1.intersects(circle1) == true);
	}

	// Testing creation of Circle2 object
	@Test
	public void valuesSet()
	{
		System.out.println("Test: valuesSet.");
		Assert.assertTrue(circle1.getPointX() == 1 && circle1.getPointY() == 2);
	}

	// Testing creation of Circle2 object using negative radius
	@Test
	public void negRadius()
	{
		System.out.println("Test: negRadius.");
		Circle2 circle2 = new Circle2(1, 1, -5);
		Assert.assertEquals(5, circle2.getRadius(), 0.00);
	}
	
	
	

	

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
