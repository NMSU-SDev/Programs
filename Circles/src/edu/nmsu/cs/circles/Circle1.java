package edu.nmsu.cs.circles;

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		if ( Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2) < Math.pow(radius-other.radius, 2)) //check if contained instruction says outer edge
			return false;
		else if ( Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2) <= Math.pow(radius+other.radius, 2))
			return true;
		return false;
	}

}
