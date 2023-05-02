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
    private final double delta = 1e-15;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle1(1, 2, 3);
		circle2 = new Circle1(1, 2, 4);
        circle3 = new Circle1(10, 11, 12);
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

	// Test scaling method.
    @Test
    public void simpleScale() {
        System.out.println("Running test: simpleScale.");
        double x = circle1.scale(3.0f);
        Assert.assertEquals(9.0f, x, delta);
    }

    // Test intersection method with two intersecting circles
    @Test
    public void simpleIntersect() {
        System.out.println("Running test: simpleIntersect.");
        Assert.assertTrue(circle1.intersects(circle2));
    }

    // Test intersection method with two non-intersecting circles
    @Test
    public void simpleNonIntersect() {
        System.out.println("Running test: simpleNonIntersect");
        Assert.assertFalse(circle1.intersects(circle3));
    }

}
