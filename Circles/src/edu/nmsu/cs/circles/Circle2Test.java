package edu.nmsu.cs.circles;

import java.beans.Transient;


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
        System.out.println(p);
	}

	@Test
	public void positiveMove()
	{

		Point p;
		System.out.println("Running Test: Positive Move By");
		p = circle2.moveBy(1.1, 1.1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
        System.out.println(p);
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
        System.out.println(p);
	}

	//tests for no intersect
	@Test
	public void noIntersect()
	{
		System.out.println("Running Test: no intersect");

		Circle2 baseCircle = new Circle2(0, 50, 10);
		Circle2 nextCircle = new Circle2(0, 0, 5); 
		Assert.assertFalse(baseCircle.intersects(nextCircle));
		Assert.assertFalse(nextCircle.intersects(baseCircle));

		baseCircle = new Circle2(1, 10, 2.99);
		nextCircle = new Circle2(1, 5, 5); 
		Assert.assertFalse(baseCircle.intersects(nextCircle));
		Assert.assertFalse(nextCircle.intersects(baseCircle));
	}

	@Test
	public void overlap()
	{

		System.out.println("Running Test: Overlap");

		Circle2 baseCircle = new Circle2(0, 0, 10);
		Circle2 nextCircle = new Circle2(0, 0, 10); 
		Assert.assertFalse(baseCircle.intersects(nextCircle));
		Assert.assertFalse(nextCircle.intersects(baseCircle));

	}

	@Test

	public void intersectAtOnePoint()
	{
		System.out.println("Running Test: IntersectAtOnePoint");

		System.out.println("intersectAtOnePoint - on x axis");
		Circle2 baseCircle = new Circle2(0,0,3);
		Circle2 nextCircle = new Circle2(7, 0, 4);
		Assert.assertTrue(baseCircle.intersects(nextCircle));
		Assert.assertTrue(nextCircle.intersects(baseCircle));
	}

	@Test
	public void scaleTest() 
	{
		double r=2; 
        double x = 1;
        double y = 1;
		System.out.println("Running Test: Scale");
		r = circle2.scale(2);
		Circle2 baseCircle = new Circle2(x, y, r);
        System.out.println(r);

	}



}