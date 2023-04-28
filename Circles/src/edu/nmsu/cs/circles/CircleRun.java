// even though package is being included, we are still
// getting error saying "cannot find symbol"
// on line 18, 19, 31, and 35
package edu.nmsu.cs.circles;

// attempted adding following line, did not work
//import Circle.java

/**
 * Sample tester for Circle1 and Circle2
 **/
public class CircleRun
{

	/**
	 * Accept command line args for two circles and then run their intersect() methods.
	 **/
	public static void main(String args[])
	{
		Circle1 c1;
		Circle2 c2;
		if (args.length != 6)
		{
			System.out.println("Error: args must be x1 y1 r1 x2 y2 r2");
			return;
		}
		try
		{
			double x, y, r;
			x = Double.parseDouble(args[0]);
			y = Double.parseDouble(args[1]);
			r = Double.parseDouble(args[2]);
			c1 = new Circle1(x, y, r);
			x = Double.parseDouble(args[3]);
			y = Double.parseDouble(args[4]);
			r = Double.parseDouble(args[5]);
			c2 = new Circle2(x, y, r);
		}
		catch (Exception e)
		{
			System.out.println("Bad arguments! " + e);
			e.printStackTrace();
			return;
		}
		System.out.println("Circle 1 says: " + c1.intersects(c2));
		System.out.println("Circle 2 says: " + c2.intersects(c1));
	}

}

public class Point
{
	double	x;

	double	y;
}


public abstract class Circle
{
	protected Point		center;

	protected double	radius;

	/**
	 * Create new circle
	 *
	 * @param x
	 *          is the x coordinate of the center
	 * @param y
	 *          is the y coordinate of the center
	 * @param radius
	 *          is the radius
	 **/
	public Circle(double x, double y, double radius)
	{
		center = new Point();
		center.x = x;
		center.y = y;
		this.radius = radius;
	}

	/**
	 * Change size of circle
	 *
	 * @param factor
	 *          is the scaling factor (0.8 make it 80% as big, 2.0 doubles its size)
	 * @return the new radius
	 **/
	public double scale(double factor)
	{
		radius = radius + factor;
		return radius;
	}

	/**
	 * Change position of circle relative to current position
	 *
	 * @param xOffset
	 *          is amount to change x coordinate
	 * @param yOffset
	 *          is amount to change y coordinate
	 * @return the new center coordinate
	 **/
	public Point moveBy(double xOffset, double yOffset)
	{
		center.x = center.x + xOffset;
		center.y = center.y + xOffset;
		return center;
	}

	/**
	 * Test if this circle intersects another circle.
	 *
	 * @param other
	 *          is the other circle
	 * @return True if the circles' outer edges intersect at all, False otherwise
	 **/
	public abstract boolean intersects(Circle other);

}

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		if (Math.abs(center.x - other.center.x) < radius &&
				Math.abs(center.y - other.center.y) < radius)
			return true;
		return false;
	}

}

public class Circle2 extends Circle
{

	public Circle2(double x, double y, double radius)
	{
		super(y, x, radius);
	}

	public boolean intersects(Circle other)
	{
		double d;
		d = Math.sqrt(Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2));
		if (d < radius)
			return true;
		else
			return false;
	}

}
