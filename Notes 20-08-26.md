# Class Reflection (20 Aug 2026)

## Topics

- Java Swing: JFrame, JPanel, and Graphics
- Anonymous Classes in Java
- Math in Computer Graphics: Triangles and Lines

## Notes

### Topic 01 — Java Swing: JFrame, JPanel, and Graphics

1. Basic structure: create a `JFrame`, insert a custom `JPanel` (or component) into it, then call `setVisible(true)`.
2. To draw custom shapes, extend `JPanel` and override `paintComponent(Graphics g)`.
3. The `Graphics` object is passed automatically to `paintComponent` — never retrieved manually.
4. Always call `super.paintComponent(g)` first, so the parent class can handle its own setup (clearing background, drawing borders, etc.).
5. `setColor()` acts like choosing a pen — every draw call after it uses that color until changed again.
6. `drawRect(x, y, width, height)` takes a top-left point plus width and height as its four parameters.

### Topic 02 — Anonymous Classes in Java

1. An anonymous class is a subclass defined inline, without a name — used when the subclass is only needed once.
2. Syntax simultaneously defines a subclass and creates an instance of it:

    X x = new X() {
        // @Override method bodies go here
    };

3. Related concepts briefly mentioned: nested classes, static nested classes, and inner classes.

### Topic 03 — Math in Computer Graphics: Triangles and Lines

1. Drawing a triangle from three line equations (not three points) requires finding pairwise intersection points by solving systems of equations.
2. Triangle inequality — any two sides must be greater than the third side:

$$
a + b > c, \quad b + c > a, \quad a + c > b
$$

3. Boundary method for generating valid triangle side lengths — pick $A$ and $B$ arbitrarily, then choose $C$ within the range:

$$
|A - B| < C < A + B
$$
