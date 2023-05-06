package edu.nmsu.cs.circles;

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		double distanceBetweenCenters = Math.sqrt((center.x - other.center.x) * (center.x - other.center.x) + (center.y - other.center.y) * (center.y - other.center.y));
		if (distanceBetweenCenters <= this.radius + other.radius) 
			return true;
		return false;
//		if (Math.abs(center.x - other.center.x) < radius && Math.abs(center.y - other.center.y) < radius)
//			return true;
//		return false;
	}

}
