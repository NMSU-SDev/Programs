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

	//
	// Test scaling the size of a circle
	//
	@Test
	public void simpleScale()
	{
		double radius;
		System.out.println("Running test simpleScale.");
		radius = circle1.scale(2.5);
		//Assert.assertTrue(radius == 7.5);
		Assert.assertEquals(7.5, radius,  0.00001);
		
	}

	//
	// Test if the circles are the same
	//
	@Test
	public void sameCircleIntersect()
	{
		System.out.println("Running test sameCircleIntersect.");
		boolean doesIntersect = circle1.intersects(circle1);
		//Assert.assertTrue(radius == 7.5);
		Assert.assertTrue(doesIntersect == true);
		
	}


	//
	// Test if the circles intersect at two points
	//
	@Test
	public void twoPointIntersect()
	{
		System.out.println("Running test twoPointIntersect.");
		Circle1 circle2 = new Circle1(7, 3, 5);
		boolean doesIntersect = circle1.intersects(circle2);
		Assert.assertTrue(doesIntersect == true);
		
	}

	//
	// Test if circles intersect at one point
	//
	@Test
	public void onePointIntersect()
	{
		System.out.println("Running test onePointIntersect.");
		Circle1 circle2 = new Circle1(0, 2, 2);
		Circle1 circle3 = new Circle1(0, -3, 3);
		boolean doesIntersect = circle2.intersects(circle3);
		Assert.assertTrue(doesIntersect == true);
	}

	//
	// Test if circles do not intersect 
	//
	@Test
	public void noIntersect()
	{
		System.out.println("Running test noIntersect.");
		Circle1 circle2 = new Circle1(-6, 0, 1);
		boolean doesIntersect = circle1.intersects(circle2);
		Assert.assertTrue(doesIntersect == false);
		
	}


	//
	//Test if one circle is contained within the other
	//
	@Test
	public void nestedIntersect()
	{
		System.out.println("Running test nestedIntersect.");
		Circle1 circle2 = new Circle1(2, 3.5, 1);
		boolean doesIntersect = circle1.intersects(circle2);
		Assert.assertTrue(doesIntersect == true);
		
	}


	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
