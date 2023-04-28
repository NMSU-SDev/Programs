package edu.nmsu.cs.circles;

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	// intersects function only works if it's within own radius's length,
	// does not account for other circle's radius
	// added lines to accommodate for this calculation error
	public boolean intersects(Circle other)
	{
	    double maxDist = radius + other.radius; // CORRECT

		// if (Math.abs(center.x - other.center.x) < radius &&
		//		Math.abs(center.y - other.center.y) < radius) WRONG

		if (Math.abs(center.x - other.center.x) < maxDist &&
		    Math.abs(center.y - other.center.y) < maxDist) // CORRECT
			return true;
		return false;
	}

}
