package edu.nmsu.cs.circles;

/**
 * Implements a base abstract class for a circle. Everything except a intersects() method is
 * implemented here. Subclass must implement the intersects() method.
 **/
public abstract class Circle
{
	protected Point		center;

	protected double	radius;

	/**
	 * Create new circle
	 * 
	 * @param x
	 *          is the x coordinate of the center
	 * @param y
	 *          is the y coordinate of the center
	 * @param radius
	 *          is the radius
	 **/
	public Circle(double x, double y, double radius)
	{
		center = new Point();
		center.x = x;
		center.y = y;
		this.radius = radius;
	}

	/**
	 * Change size of circle
	 * 
	 * @param factor
	 *          is the scaling factor (0.8 make it 80% as big, 2.0 doubles its size)
	 * @return the new radius
	 **/
	public double scale(double factor)
	{
		radius *= factor;
		return radius;
	}

	/**
	 * Change position of circle relative to current position
	 * 
	 * @param xOffset
	 *          is amount to change x coordinate
	 * @param yOffset
	 *          is amount to change y coordinate
	 * @return the new center coordinate
	 **/
	public Point moveBy(double xOffset, double yOffset)
	{
		center.x = center.x + xOffset;
		center.y = center.y + xOffset;
		return center;
	}

	/**
	 * Test if this circle intersects another circle.
	 * 
	 * @param other
	 *          is the other circle
	 * @return True if the circles' outer edges intersect at all, False otherwise
	 **/
	public abstract boolean intersects(Circle other);

}
