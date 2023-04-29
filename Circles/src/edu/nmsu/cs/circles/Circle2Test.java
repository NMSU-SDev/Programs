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
	private Circle2 circle1;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle2(1, 2, 3);
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
	// Test constructing a circle with a negative radius
	//
	@Test
	public void negRadius()
	{
		System.out.println("Running test negRadius.");
		Circle2 testCircle = new Circle2(2, 5, -3);
		Assert.assertTrue(testCircle.center.x == 2 && testCircle.center.y == 5 && testCircle.radius == 3);
	}

	//
	// Test a simple move to the first quadrent
	//
	@Test
	public void simpleMoveQuad1()
	{
		Point p;
		System.out.println("Running test simpleMoveQuad1.");
		p = circle1.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple move to the second quadrant
	//
	@Test
	public void simpleMoveQuad2()
	{
		Point p;
		System.out.println("Running test simpleMoveQuad2.");
		p = circle1.moveBy(-5, 3);
		Assert.assertTrue(p.x == -4 && p.y == 5);
	}

	//
	// Test a simple move to the third quadrant
	//
	@Test
	public void simpleMoveQuad3()
	{
		Point p;
		System.out.println("Running test simpleMoveQuad3.");
		p = circle1.moveBy(-2, -1);
		Assert.assertTrue(p.x == -1 && p.y == 1);
	}

	
	//
	// Test a simple move to the fourth quadrant
	//
	@Test
	public void simpleMoveQuad4()
	{
		Point p;
		System.out.println("Running test simpleMoveQuad4.");
		p = circle1.moveBy(5, -17);
		Assert.assertTrue(p.x == 6 && p.y == -15);
	}


	//
	// Test scaling the size of a circle by positive factor
	//
	@Test
	public void posScale()
	{
		double radius;
		System.out.println("Running test posScale.");
		radius = circle1.scale(2.5);
		Assert.assertEquals(7.5, radius,  0.00001);
		
	}

	//
	// Test scaling the size of a circle by 0
	//
	@Test
	public void zeroScale()
	{
		double radius;
		System.out.println("Running test zeroScale.");
		radius = circle1.scale(0);
		Assert.assertEquals(Double.NaN, radius,  0.00001);
		
	}

	//
	// Test scaling the size of a circle by 0
	//
	@Test
	public void negScale()
	{
		double radius;
		System.out.println("Running test negScale.");
		radius = circle1.scale(-1);
		Assert.assertEquals(Double.NaN, radius,  0.00001);
		
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
		Circle2 circle2 = new Circle2(7, 3, 5);
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
		Circle2 circle2 = new Circle2(-6, 0, 1);
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
		Circle2 circle2 = new Circle2(2, 3.5, 1);
		boolean doesIntersect = circle1.intersects(circle2);
		Assert.assertTrue(doesIntersect == true);
		
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
