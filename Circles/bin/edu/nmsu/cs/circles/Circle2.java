package edu.nmsu.cs.circles;

public class Circle2 extends Circle
{

	public Circle2(double x, double y, double radius)
	{
	    // the order of parameters is not correct accoring to
	    // Circle constructor
		// super(y, x, radius); WRONG
	    super(x, y, radius); // CORRECT
	}

	// intersects function only works if it's within own radius's length,
	// does not account for other circle's radius
	// added lines to accommodate for this calculation error
	public boolean intersects(Circle other)
	{
		double d;
		double maxDist = radius + other.radius; // CORRECT

		d = Math.sqrt(Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2));

		//if (d < radius) WRONG
		if (d < maxDist) // CORRECT
			return true;
		else
			return false;
	}

}
