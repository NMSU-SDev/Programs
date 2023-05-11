package edu.nmsu.cs.circles;

public class Circle2 extends Circle
{

	public Circle2(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		// distance between the two centers
		double dist = Math.sqrt((Math.pow(this.center.x - other.center.x, 2)) + (Math.pow(this.center.y - other.center.y, 2)));
		// sum of the radii
		double radSum = this.radius + other.radius;
		
		// this.circle is inside of the other circle, but they're touching at a point
		// and vice-versa
		if (dist + this.radius == other.radius) {
			return true;
		}
		
		if (dist + other.radius == this.radius) {
			return true;
		}
		
		// this.circle is inside of the other circle, but they're not intersecting
		// and vice-versa
		if (dist + this.radius < other.radius) {
			return false;
		}
		
		if (dist + other.radius < this.radius) {
			return false;
		}
		
		// normal case
		if (dist <= radSum) {
			return true;
		}
		
		return false;
	}

}
