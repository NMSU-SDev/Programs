package edu.nmsu.cs.circles;

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{

		// Equation found at:
		//https://lucidar.me/en/mathematics/how-to-calculate-the-intersection-points-of-two-circles/

		// Distance between two points 
		double c1 = Math.abs(center.x - other.center.x);
		double c2 = Math.abs(center.y - other.center.y);
		double distance =  Math.sqrt(c1 * c1 + c2 * c2);

		if (distance == this.radius + other.radius) 
			// Circles intersect at a single point
			return true;
		else if (distance < this.radius + other.radius)
			// Circles intersect at two points
			return true;
		else if (distance == 0 && (this.radius == other.radius))
			// Circles are merged
			return true;
		// No intersection
		return false;
	}

}
