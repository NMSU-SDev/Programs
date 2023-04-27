package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle2 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle2Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle2Test
{
	// Data you need for each test case
	private Circle2 circle2;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle2 = new Circle2(1, 2, 3);
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
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMove.");
		p = circle2.moveBy(1, 1);
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
		p = circle2.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("circle2Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/



	// Test the scale() method
	@Test
	public void testScale()
	{
		System.out.println("Running test testScale.");
		double newRadius = circle2.scale(2);
		Assert.assertEquals(newRadius, 6, 0.00001);
	}

	// Test intersection with another circle2
	@Test
	public void testIntersectsWithSelf()
	{
		System.out.println("Running test testIntersectsWithSelf.");
		Assert.assertTrue(circle2.intersects(circle2));
	}

	// Test intersection with another circle2 that intersects
	@Test
	public void testIntersectsWithIntersectingCircle()
	{
		System.out.println("Running test testIntersectsWithIntersectingCircle.");
		Circle2 other = new Circle2(2, 3, 2);
		Assert.assertTrue(circle2.intersects(other));
	}

	// Test intersection with another circle2 that doesn't intersect
	@Test
	public void testIntersectsWithNonIntersectingCircle()
	{
		System.out.println("Running test testIntersectsWithNonIntersectingCircle.");
		Circle2 other = new Circle2(10, 10, 1);
		Assert.assertFalse(circle2.intersects(other));
	}

	// Test intersection with another Circle2
	@Test
	public void testIntersectsWithCircle2()
	{
		System.out.println("Running test testIntersectsWithCircle2.");
		Circle2 other = new Circle2(2, 3, 2);
		Assert.assertTrue(circle2.intersects(other));
	}

}
