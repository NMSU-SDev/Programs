package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle2 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle2Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle2Test {
    // Data you need for each test case
    private Circle2 circle2;

    //
    // Stuff you want to do before each test case
    //
    @Before
    public void setup() {
        System.out.println("\nTest starting...");
        circle2 = new Circle2(1, 2, 3);
    }

    //
    // Stuff you want to do after each test case
    //
    @After
    public void teardown() {
        System.out.println("\nTest finished.");
    }

    //
    // Test a simple positive move
    //
    @Test
    public void simpleMove() {
        Point p;
        System.out.println("Running test simpleMove.");
        p = circle2.moveBy(1, 1);
        Assert.assertTrue(p.x == 2 && p.y == 3);
    }
    //
    // Test a fractional positive move
    //
    @Test
    public void simpleMoveFrac() {
        Point p;
        System.out.println("Running test simpleMoveFrac.");
        p = circle2.moveBy(1.5, 1.5);
        Assert.assertTrue(p.x == 2.5 && p.y == 3.5);
    }

    //
    // Test a fractional negative move
    //
    @Test
    public void simpleMoveNegFrac() {
        Point p;
        System.out.println("Running test simpleMoveNegFrac.");
        p = circle2.moveBy(-1.5, -1.5);
        Assert.assertTrue(p.x == -0.5 && p.y == 0.5);
    }

    //
    // Test a simple negative move
    //
    @Test
    public void simpleMoveNeg() {
        Point p;
        System.out.println("Running test simpleMoveNeg.");
        p = circle2.moveBy(-1, -1);
        Assert.assertTrue(p.x == 0 && p.y == 1);
    }

    //
    // Test a simple negative X move
    //
    @Test
    public void simpleMoveXNeg() {
        Point p;
        System.out.println("Running test simpleMoveXNeg.");
        p = circle2.moveBy(-1, 1);
        Assert.assertTrue(p.x == 0 && p.y == 3);
    }
    //
    // Test a simple negative X move
    //
    @Test
    public void simpleMoveYNeg() {
        Point p;
        System.out.println("Running test simpleMoveYNeg.");
        p = circle2.moveBy(1, -1);
        Assert.assertTrue(p.x == 2 && p.y == 1);
    }

    //
    // Test a simple Y fractional move
    //
    @Test
    public void simpleMoveYFrac() {
        Point p;
        System.out.println("Running test simpleMoveYFrac.");
        p = circle2.moveBy(1, 1.5);
        Assert.assertTrue(p.x == 2 && p.y == 3.5);
    }
    //
    // Test a simple X fractional move
    //
    @Test
    public void simpleMoveXFrac() {
        Point p;
        System.out.println("Running test simpleMoveXFrac.");
        p = circle2.moveBy(1.5, 1);
        Assert.assertTrue(p.x == 2.5 && p.y == 3);
    }

    //
    // Test with different values
    //
    @Test
    public void simpleMoveDiff() {
        Point p;
        System.out.println("Running test simpleMoveDiff.");
        p = circle2.moveBy(2, 3);
        Assert.assertTrue(p.x == 3 && p.y == 5);
    }
    //
    // Test negative with different values
    //
    @Test
    public void simpleMoveNegDiff() {
        Point p;
        System.out.println("Running test simpleMoveNegDiff.");
        p = circle2.moveBy(-2, -3);
        Assert.assertTrue(p.x == -1 && p.y == -1);
    }

    //
    // Test scaling with a positive value
    //
    @Test
    public void simpleScale() {
        Point p;
        System.out.println("Running test simpleScale.");
        circle2.scale(2);
        Assert.assertTrue(circle2.radius == 6);
    }

    //
    // Test scaling with a fractional value
    //
    @Test
    public void simpleScaleFrac() {
        Point p;
        System.out.println("Running test simpleScaleFrac.");
        circle2.scale(2.5);
        Assert.assertTrue(circle2.radius == 7.5);
    }

    //
    // Test scaling to make it smaller
    //
    @Test
    public void simpleScaleLess() {
        Point p;
        System.out.println("Running test simpleScaleLess.");
        circle2.scale(0.5);
        Assert.assertTrue(circle2.radius == 1.5);
    }

    //
    // Test intersect with a True result
    //
    @Test
    public void simpleIntersect() {
        System.out.println("Running test simpleIntersect.");
        Circle2 circleA = new Circle2(2, 1, 3);
        Circle2 circleB = new Circle2(10, 5, 8);
        Assert.assertTrue(circleA.intersects(circleB));
    }

    //
    // Test Intersect with a false result
    //
    @Test
    public void simpleIntersectFalse() {
        System.out.println("Running test simpleIntersectFalse.");
        Circle2 circleA = new Circle2(1, 1, 2);
        Circle2 circleB = new Circle2(6, 7, 2);
        Assert.assertTrue(!circleA.intersects(circleB));
    }

    //
    // Test Intersect with negative x and y when True
    //
    @Test
    public void simpleIntersectTrueNeg() {
        System.out.println("Running test simpleIntersectFalse.");
        Circle2 circleA = new Circle2(-2, -2, 3);
        Circle2 circleB = new Circle2(-6, -6, 3);
        Assert.assertTrue(circleA.intersects(circleB));

    }

    //
    // Test Intersect with negative x and y when False
    //
    @Test
    public void simpleIntersectFalseNeg() {
        System.out.println("Running test simpleIntersectFalse.");
        Circle2 circleA = new Circle2(-2, -2, 3);
        Circle2 circleB = new Circle2(-12, -6, 2);
        Assert.assertTrue(!circleA.intersects(circleB));
    }

    public static void main(String args[]) {
        try {
            org.junit.runner.JUnitCore.runClasses(
                    java.lang.Class.forName("Circle2Test"));
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

}
