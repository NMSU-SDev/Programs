package edu.nmsu.cs.circles;

public class Circle2 extends Circle {

	public Circle2(double x, double y, double radius) {
		super(x, y, radius);
	}

	public boolean intersects(Circle other) {
		double distSq = (center.x - other.center.x) * (center.x - other.center.x)
				+ (center.y - other.center.y) * (center.y - other.center.y);
		double radSumSq = (radius + other.radius) * (radius + other.radius);
		if (distSq == radSumSq)
			return true;
		return false;
	}

}
