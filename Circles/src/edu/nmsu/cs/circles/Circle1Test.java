package edu.nmsu.cs.circles;

import java.beans.Transient;

/***
 * Example JUnit testing class for Circle1 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle1Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * tests - note that normally you would not have print statements in firstCircle JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

@@ -36,29 +38,129 @@ public void teardown()
	}

	//
	// Test a simple positive move
	// Test firstCircle simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMove.");
		System.out.println("Running Test: simpleMove.");
		p = circle1.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple negative move
	// Test firstCircle simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running test simpleMoveNeg.");
		System.out.println("Running Test: simpleMoveNeg.");
		p = circle1.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);

		Circle2 secondCircle = new Circle2(1, 2, 3);
		Point q;
		q = secondCircle.moveBy(1, 2);
		Assert.assertTrue(q.x == 2 && q.y == 4);
	}

	//
	//Tests when circles do not intersect
	//
	@Test
	public void doesNotIntersect()
	{
		System.out.println("Running Test: Circles do not intersect");

		Circle1 firstCircle = new Circle1(0, 50, 10);
		Circle1 secondCircle = new Circle1(0, 0, 5);
		Assert.assertFalse(firstCircle.intersects(secondCircle));
		Assert.assertFalse(secondCircle.intersects(firstCircle));

		firstCircle = new Circle1(1, 10, 2.99);
		secondCircle = new Circle1(1, 5, 5);
		Assert.assertFalse(firstCircle.intersects(secondCircle));
		Assert.assertFalse(secondCircle.intersects(firstCircle));
	}

	//
	// Tests single point intersection
	//
	@Test
	public void singleIntersection() {

		System.out.println("Running Test: singleIntersection.");

		Circle1 firstCircle = new Circle1(0, 0, 3);
		Circle1 secondCircle = new Circle1(5, 0, 2);

		Assert.assertFalse(firstCircle.intersects(secondCircle));
		Assert.assertFalse(secondCircle.intersects(firstCircle));
	}

	//
	// Tests two circles that are intersecting at two points
	//
	@Test
	public void twoPointIntersection() {

		System.out.println("Running Test: twoPointIntersection.");

		Circle1 firstCircle = new Circle1(0, 2, 1);
		Circle1 secondCircle = new Circle1(4, 2, 0);

		Assert.assertFalse(firstCircle.intersects(secondCircle));
		Assert.assertFalse(secondCircle.intersects(firstCircle));
	}

	//
	//Tests complete overlap
	//
	@Test
	public void completeOverlap()
	{
		System.out.println("Running Test: Circles overlap one another");

		Circle1 firstCircle = new Circle1(0, 50, 10);
		Circle1 secondCircle = new Circle1(0, 50, 10);

		Assert.assertTrue(firstCircle.intersects(secondCircle));
		Assert.assertTrue(secondCircle.intersects(firstCircle));
	}

	//
 	// Tests enlargening circle
 	//
 	@Test
 	public void enlarge() {

 		System.out.println("Running Test: enlarge.");

 		Circle1 circle1 = new Circle1(1, 1, 1);

 		circle1.scale(3.0);

 		Assert.assertTrue(circle1.radius == 3.0);
 	}

 	//
 	// Tests scaling a circle to a smaller circle
 	//
 	@Test
 	public void shrink() {

 		System.out.println("Running Test: shrink.");

 		Circle1 circle1 = new Circle1(1, 1, 1);

 		circle1.scale(0.5);

 		Assert.assertTrue(circle1.radius == 0.5);
 	}
