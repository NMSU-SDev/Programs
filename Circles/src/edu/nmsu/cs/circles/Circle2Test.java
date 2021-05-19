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
	private Circle2 circle2; 
	private Circle2 circle3;
	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle2(1, 2, 3);
		circle2 = new Circle2(0, 0, 6);
		circle3 = new Circle2(2, 1, 4);
		
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
	// Test a positive & negative move
	//
	@Test
	public void simpleMove()
	{//found error for Circle2() constructor -x,y was flipped
		Point p;
		System.out.println("Running test simpleMove.");
		p = circle1.moveBy(2, -1);
		Assert.assertTrue(p.x == 3 && p.y == 1);
		
	}

	//
	// Test a simple negative move
	//
	@Test
	public void simpleScale()
	{ //found error for Circle scale() 
		Double r = circle1.scale(1.5);
		Assert.assertTrue(r == 4.5);
	}

	//
	// Test intersects - false
	//
	@Test
	public void simpleIntersects20()
	{//found error for Circle2 intersects() 
		Assert.assertTrue( false == circle1.intersects(circle2));
		
	}
	
	//
	// Test intersects - true
	//
	@Test
	public void simpleIntersects21()
	{
		Assert.assertTrue( true == circle1.intersects(circle3));
		
	}
	//extra cases if I ran test with prototypes only:
	//scale up and down, move positive/negative x,y direction,
	//intersects - touching case, in different quad, overlapping circle case 
	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
