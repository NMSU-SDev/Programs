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
	// Test circle initialization with all positive values
	//
	@Test
	public void circleInitializationTestPositive()
	{
		Circle1 testCircle;
		System.out.println("Running test circleInitializationTestPositive.");
		testCircle = new Circle1(1, 3, 7);
		Assert.assertTrue(testCircle.center.x == 1 &&
						  testCircle.center.y == 3 &&
						  testCircle.radius == 7);
	}

	//
	// Test circle initialization with all negative values
	//
	@Test
	public void circleInitializationTestNegative()
	{
		Circle1 testCircle;
		System.out.println("Running test circleInitializationTestNegative.");
		testCircle = new Circle1(-1, -3, -7);
		Assert.assertTrue(testCircle.center.x == -1 &&
						  testCircle.center.y == -3 &&
						  testCircle.radius == 7);
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
	// Test radius increase
	//
	@Test
	public void radiusIncrease()
	{
		double r;
		System.out.println("Running test radiusIncrease.");
		r = circle1.scale(2);
		Assert.assertTrue(r == 6);
	}

	//
	// Test radius decrease
	//
	@Test
	public void radiusDecrease()
	{
		double r;
		System.out.println("Running test radiusDecrease.");
		r = circle1.scale(0.5);
		Assert.assertTrue(r == 1.5);
	}

	//
	// Test intersection of two circles with changing x values;
	// returns true
	//
	@Test
	public void intersectOnlyXChange()
	{
		System.out.println("Running test intersectOnlyXChange.");
		Circle1 testCircle;
		testCircle = new Circle1(8, 2, 5);
		Assert.assertTrue(circle1.intersects(testCircle));
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
