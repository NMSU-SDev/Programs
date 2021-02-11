package edu.nmsu.cs.circles;
import org.junit.*;

public class Circle2Test {

	 // Data you need for each test case
    private Circle2 circle;
    @Before
    public void setup()
    {
        circle = new Circle2(1, 2, 3);
    }
	
	//
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		p = circle.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		p = circle.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}	
	
    @Test //testing circles with some intersection
    public void intersectTest(){
        Circle2 c = new Circle2(4, 2, 4);
        boolean t = circle.intersects(c);
        Assert.assertTrue(t);
    }
    
    @Test //testing circles with no intersection
    public void noIntersectTest(){
        Circle2 c = new Circle2(50, 50, 40);
        boolean t = circle.intersects(c);
        Assert.assertFalse(t);
    }
    
    @Test //scale up the radius
    public void scaleUp(){
        circle.scale(5);
        Assert.assertTrue(circle.radius == 5);
    }
    
    @Test //scale down the radius
    public void scaleDown(){
        circle.scale(0.3);
        Assert.assertTrue(circle.radius == 0.3);
    }
}
