
package edu.nmsu.cs.circles;

public class Circle2 extends Circle
{

	public Circle2(double x, double y, double radius)
	{
	//#4
	//x & y are reversed
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		double d;
		d = Math.sqrt(Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2));
		if (d < radius + other.radius)//#5 added other.radius
			return true;
		else
			return false;
	}

}