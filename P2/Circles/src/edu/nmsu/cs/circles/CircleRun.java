package edu.nmsu.cs.circles;

/**
 * Sample tester for Circle1 and Circle2
 **/
public class CircleRun
{

	/**
	 * Accept command line args for two circles and then run their intersect() methods.
	 **/
	public static void main(String args[])
	{
		Circle1 c1;
		Circle2 c2;
		if (args.length != 6)
		{
			System.out.println("Error: args must be x1 y1 r1 x2 y2 r2");
			return;
		}
		try
		{
			double x, y, r;
			x = Double.parseDouble(args[0]);
			y = Double.parseDouble(args[1]);
			r = Double.parseDouble(args[2]);
			c1 = new Circle1(x, y, r);
			x = Double.parseDouble(args[3]);
			y = Double.parseDouble(args[4]);
			r = Double.parseDouble(args[5]);
			c2 = new Circle2(x, y, r);
		}
		catch (Exception e)
		{
			System.out.println("Bad arguments! " + e);
			e.printStackTrace();
			return;
		}
		System.out.println("Circle 1 says: " + c1.intersects(c2));
		System.out.println("Circle 2 says: " + c2.intersects(c1));
	}

}
