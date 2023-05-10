package edu.nmsu.cs.circles;

public class Circle2 extends Circle
{

	public Circle2(double x, double y, double radius)
	{
		super(y, x, radius);
	}

	public boolean intersects(Circle other)
	{
		double d, min, max;
		d = Math.sqrt(Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2));
		min = Math.abs(radius - other.radius);
		max = radius + other.radius;
		if (d < max && d > min)
			return true;
		else
			return false;
	}

}