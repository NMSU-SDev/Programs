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
	// Test an intersection of two circles
	//
	@Test
	public void intersectionOfTwoCircles(){
		System.out.println("Test Starting...");
		System.out.println("This is two circles intersecting");
		Circle2 circle1 = new Circle2(0, 2, 3);
		Assert.assertTrue(circle2.intersects(circle1) == true);
	}

	//
	// Test an that two circles meet but not intersect
	//
	@Test
	public void meetingOfTwoCircles(){
		System.out.println("Test Starting...");
		System.out.println("This is two circles meeting, but not intersecting");
		Circle2 circle1 = new Circle2(-5, 2, 3);
		Assert.assertTrue(circle2.intersects(circle1) == true);
	}

	//
	// Test that two circles do not meet at all
	//
	@Test
	public void noMeetingOfTwoCircles(){
		System.out.println("Test Starting...");
		System.out.println("This is two circles meeting, but not intersecting");
		Circle2 circle1 = new Circle2(-7, 2, 3);
		Assert.assertTrue(circle2.intersects(circle1) == true);
	}

	//
	// Test scaling a circle by a factor of 3
	//
	@Test
	public void scaleCircle(){
		System.out.println("Test Starting...");
		System.out.println("Scaling of a circle by a factor of 3");
		double oldSize = circle2.radius;
		double newSize = circle2.scale(2);
		Assert.assertTrue(oldSize * 2 == newSize);
	}

	//
	// Test shrinking a circle by a factor of 0.5
	//
	@Test
	public void shrinkCircle(){
		System.out.println("Test Starting...");
		System.out.println("Shrikning of a circle by a factor of 0.5");
		double oldSize = circle2.radius;
		double newSize = circle2.scale(0.5);
		Assert.assertTrue(oldSize * 0.5 == newSize);
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
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
