/**
* Test case to ensure that IllegalArgumentException is thrown when the radius is less than or equal to zero
*/
@Test(expected=IllegalArgumentException.class)
public void testCircle1NegativeRadius() {
   Circle1 circle1 = new Circle1(-2.0);
}

/**
* Test case to ensure that a Circle1 object is created with a positive radius and the getRadius method returns the same radius
*/
@Test
public void testCircle1PositiveRadius() {
   Circle1 circle1 = new Circle1(2.0);
   assertEquals(2.0, circle1.getRadius(), 0.001);
}

/**
* Test case to ensure that a Circle1 object is created with a radius greater than 10 and the getRadius method returns the same radius
*/
@Test
public void testCircle1LargeRadius() {
   Circle1 circle1 = new Circle1(15.0);
   assertEquals(15.0, circle1.getRadius(), 0.001);
}
