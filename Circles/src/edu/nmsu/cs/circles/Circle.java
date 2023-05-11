package edu.nmsu.cs.circles;

public abstract class Circle {
    protected Point center;
    protected double radius;

    public Circle(double x, double y, double radius) {
        center = new Point(x, y);
        this.radius = radius;
    }

    public double scale(double factor) {
        radius *= factor;
        return radius;
    }

    public Point moveBy(double xOffset, double yOffset) {
        center.x += xOffset;
        center.y += yOffset;
        return center;
    }

    public abstract boolean intersects(Circle other);
}
