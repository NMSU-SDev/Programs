package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle2 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore circle2Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in firstCircle JUnit testing class; they are
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
	// Test firstCircle simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running Test: simpleMove.");
		p = circle2.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);

		Circle2 secondCircle = new Circle2(1, 2, 3);
		Point q;
		q = secondCircle.moveBy(1, 2);
		Assert.assertTrue(q.x == 2 && q.y == 4);
	}

	//
	// Test firstCircle simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running Test: simpleMoveNeg.");
		p = circle2.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	//Tests when circles do not intersect
	@Test
	public void doesNotIntersect()
	{
		System.out.println("Running Test: Circles do not intersect");

		Circle2 firstCircle = new Circle2(0, 50, 10);
		Circle2 secondCircle = new Circle2(0, 0, 5);
		Assert.assertFalse(firstCircle.intersects(secondCircle));
		Assert.assertFalse(secondCircle.intersects(firstCircle));

		firstCircle = new Circle2(1, 10, 2.99);
		secondCircle = new Circle2(1, 5, 5);
		Assert.assertTrue(firstCircle.intersects(secondCircle));
		Assert.assertTrue(secondCircle.intersects(firstCircle));
	}


	//
	// Tests Single point intersection
	//
	@Test
	public void singleIntersection() {

		System.out.println("Running Test: singleIntersection.");

		Circle2 firstCircle = new Circle2(0, 0, 3);
		Circle2 secondCircle = new Circle2(5, 0, 2);

		Assert.assertTrue(firstCircle.intersects(secondCircle));
		Assert.assertTrue(secondCircle.intersects(firstCircle));
	}

    	//
	// Tests two circles that are intersecting at two points
	//
	@Test
	public void twoPointIntersection() {

		System.out.println("Running Test: twoPointIntersection.");

		Circle2 firstCircle = new Circle2(0, 2, 1);
		Circle2 secondCircle = new Circle2(4, 2, 0);

		Assert.assertFalse(firstCircle.intersects(secondCircle));
		Assert.assertFalse(secondCircle.intersects(firstCircle));
	}

    //
	//Tests complete overlap
	//
	@Test
	public void completeOverlap()
	{
		System.out.println("Test: Circles overlap one another");

		Circle2 firstCircle = new Circle2(0, 50, 10);
		Circle2 secondCircle = new Circle2(0, 50, 10);

		Assert.assertTrue(firstCircle.intersects(secondCircle));
		Assert.assertTrue(secondCircle.intersects(firstCircle));
	}

		//
 	// Tests enlargening circle
 	//
 	@Test
 	public void enlarge() {

 		System.out.println("Running Test: enlarge.");

 		Circle2 circle2 = new Circle2(1, 1, 1);

 		circle2.scale(3.0);

 		Assert.assertTrue(circle2.radius == 3.0);
 	}

 	//
 	// Tests scaling a circle to a smaller circle
 	//
 	@Test
 	public void shrink() {

 		System.out.println("Running Test: shrink.");

 		Circle2 circle2 = new Circle2(1, 1, 1);

 		circle2.scale(0.5);

 		Assert.assertTrue(circle2.radius == 0.5);
 	}


	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("circle2Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
