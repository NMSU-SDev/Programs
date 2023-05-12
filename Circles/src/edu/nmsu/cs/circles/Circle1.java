package edu.nmsu.cs.circles;

import javax.lang.model.util.ElementScanner14;

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		double distSq = (center.x - other.center.x ) * (center.x - other.center.x ) + (center.y - other.center.y) * (center.y - other.center.y);
		double radSumSq = (radius + other.radius) * (radius + other.radius);
		if (distSq == radSumSq)
			return true;
		return false;
	}

}
