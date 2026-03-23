package mousebot.components;

import java.awt.geom.Rectangle2D;
import mousebot.components.Bezier.BezierType;
import mousebot.executing.Executor;
import mousebot.executing.MouseBot;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;

/**
 * Služi za lakši rad s komponentama čiji su sastavni dio bezierove krivulje (Bezierova krivulja, elipsa, tekst).
 *
 * @author josip
 */
public class BezierProcessor {

  public static void render(Graphics g, double accuracy, BezierType type, Point... cp) {
    try {
      bezierLoop((x, y, start, end) -> {
        if (start) {
          g.gc.beginPath();
        }
        g.lineTo(x, y, start);
        if (end) {
          g.gc.stroke();
        }
      }, accuracy, type, cp);
    } catch (Exception e) {}
  }

  public static void execute(double accuracy, BezierType type, Point... cp) throws Exception {
    bezierLoop((x, y, start, end) -> {
      Executor.check();
      MouseBot.move(x, y);
      if (start) {
        MouseBot.press();
      } else if (end) {
        MouseBot.release();
      }
    }, accuracy, type, cp);
  }

  public static boolean intersect(Rectangle2D rect, double accuracy, BezierType type, Point... cp) {
    Intersect i = new Intersect();
    try {
      bezierLoop((x, y, start, end) -> {
        if (!start) {
          if (rect.intersectsLine(i.px, i.py, x, y)) {
            i.intersect = true;
            throw new Exception();
          }
        }
        i.px = x;
        i.py = y;
      }, accuracy, type, cp);
    } catch (Exception e) {}
    return i.intersect;
  }

  public static void processRect(double accuracy, BezierType type, Point... cp) {
    try {
      bezierLoop((x, y, start, end) -> {
        RectFinder.getInstance().process(x, y);
      }, accuracy, type, cp);
    } catch (Exception e) {}
  }

  private interface Action {

    public void process(double x, double y, boolean start, boolean end) throws Exception;
  }

  private static class Intersect {

    public double px, py;
    public boolean intersect;
  }

  private static void bezierLoop(Action act, double accuracy, BezierType type, Point... cp) throws Exception {

    boolean start = true;
    double values[] = new double[2];

    for (double t = 0d; t < 1d; t += accuracy) {

      if (type == BezierType.QUADRATIC) {
        quadratic(t, cp[0], cp[1], cp[2], values);
      } else {
        cubic(t, cp[0], cp[1], cp[2], cp[3], values);
      }

      act.process(values[0], values[1], start, false);

      if (start) {
        start = false;
      }

    }

    if (type == BezierType.QUADRATIC) {
      quadratic(1d, cp[0], cp[1], cp[2], values);
    } else {
      cubic(1d, cp[0], cp[1], cp[2], cp[3], values);
    }

    act.process(values[0], values[1], false, true);

  }

  public static void quadratic(double t, Point start, Point end, Point cp1, double values[]) {
    values[0] = (start.x - 2d * cp1.x + end.x) * Math.pow(t, 2) + 2d * (cp1.x - start.x) * t + start.x;
    values[1] = (start.y - 2d * cp1.y + end.y) * Math.pow(t, 2) + 2d * (cp1.y - start.y) * t + start.y;
  }

  public static void cubic(double t, Point start, Point end, Point cp1, Point cp2, double values[]) {
    values[0] = Math.pow(1d - t, 3) * start.x +
        3d * Math.pow(1d - t, 2) * t * cp1.x +
        3d * (1d - t) * Math.pow(t, 2) * cp2.x +
        Math.pow(t, 3) * end.x;

    values[1] = Math.pow(1d - t, 3) * start.y +
        3d * Math.pow(1d - t, 2) * t * cp1.y +
        3d * (1d - t) * Math.pow(t, 2) * cp2.y +
        Math.pow(t, 3) * end.y;
  }

}