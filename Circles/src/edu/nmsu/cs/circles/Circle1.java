package edu.nmsu.cs.circles;

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
		if (Math.abs(other.center.x - center.x) < other.radius &&
			Math.abs(other.center.y - center.y) < other.radius)
			return true;
		return false;
	}

}
