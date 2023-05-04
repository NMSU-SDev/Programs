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
	private Circle1 circle2;
	private Circle1 circle3;
	private Circle1 circle4;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle1(1, 2, 3);

		circle2 = new Circle1(1, 3, 1); // should overlap

		circle3 = new Circle1(30, 2, 30); // should overlap

		circle4 = new Circle1(20, 30, 2); // shouldn't overlap
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
	// Test a simple intersection, radius > distance
	//
	@Test
	public void simpleIntersectTest()
	{
		Point p;
		System.out.println("Running test simpleIntersectTest, radius > distance.");
		System.out.println(circle1.intersects(circle2));
	}

	//
	// Test a complex intersection, other.radius > distance
	//
	@Test
	public void complexIntersectTest()
	{
		Point p;
		System.out.println("Running test complexIntersectTest, other.radius > distance.");
		System.out.println(circle1.intersects(circle3));
	}

	//
	// Test a non-intersection, radius < distance
	//
	@Test
	public void nonIntersectTest()
	{
		Point p;
		System.out.println("Running test nonIntersectTest, radius < distance.");
		System.out.println(circle1.intersects(circle4));
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
