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
		circle1 = new Circle1(1, 2, 3);
	}

	//
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
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
		p = circle1.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}
	
    @Test //scale up the radius
    public void scaleUp(){
        circle1.scale(5);
        Assert.assertTrue(circle1.radius == 5);
    }
    @Test //scale down the radius
    public void scaleDown(){
        circle1.scale(0.3);
        Assert.assertTrue(circle1.radius == 0.3);

}
    
    @Test //testing circles with some intersection
    public void intersectTest(){
        Circle1 c = new Circle1(4, 2, 4);
        boolean t = circle1.intersects(c);
        Assert.assertTrue(t);
    }
    
    @Test //testing circles with no intersection
    public void noIntersectTest(){
        Circle1 c = new Circle1(50, 50, 40);
        boolean t = circle1.intersects(c);
        Assert.assertFalse(t);
    }
}
