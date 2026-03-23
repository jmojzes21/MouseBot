package mousebot.components;

import java.awt.geom.Rectangle2D;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.LanguageManager;
import mousebot.main.Snap;
import mousebot.parameters.Param;

/**
 * Komponenta koja se odnosi na Bezierovu krivulju.
 *
 * @author josip
 */
public class Bezier extends Component {

  private static final long serialVersionUID = 854849185619461742L;

  public enum BezierType {

    QUADRATIC("params.beziertype.quadratic"), CUBIC("params.beziertype.cubic");

    String text;

    BezierType(String s) {
      text = LanguageManager.getString(s);
    }

    public String toString() {
      return text;
    }

  }

  public boolean initedCP1 = false, initedCP2 = false;

  public Bezier() {
    super(4, Param.BEZIER_TYPE, Param.ACCURACY_BEZIER);
  }

  public Bezier(double x, double y) {
    this();
    cp[0] = new Point(x, y);
    cp[1] = cp[0].clone();
    cp[2] = cp[0].clone();
    cp[3] = cp[0].clone();
  }

  public void render(Graphics g) {

    if (!initedCP1) {
      g.strokeLine(cp[0].x, cp[0].y, cp[1].x, cp[1].y);
      return;
    }

    BezierProcessor.render(g, getAccuracy(), getBezierType(), cp);
  }

  public void execute() throws Exception {
    BezierProcessor.execute(getAccuracy(), getBezierType(), cp);
  }

  public boolean intersects(Rectangle2D rect) {
    return BezierProcessor.intersect(rect, getAccuracy(), getBezierType(), cp);
  }

  public void initControlPoints() {

    Controller.getProject().cpManager.removeControlPoints();

    BezierType type = (BezierType) getParamValue(Param.BEZIER_TYPE);

    if (type == BezierType.QUADRATIC) {
      cp[2] = Point.getMiddlePoint(cp[0], cp[1]);
      initedCP1 = true;
    } else {
      cp[2] = new Point((cp[1].x + 2d * cp[0].x) / 3d, (cp[1].y + 2d * cp[0].y) / 3d);
      cp[3] = new Point((2d * cp[1].x + cp[0].x) / 3d, (2d * cp[1].y + cp[0].y) / 3d);
      initedCP1 = true;
      initedCP2 = true;
    }

  }

  public void createControlPoints() {

    BezierType type = (BezierType) getParamValue(Param.BEZIER_TYPE);

    if (type == BezierType.QUADRATIC) {
      if (!initedCP1) {
        cp[2] = Point.getMiddlePoint(cp[0], cp[1]);
        initedCP1 = true;
      }
      Controller.getProject().cpManager.createControlPoints(cp[0], cp[2], cp[1]);
    } else {
      if (!initedCP1) {
        cp[2] = new Point((cp[1].x + 2d * cp[0].x) / 3d, (cp[1].y + 2d * cp[0].y) / 3d);
        initedCP1 = true;
      }
      if (!initedCP2) {
        cp[3] = new Point((2d * cp[1].x + cp[0].x) / 3d, (2d * cp[1].y + cp[0].y) / 3d);
        initedCP2 = true;
      }
      Controller.getProject().cpManager.createControlPoints(cp[0], cp[2], cp[3], cp[1]);
    }
  }

  public void processRect() {
    BezierProcessor.processRect(getAccuracy(), getBezierType(), cp);
  }

  public Point getSnapped(Point p) {

    Point cp[] = {this.cp[0], this.cp[1]};

    for (int i = 0; i < cp.length; i++) {

      if (cp[i] == null || Controller.getProject().cpManager.containsCP(cp[i])) {continue;}
      if (Snap.process(cp[i], p)) {
        return cp[i];
      }
    }

    return null;
  }

  private double getAccuracy() {
    return (double) getParamValue(Param.ACCURACY_BEZIER);
  }

  private BezierType getBezierType() {
    return (BezierType) getParamValue(Param.BEZIER_TYPE);
  }

}