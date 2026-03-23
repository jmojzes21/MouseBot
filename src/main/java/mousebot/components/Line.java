package mousebot.components;

import java.awt.geom.Rectangle2D;
import mousebot.executing.MouseBot;
import mousebot.main.Controller;
import mousebot.main.Graphics;
import mousebot.main.RectFinder;
import mousebot.parameters.Param;

/**
 * Komponenta koja se odnosi na liniju.
 *
 * @author josip
 */
public class Line extends Component {

  private static final long serialVersionUID = 3773202489238497194L;

  private double freezeLength, freezeAngle;

  public Line() {
    super(2);
    unfreeze();
  }

  public Line(double x, double y) {
    this();
    cp[0] = new Point(x, y);
    cp[1] = cp[0].clone();
  }

  public void render(Graphics g) {
    g.strokeLine(cp[0].x, cp[0].y, cp[1].x, cp[1].y);
  }

  public boolean intersects(Rectangle2D rect) {
    return rect.intersectsLine(cp[0].x, cp[0].y, cp[1].x, cp[1].y);
  }

  public void setStart(Point start) {
    this.cp[0] = start;
    updateParams();
  }

  public void setEnd(Point end) {
    this.cp[1] = end;
    updateParams();
  }

  public Point getEnd() {
    return cp[1];
  }

  private static final double C_HR = Math.toRadians(15d / 2d);
  private static final double C_VR = Math.PI / 2d - C_HR;
  private static final double C_12PI = 12d / Math.PI;
  private static final double C_PI24 = Math.PI / 24d;

  public void setEndPoint(double x, double y) {

    setEndPoint:
    {

      if (freezeAngle != -1 && freezeLength != -1) {
        cp[1].set(cp[0].x + freezeLength * Math.cos(Math.toRadians(freezeAngle)),
            cp[0].y + freezeLength * Math.sin(Math.toRadians(freezeAngle)));
        break setEndPoint;
      }

      if (freezeAngle != -1) {
        cp[1].set(x, (x - cp[0].x) * Math.tan(Math.toRadians(freezeAngle)) + cp[0].y);
        break setEndPoint;
      }

      if (Controller.getProject().shiftDown || freezeLength != -1) {

        double ra = (y - cp[0].y) / (x - cp[0].x);
        double a = Math.atan(ra);

        if (Math.abs(a) < C_HR && freezeLength == -1) {
          cp[1].set(x, cp[0].y);
        } else if (Math.abs(a) > C_VR && freezeLength == -1) {
          cp[1].set(cp[0].x, y);
        } else {

          if (a < 0) {
            a += Math.PI;
          }
          if (y < cp[0].y) {
            a += Math.PI;
          }
          if (a == 0 && x < cp[0].x) {
            a = Math.PI;
          }

          double a2 = Math.floor(C_12PI * (a + C_PI24)) / C_12PI;

          if (freezeLength != -1) {
            double a3 = Controller.getProject().shiftDown ? a2 : a;
            cp[1].set(cp[0].x + freezeLength * Math.cos(a3), cp[0].y + freezeLength * Math.sin(a3));
          } else {
            cp[1].set(x, (x - cp[0].x) * Math.tan(a2) + cp[0].y);
          }

        }

        break setEndPoint;
      }

      cp[1].set(x, y);

    }

    updateParams();

  }

  public void updateParams() {
    if (freezeLength == -1) {
      Controller.getParamsManager().setValue(Param.LENGTH, Point.length(cp[0], cp[1]));
    }
    if (freezeAngle == -1) {
      double a = -Point.getAngle(cp[0], cp[1]);
      Controller.getParamsManager().setValue(Param.ANGLE, Math.toDegrees(a));
    }
  }

  public void freezeLength(double v) {
    freezeLength = v;
  }

  public void freezeAngle(double v) {
    freezeAngle = v;
  }

  public void unfreeze() {
    freezeLength = -1;
    freezeAngle = -1;
  }

  public void createControlPoints() {
    Controller.getProject().cpManager.createControlPoints(cp[0], cp[1]);
  }

  public void processRect() {
    RectFinder.getInstance().process(cp[0], cp[1]);
  }

  public void execute() {
    MouseBot.move(cp[0]);
    MouseBot.press();
    MouseBot.move(cp[1]);
    MouseBot.release();
  }

}