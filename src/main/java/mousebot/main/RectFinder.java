package mousebot.main;

import mousebot.components.Point;

/**
 * Klasa za traženje krajnih točaka.
 *
 * @author josip
 *
 */
public class RectFinder {

  private static RectFinder bf = new RectFinder();

  // singleton design pattern
  public static RectFinder getInstance() {
    return bf;
  }

  private Point min, max;

  public void reset() {
    min = null;
    max = null;
  }

  public void process(double x, double y) {
    process(new Point(x, y));
  }

  public void process(Point... ps) {

    for (Point p : ps) {

      if (min == null) {
        min = p.clone();
        max = p.clone();
        continue;
      }

      if (p.x < min.x) {
        min.x = p.x;
      } else if (p.x > max.x) {
        max.x = p.x;
      }

      if (p.y < min.y) {
        min.y = p.y;
      } else if (p.y > max.y) {
        max.y = p.y;
      }

    }

  }

  public Point getStart() {
    return min;
  }

  public Point getEnd() {
    return max;
  }

  /**
   * Koordinate krajnih točaka ne smiju biti jednake.
   */
  public void fixZeroSize() {
    if (min.x == max.x) {
      min.x -= 0.1;
      max.x += 0.1;
    }
    if (min.y == max.y) {
      min.y -= 0.1;
      max.y += 0.1;
    }
  }

}