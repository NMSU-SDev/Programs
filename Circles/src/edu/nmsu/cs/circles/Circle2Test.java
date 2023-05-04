package edu.nmsu.cs.circles;

import org.junit.*;

public class Circle2Test
{
	// Data you need for each test case
	private Circle2 circle2;
	private Circle2 circle3;
	private Circle2 circle4;
	private Circle2 circle5;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle2 = new Circle2(1, 2, 3);

		circle3 = new Circle2(1, 3, 1); // should overlap

		circle4 = new Circle2(30, 2, 30); // should overlap

		circle5 = new Circle2(20, 30, 2); // shouldn't overlap
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
	// Test a simple intersection, radius > distance
	//
	@Test
	public void simpleIntersectTest()
	{
		Point p;
		System.out.println("Running test simpleIntersectTest, radius > distance.");
		System.out.println(circle2.intersects(circle3));
	}

	//
	// Test a complex intersection, other.radius > distance
	//
	@Test
	public void complexIntersectTest()
	{
		Point p;
		System.out.println("Running test complexIntersectTest, other.radius > distance.");
		System.out.println(circle2.intersects(circle4));
	}

	//
	// Test a non-intersection, radius < distance
	//
	@Test
	public void nonIntersectTest()
	{
		Point p;
		System.out.println("Running test nonIntersectTest, radius < distance.");
		System.out.println(circle2.intersects(circle5));
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle2Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}

