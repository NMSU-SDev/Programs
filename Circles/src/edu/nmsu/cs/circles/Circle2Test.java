package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle1 (and Circle)
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
	
	//
	// Test move with different x and y values
	// 
	@Test
	public void unevenMove() {
		Point p;
		System.out.println("Running test unevenMove.");
		p = circle2.moveBy(5, 4);
		Assert.assertTrue(p.x == 6 && p.y == 6);
	}
	
	// 
	// Test a simple scale 
	// 
	@Test
	public void scale() {
		double r;
		System.out.println("Running test scale.");
		r = circle2.scale(2.0);
		Assert.assertTrue(r == 6.0);
	}
	
	//
	// Test an intersect function--should return true
	//
	@Test
	public void intersect() {
		Circle1 iCircle = new Circle1(3, 5, 2);
		boolean i;
		System.out.println("Running test intersect.");
		i = circle2.intersects(iCircle);
		Assert.assertTrue(i);
	}
	
	//
	// Test intersect function on touching circles--should return false
	//
	@Test
	public void noIntersect() {
		Circle1 iCircle = new Circle1(8, 2, 4);
		boolean i;
		System.out.println("Running test noIntersect.");
		i = circle2.intersects(iCircle);
		Assert.assertFalse(i);
	}
	
	
	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle2Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
