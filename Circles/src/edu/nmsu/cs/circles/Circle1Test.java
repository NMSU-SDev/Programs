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
    // Test constructor
    @Test
    public void testConstructor() {
        System.out.println("Running testConstructor.");
        Assert.assertEquals(1, circle1.getX());
        Assert.assertEquals(2, circle1.getY());
        Assert.assertEquals(3, circle1.getRadius());
    }
    // Test scale
    @Test
    public void testScale() {
        System.out.println("Running testScale.");
        circle1.scale(2);
        Assert.assertEquals(6, circle1.getRadius());
}
    // Test Intercepts (boundary value)
    @Test
    public void testIntercepts() {
        System.out.println("Running testIntercepts.");
        Circle1 other = new Circle1(4, 5, 2);
        Assert.assertTrue(circle1.intersects(other));
}
    // Test no intercepts (equivalence partitioning)
    @Test
    public void testNoIntercepts() {
        System.out.println("Running testNoIntercepts.");
        Circle1 other = new Circle1(10, 10, 2);
        Assert.assertFalse(circle1.intersects(other));
}
    // Test moveBy with Postitive Values
    @Test
    public void testMoveByPositive() {
        System.out.println("Running testMoveByPositive.");
        circle1.moveBy(3, 4);
        Assert.assertEquals(4, circle1.getX());
        Assert.assertEquals(6, circle1.getY());
}
    // Test moveBy with negative values
    @Test
    public void testMoveByNegative() {
        System.out.println("Running testMoveByNegative.");
        circle1.moveBy(-1, -1);
        Assert.assertEquals(0, circle1.getX());
        Assert.assertEquals(1, circle1.getY());
}



	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
