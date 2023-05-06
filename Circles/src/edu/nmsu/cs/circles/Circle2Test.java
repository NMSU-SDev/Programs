package edu.nmsu.cs.circles;

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
	// Test circle with a positive move
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
		p = circle1.moveBy(-2, -2);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}
	//
	// Test circle with a zero value
	//
	@Test
	public void zeroMove()
	{
		double radius;
		System.out.println("Running test zeroMove.");
		radius = circle1.scale(0);
		Assert.assertEquals(Double.NaN, radius,  0.00001);
		
	}

	//
	// Test circle with a positive move
	//
	@Test
	public void negMove()
	{
		double radius;
		System.out.println("Running test negMove.");
		radius = circle1.scale(-5);
		Assert.assertEquals(Double.NaN, radius,  0.00001);
		
	}

	//
	// Test two circles for intersection
	//
	@Test
	public void intersectTest()
	{
		System.out.println("Running test intersectTest.");
		Circle1 circle2 = new Circle1(3, 4, 2);
		Circle1 circle3 = new Circle1(2, 3, 1);
		boolean doesIntersect = circle2.intersects(circle3);
		Assert.assertTrue(doesIntersect == true);
	}

	//
	// Test circles that don't intersect
	//
	@Test
	public void noIntersectTest()
	{
		System.out.println("Running test noIntersectTest.");
		Circle2 circle2 = new Circle2(-3, 1, 2);
		boolean doesIntersect = circle1.intersects(circle2);
		Assert.assertTrue(doesIntersect == false);
		
	}
}
