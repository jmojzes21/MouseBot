package mousebot.components;

import java.awt.geom.Rectangle2D;
import mousebot.components.Bezier.BezierType;
import mousebot.main.Graphics;
import mousebot.parameters.Param;

/**
 * Komponenta koja se odnosi na elipsu.
 *
 * @author josip
 */
public class Ellipse extends Shape {

  private static final long serialVersionUID = 6611843145935519426L;

  public Ellipse() {
    super(9, Param.ACCURACY_BEZIER);
  }

  public Ellipse(double x, double y) {
    this();
    cp[0] = new Point(x, y);
    for (int i = 1; i < cp.length; i++) {
      cp[i] = cp[0].clone();
    }
  }

  public void render(Graphics g) {
    BezierProcessor.render(g, getAccuracy(), BezierType.CUBIC, cp[1], cp[4], cp[2], cp[3]);
    BezierProcessor.render(g, getAccuracy(), BezierType.CUBIC, cp[5], cp[8], cp[6], cp[7]);
  }

  public void processRect() {
    BezierProcessor.processRect(getAccuracy(), BezierType.CUBIC, cp[1], cp[4], cp[2], cp[3]);
    BezierProcessor.processRect(getAccuracy(), BezierType.CUBIC, cp[5], cp[8], cp[6], cp[7]);
  }

  public void execute() throws Exception {
    BezierProcessor.execute(getAccuracy(), BezierType.CUBIC, cp[1], cp[4], cp[2], cp[3]);
    BezierProcessor.execute(getAccuracy(), BezierType.CUBIC, cp[5], cp[8], cp[6], cp[7]);
  }

  public void setEndPoint(double x, double y) {

    double h = y - cp[0].y;

    cp[1].x = x;
    cp[1].y = y - h / 2d;

    cp[2].x = cp[1].x;
    cp[2].y = cp[1].y + h * 2d / 3d;

    cp[3].y = cp[2].y;

    cp[4].y = cp[1].y;

    cp[5].y = y - h / 2d;

    cp[6].y = cp[5].y - h * 2d / 3d;

    cp[7].x = x;
    cp[7].y = cp[6].y;

    cp[8].set(cp[1]);

  }

  public boolean intersects(Rectangle2D rect) {
    boolean a = BezierProcessor.intersect(rect, getAccuracy(), BezierType.CUBIC, cp[1], cp[4], cp[2], cp[3]);
    if (a) {return true;}
    return BezierProcessor.intersect(rect, getAccuracy(), BezierType.CUBIC, cp[5], cp[8], cp[6], cp[7]);
  }

  public Point getSnapped(Point p) {
    return null;
  }

  private double getAccuracy() {
    return (double) getParamValue(Param.ACCURACY_BEZIER);
  }

}