/**
* Test case to ensure that IllegalArgumentException is thrown when the radius is less than or equal to zero
*/
@Test(expected=IllegalArgumentException.class)
public void testCircle2NegativeRadius() {
   Circle2 circle2 = new Circle2(-2.0);
}

/**
* Test case to ensure that a Circle2 object is created with a positive radius and the getRadius method returns the same radius
*/
@Test
public void testCircle2PositiveRadius() {
   Circle2 circle2 = new Circle2(2.0);
   assertEquals(2.0, circle2.getRadius(), 0.001);
}
